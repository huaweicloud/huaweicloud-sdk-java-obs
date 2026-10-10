 /**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.obs.services;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.security.LimitedTimeSecurityKey;
import com.obs.services.internal.security.SecurityKey;
import com.obs.services.internal.security.SecurityKeyBean;
import com.obs.services.internal.utils.JSONChange;
import com.obs.services.model.IdTokenFileConfig;
import com.obs.services.model.ISecurityKey;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * ID Token 凭证提供者
 *
 * <p>实现基于华为云 IAM 身份提供商的联邦认证流程，通过 ID Token 获取临时 AK/SK/SecurityToken。</p>
 *
 * <p><b>认证流程：</b></p>
 * <pre>{@code
 * 1. 从本地 JSON 文件读取配置（包含 ID Token 和认证参数）
 * 2. 调用 IAM 联邦认证接口获取 federation token
 * 3. 调用 IAM 临时凭证接口获取 AK/SK/SecurityToken
 * 4. 在凭证过期前自动刷新
 * }</pre>
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 简单方式（默认信任所有 SSL 证书，与 ObsClient 默认行为一致）
 * ObsClient obsClient = new ObsClient(
 *     new IdTokenCredentialsProvider("/path/to/id_token_file.json")
 * );
 *
 * // 完整方式
 * ObsClient obsClient = new ObsClient(
 *     new IdTokenCredentialsProvider(
 *         "/path/to/id_token_file.json",
 *         "https://iam.cn-north-4.myhuaweicloud.com",
 *         300,
 *         3
 *     ),
 *     new ObsConfiguration()
 * );
 *
 * // 自定义 SSL 配置（启用证书校验）
 * OkHttpClient secureClient = new OkHttpClient.Builder()
 *     .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
 *     .build();
 * ObsClient obsClient = new ObsClient(
 *     new IdTokenCredentialsProvider(
 *         "/path/to/id_token_file.json",
 *         "https://iam.cn-north-4.myhuaweicloud.com",
 *         300, 3, secureClient
 *     ),
 *     new ObsConfiguration()
 * );
 * }</pre>
 *
 * <p><b>线程安全：</b></p>
 * <p>此类是线程安全的，可在多线程环境中共享使用。凭证刷新操作会自动加锁，防止并发刷新。</p>
 *
 * @see IObsCredentialsProvider
 * @see LimitedTimeSecurityKey
 */
public class IdTokenCredentialsProvider implements IObsCredentialsProvider {

    private static final ILogger ILOG = LoggerBuilder.getLogger(IdTokenCredentialsProvider.class);

    /** Jackson ObjectMapper（线程安全，可全局共享） */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** 临时凭证有效期最小值：900 秒（15 分钟） */
    private static final int MIN_CREDENTIAL_EXPIRES_SECONDS = 900;

    /** 临时凭证有效期最大值：86400 秒（24 小时） */
    private static final int MAX_CREDENTIAL_EXPIRES_SECONDS = 86400;

    /** 临时凭证有效期默认值：86400 秒（24 小时） */
    private static final int DEFAULT_CREDENTIAL_EXPIRES_SECONDS = 86400;

    /** 华为云 IAM 公共端点 */
    private static final String DEFAULT_IAM_ENDPOINT = "https://iam.myhuaweicloud.com";

    /** 联邦认证接口路径 */
    private static final String FEDERATION_TOKEN_PATH = "/v3.0/OS-AUTH/id-token/tokens";

    /** 临时凭证接口路径 */
    private static final String CREDENTIAL_PATH = "/v3.0/OS-CREDENTIAL/securitytokens";

    /** 默认 OIDC Token 文件路径（CCE Pod 中的标准挂载路径） */
    private static final String DEFAULT_OIDC_TOKEN_FILE = "/var/run/secrets/tokens/oidc-token";

    /** 凭证刷新锁，保证并发安全 */
    private final Object refreshLock = new Object();

    /** 当前的限时凭证（volatile 保证多线程可见性） */
    private volatile LimitedTimeSecurityKey securityKey;

    /** ID Token 配置文件路径 */
    private final String configFilePath;

    /** 默认 IAM 端点（当配置文件未指定时使用） */
    private final String defaultIamEndpoint;

    /** 提前刷新秒数 */
    private final int refreshBeforeSeconds;

    /** 最大重试次数 */
    private final int maxRetryTimes;

    /** HTTP 客户端（volatile 允许代理配置动态更新） */
    private volatile OkHttpClient httpClient;

    /**
     * 简单构造函数
     *
     * <p>默认信任所有 SSL 证书，与 ObsClient 的默认行为一致
     * （ObsConfiguration.isValidateCertificate() 默认为 false）。</p>
     *
     * @param idTokenFilePath ID Token 配置文件路径（必填）
     * @throws IllegalArgumentException 如果 idTokenFilePath 为 null
     */
    public IdTokenCredentialsProvider(String idTokenFilePath) {
        this(idTokenFilePath, null, 300, 3, null);
    }

    /**
     * 完整构造函数
     *
     * <p>默认信任所有 SSL 证书，与 ObsClient 的默认行为一致
     * （ObsConfiguration.isValidateCertificate() 默认为 false）。</p>
     *
     * @param idTokenFilePath      ID Token 配置文件路径（必填）
     * @param defaultIamEndpoint   默认 IAM 端点（可选，为 null 时使用华为云公共端点）
     * @param refreshBeforeSeconds 提前刷新秒数（默认 300）
     * @param maxRetryTimes        最大重试次数（默认 3）
     * @throws IllegalArgumentException 如果 idTokenFilePath 为 null
     */
    public IdTokenCredentialsProvider(String idTokenFilePath, String defaultIamEndpoint,
            int refreshBeforeSeconds, int maxRetryTimes) {
        this(idTokenFilePath, defaultIamEndpoint, refreshBeforeSeconds, maxRetryTimes, null);
    }

    /**
     * 完整构造函数（支持自定义 HTTP 客户端）
     *
     * <p>当传入自定义的 OkHttpClient 时，SSL 证书校验策略由调用方控制。
     * 例如，可传入启用了证书校验的 OkHttpClient 以提高安全性。
     * 当 httpClient 为 null 时，使用默认的 OkHttpClient（信任所有 SSL 证书）。</p>
     *
     * @param idTokenFilePath      ID Token 配置文件路径（必填）
     * @param defaultIamEndpoint   默认 IAM 端点（可选，为 null 时使用华为云公共端点）
     * @param refreshBeforeSeconds 提前刷新秒数（默认 300）
     * @param maxRetryTimes        最大重试次数（默认 3）
     * @param httpClient           自定义 OkHttpClient（可选，为 null 时使用默认客户端）
     * @throws IllegalArgumentException 如果 idTokenFilePath 为 null
     */
    public IdTokenCredentialsProvider(String idTokenFilePath, String defaultIamEndpoint,
            int refreshBeforeSeconds, int maxRetryTimes, OkHttpClient httpClient) {
        this.configFilePath = Objects.requireNonNull(idTokenFilePath, "idTokenFilePath cannot be null");
        this.defaultIamEndpoint = defaultIamEndpoint != null ? defaultIamEndpoint : DEFAULT_IAM_ENDPOINT;
        this.refreshBeforeSeconds = refreshBeforeSeconds;
        this.maxRetryTimes = maxRetryTimes;
        this.httpClient = httpClient != null ? httpClient : buildHttpClient(null);
    }

    /**
     * 构建默认 OkHttpClient（信任所有 SSL 证书），可选配置代理
     *
     * <p>仅在未通过构造函数传入自定义 OkHttpClient 时使用。
     * 默认信任所有 SSL 证书，与 ObsClient 的默认行为一致
     * （ObsConfiguration.isValidateCertificate() 默认为 false）。</p>
     *
     * <p>如需启用 SSL 证书校验，请通过构造函数传入自定义的 OkHttpClient。</p>
     *
     * @param config 配置对象，为 null 时不配置代理
     * @return OkHttpClient 实例
     */
    private OkHttpClient buildHttpClient(IdTokenFileConfig config) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS);

        if (config != null && config.hasProxy()) {
            java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                new java.net.InetSocketAddress(config.getProxyHost(), config.getProxyPort()));
            builder.proxy(proxy);

            if (config.getProxyUsername() != null && !config.getProxyUsername().isEmpty()) {
            builder.proxyAuthenticator((route, response) -> {
                String credential = okhttp3.Credentials.basic(
                    config.getProxyUsername(),
                    config.getProxyPassword() != null ? config.getProxyPassword() : "");
                return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
            });
            }
        }

        // Configure SSL to trust all certificates by default, consistent with ObsClient behavior
        try {
            javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new javax.net.ssl.TrustManager[] { TRUST_ALL_MANAGER },
                new java.security.SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), TRUST_ALL_MANAGER);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            if (ILOG.isErrorEnabled()) {
                ILOG.error("Failed to configure SSL context for IdToken HTTP client", e);
            }
        }

        return builder.build();
    }

    /** 信任所有证书的 TrustManager，仅用于默认 HTTP 客户端构建，与 ObsClient 默认行为一致 */
    private static final javax.net.ssl.X509TrustManager TRUST_ALL_MANAGER =
        new javax.net.ssl.X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }
        };

    @Override
    public void setSecurityKey(ISecurityKey securityKey) {
        throw new UnsupportedOperationException("IdTokenCredentialsProvider class does not support this method");
    }

    /**
     * 获取安全凭证
     *
     * <p>如果凭证不存在或即将过期（当前时间 + refreshBeforeSeconds >= 过期时间），
     * 会自动触发刷新。</p>
     *
     * @return 当前有效的安全凭证
     */
    @Override
    public ISecurityKey getSecurityKey() {
        if (needsRefresh()) {
            synchronized (refreshLock) {
                if (needsRefresh()) {
                    refreshCredentials();
                }
            }
        }
        return securityKey;
    }

    /**
     * 手动刷新凭证
     *
     * <p>强制刷新凭证，忽略当前凭证的有效期状态。
     * 下次调用 getSecurityKey() 时会获取新的凭证。</p>
     */
    public void refresh() {
        synchronized (refreshLock) {
            refreshCredentials();
        }
    }

    /**
     * 执行凭证刷新
     *
     * <p>完整流程：</p>
     * <ol>
     *   <li>从 JSON 文件读取配置</li>
     *   <li>调用联邦认证接口获取 federation token</li>
     *   <li>调用临时凭证接口获取 AK/SK/SecurityToken</li>
     *   <li>封装为 LimitedTimeSecurityKey</li>
     * </ol>
     */
    private void refreshCredentials() {
        int times = 0;
        do {
            try {
                // 1. 从 JSON 文件读取配置
                IdTokenFileConfig config = readConfigFromFile(configFilePath);

                // 1.1 如果配置中包含代理信息，重建 HTTP 客户端
                if (config.hasProxy()) {
                    OkHttpClient newClient = buildHttpClient(config);
                    this.httpClient = newClient;
                }

                // 2. 获取联邦认证 Token
                String federationToken = obtainFederationToken(config);

                // 3. 获取临时 AK/SK/SecurityToken
                SecurityKeyBean credentials = obtainTemporaryCredentials(federationToken, config);

                // 4. 解析过期时间
                Date expiryDate = parseExpiryDate(credentials.getExpiresDate());

                // 5. 封装为限时凭证
                this.securityKey = new LimitedTimeSecurityKey(
                    credentials.getAccessKey(),
                    credentials.getSecretKey(),
                    credentials.getSecurityToken(),
                    expiryDate
                );

                if (ILOG.isDebugEnabled()) {
                    ILOG.debug("IdToken credentials refreshed successfully, expires at: " + expiryDate);
                }
                return;
            } catch (ObsException e) {
                // 401/403 errors should not be retried
                if (isNonRetriableError(e)) {
                    throw e;
                }
                times++;
                ILOG.warn("IdToken credentials refresh failed. times: " + times
                    + "; maxRetryTimes: " + maxRetryTimes, e);

                if (times > this.maxRetryTimes) {
                    ILOG.error("IdToken credentials refresh failed after " + times + " retries.", e);
                    throw e;
                }

                // Exponential backoff: 1s, 2s, 4s, ... max 30s
                long delayMs = Math.min((1L << (times - 1)) * 1000, 30000);
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new ObsException("IdToken credentials refresh interrupted", ie);
                }
            } catch (Exception e) {
                times++;
                ILOG.warn("IdToken credentials refresh failed. times: " + times
                    + "; maxRetryTimes: " + maxRetryTimes, e);

                if (times > this.maxRetryTimes) {
                    ILOG.error("IdToken credentials refresh failed after " + times + " retries.", e);
                    throw new ObsException("Failed to refresh IdToken credentials: " + e.getMessage(), e);
                }

                // 指数退避：1s, 2s, 4s, ... 最大 30s
                long delayMs = Math.min((1L << (times - 1)) * 1000, 30000);
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new ObsException("IdToken credentials refresh interrupted", ie);
                }
            }
        } while (times <= maxRetryTimes);
    }

    /**
     * 从 JSON 文件读取配置
     *
     * @param filePath 配置文件路径
     * @return 解析后的配置对象
     * @throws ObsException 如果文件读取或解析失败，或必填字段校验不通过
     */
    private IdTokenFileConfig readConfigFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new ObsException("IdToken config file not found: " + filePath);
        }

        String content;
        try {
            content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ObsException("Failed to read IdToken config file: " + filePath, e);
        }

        IdTokenFileConfig config;
        try {
            ObjectMapper mapper = OBJECT_MAPPER;
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            config = mapper.readValue(content, IdTokenFileConfig.class);
        } catch (IOException e) {
            throw new ObsException("Failed to parse IdToken config file: " + filePath, e);
        }

        validateConfig(config);
        return config;
    }

    /**
     * 校验配置参数
     *
     * <p>校验顺序：</p>
     * <ol>
     *   <li>id_token 和 oidc_token_file 互斥校验</li>
     *   <li>通过 4 级优先级解析 id_token，所有来源均不可用时抛出异常</li>
     *   <li>idp_id 非空校验</li>
     *   <li>project 和 domain 互斥校验</li>
     *   <li>project/domain 均可选，均不指定时获取 unscoped token</li>
     *   <li>日志脱敏：仅打印 Token 前缀（前 20 字符）</li>
     * </ol>
     *
     * @param config 配置对象
     * @throws ObsException 如果必填字段校验不通过
     */
    private void validateConfig(IdTokenFileConfig config) {
        // 1. id_token 和 oidc_token_file 互斥校验
        boolean hasIdToken = config.getIdToken() != null && !config.getIdToken().trim().isEmpty();
        boolean hasOidcTokenFile = config.getOidcTokenFile() != null && !config.getOidcTokenFile().trim().isEmpty();
        if (hasIdToken && hasOidcTokenFile) {
            throw new ObsException("id_token and oidc_token_file are mutually exclusive");
        }

        // 2. 通过 4 级优先级解析 id_token
        String resolvedToken = resolveIdToken(config);
        if (resolvedToken == null || resolvedToken.trim().isEmpty()) {
            throw new ObsException("Unable to resolve id_token from any source (config id_token, oidc_token_file, or default path)");
        }

        // 3. 校验 idp_id 非空
        if (config.getIdpId() == null || config.getIdpId().trim().isEmpty()) {
            throw new ObsException("idp_id cannot be empty");
        }

        // 4. project 和 domain 互斥校验
        boolean hasProject = config.hasProject();
        boolean hasDomain = config.hasDomain();

        if (hasProject && hasDomain) {
            throw new ObsException("project and domain are mutually exclusive");
        }

        // 5. project/domain 均可选，不指定时获取 unscoped token

        // 6. 日志脱敏：仅打印 Token 前缀
        if (ILOG.isDebugEnabled()) {
            String prefix = resolvedToken.length() > 20 ? resolvedToken.substring(0, 20) + "..." : resolvedToken;
            ILOG.debug("IdToken loaded, length: " + resolvedToken.length() + ", prefix: " + prefix);
        }
    }

    /**
     * 解析 id_token
     *
     * <p>优先级（高到低）：</p>
     * <ol>
     *   <li>配置文件中的 id_token 字段</li>
     *   <li>配置文件中的 oidc_token_file 指定的文件内容（纯文本 JWT，自动 strip 空白字符）</li>
     *   <li>默认路径 /var/run/secrets/tokens/oidc-token（CCE Pod 标准挂载路径）</li>
     *   <li>以上来源均不可用时返回 null</li>
     * </ol>
     *
     * <p>注意：调用此方法前应先校验 id_token 和 oidc_token_file 互斥。</p>
     *
     * @param config 从配置文件解析的配置对象
     * @return id_token 字符串，所有来源均不可用时返回 null
     */
    private String resolveIdToken(IdTokenFileConfig config) {
        // 优先级 1：配置文件中的 id_token 字段
        if (config.getIdToken() != null && !config.getIdToken().trim().isEmpty()) {
            return config.getIdToken();
        }

        // 优先级 2：oidc_token_file 指定的文件内容
        if (config.getOidcTokenFile() != null && !config.getOidcTokenFile().trim().isEmpty()) {
            String token = readTokenFromFile(config.getOidcTokenFile());
            if (token != null && !token.trim().isEmpty()) {
                return token.trim();
            }
        }

        // 优先级 3：默认路径 /var/run/secrets/tokens/oidc-token
        String defaultToken = readTokenFromFile(DEFAULT_OIDC_TOKEN_FILE);
        if (defaultToken != null && !defaultToken.trim().isEmpty()) {
            return defaultToken.trim();
        }

        // 所有来源均不可用
        return null;
    }

    /**
     * 从文件读取 Token 字符串
     *
     * @param filePath 文件路径
     * @return 文件内容字符串，读取失败时返回 null
     */
    private String readTokenFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            if (ILOG.isDebugEnabled()) {
                ILOG.debug("OIDC token file not found: " + filePath);
            }
            return null;
        }

        try {
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            return content.trim();
        } catch (IOException e) {
            if (ILOG.isWarnEnabled()) {
                ILOG.warn("Failed to read OIDC token file: " + filePath, e);
            }
            return null;
        }
    }

    /**
     * 获取联邦认证 Token
     *
     * <p>调用 IAM 联邦认证接口，使用 ID Token 进行身份认证。</p>
     *
     * @param config 认证配置
     * @return 联邦认证 Token（来自 X-Subject-Token 响应头）
     * @throws IOException 如果网络请求失败
     * @throws ObsException 如果认证失败
     */
    private String obtainFederationToken(IdTokenFileConfig config) throws IOException {
        String iamEndpoint = resolveIamEndpoint(config);
        String url = iamEndpoint + FEDERATION_TOKEN_PATH;

        // 构建请求体
        String jsonBody = buildFederationRequestBody(config);

        if (ILOG.isDebugEnabled()) {
            ILOG.debug("Requesting federation token from: " + url);
        }

        RequestBody body = RequestBody.create(
            jsonBody, MediaType.get("application/json;charset=utf8"));
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json;charset=utf8")
            .addHeader("X-Idp-Id", config.getIdpId())
            .addHeader("User-Agent", "obs-sdk-java")
            .build();

        Call call = httpClient.newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                handleHttpError("Federation token request failed", response.code(), responseBody);
            }

            String federationToken = response.header("X-Subject-Token");
            if (federationToken == null || federationToken.isEmpty()) {
                throw new ObsException("Federation token not found in response header X-Subject-Token");
            }

            if (ILOG.isDebugEnabled()) {
                ILOG.debug("Federation token obtained successfully");
            }

            return federationToken;
        }
    }

    /**
     * 构建联邦认证请求体
     *
     * <p>华为云 IAM OIDC 联邦认证请求体格式：</p>
     * <pre>{@code
     * {
     *     "auth": {
     *         "id_token": {
     *             "id": "eyJhbGciOiJSU..."
     *         },
     *         "scope": {
     *             "project": {"id": "..."}  // 或 "domain": {"id": "..."}
     *         }
     *     }
     * }
     * }</pre>
     *
     * <p>scope 为可选字段，不传时获取 unscoped token。</p>
     *
     * @param config 认证配置
     * @return JSON 格式的请求体
     * @throws ObsException 如果 JSON 序列化失败
     */
    private String buildFederationRequestBody(IdTokenFileConfig config) {
        ObjectNode root = OBJECT_MAPPER.createObjectNode();
        ObjectNode auth = root.putObject("auth");
        ObjectNode idTokenNode = auth.putObject("id_token");
        String resolvedToken = resolveIdToken(config);
        idTokenNode.put("id", resolvedToken);

        if (config.hasProject()) {
            ObjectNode scope = auth.putObject("scope");
            ObjectNode project = scope.putObject("project");
            if (config.getProjectId() != null && !config.getProjectId().trim().isEmpty()) {
                project.put("id", config.getProjectId());
            }
            if (config.getProjectName() != null && !config.getProjectName().trim().isEmpty()) {
                project.put("name", config.getProjectName());
            }
        } else if (config.hasDomain()) {
            ObjectNode scope = auth.putObject("scope");
            ObjectNode domain = scope.putObject("domain");
            if (config.getDomainId() != null && !config.getDomainId().trim().isEmpty()) {
                domain.put("id", config.getDomainId());
            }
            if (config.getDomainName() != null && !config.getDomainName().trim().isEmpty()) {
                domain.put("name", config.getDomainName());
            }
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new ObsException("Failed to serialize federation request body", e);
        }
    }

    /**
     * 获取临时凭证
     *
     * <p>使用联邦认证 Token 调用 IAM 临时凭证接口，获取 AK/SK/SecurityToken。</p>
     *
     * @param federationToken 联邦认证 Token
     * @param config          认证配置
     * @return 临时凭证（包含 AK、SK、SecurityToken、过期时间）
     * @throws IOException 如果网络请求失败
     * @throws ObsException 如果获取凭证失败
     */
    private SecurityKeyBean obtainTemporaryCredentials(String federationToken,
            IdTokenFileConfig config) throws IOException {
        String iamEndpoint = resolveIamEndpoint(config);
        String url = iamEndpoint + CREDENTIAL_PATH;

        int expiresSeconds = normalizeCredentialExpiresSeconds(config.getCredentialExpiresSeconds());

        // 构建请求体
        String jsonBody = buildCredentialRequestBody(expiresSeconds);

        if (ILOG.isDebugEnabled()) {
            ILOG.debug("Requesting temporary credentials from: " + url
                + ", expires_seconds: " + expiresSeconds);
        }

        RequestBody body = RequestBody.create(
            jsonBody, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json;charset=utf8")
            .addHeader("X-Auth-Token", federationToken)
            .build();

        Call call = httpClient.newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                handleHttpError("Temporary credentials request failed", response.code(), responseBody);
            }

            String responseBodyString;
            if (response.body() == null || response.body().contentLength() == 0) {
                throw new ObsException("Temporary credentials response body is null");
            }

            responseBodyString = response.body().string();
            SecurityKey securityKeyObj = (SecurityKey) JSONChange.jsonToObj(new SecurityKey(), responseBodyString);

            if (securityKeyObj == null || securityKeyObj.getBean() == null) {
                throw new ObsException("Failed to parse temporary credentials response");
            }

            SecurityKeyBean bean = securityKeyObj.getBean();
            if (bean.getAccessKey() == null || bean.getSecretKey() == null
                    || bean.getSecurityToken() == null) {
                throw new ObsException("Temporary credentials response missing required fields");
            }

            return bean;
        }
    }

    /**
     * 构建临时凭证请求体
     *
     * @param durationSeconds 凭证有效期（秒）
     * @return JSON 格式的请求体
     * @throws ObsException 如果 JSON 序列化失败
     */
    private String buildCredentialRequestBody(int durationSeconds) {
        ObjectNode root = OBJECT_MAPPER.createObjectNode();
        ObjectNode auth = root.putObject("auth");
        ObjectNode identity = auth.putObject("identity");
        identity.putArray("methods").add("token");
        identity.putObject("token").put("duration_seconds", durationSeconds);

        try {
            return OBJECT_MAPPER.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new ObsException("Failed to serialize credential request body", e);
        }
    }

    /**
     * 解析过期时间字符串
     *
     * @param expiresDateStr 过期时间字符串（ISO 8601 格式）
     * @return 过期时间 Date 对象
     * @throws ObsException 如果解析失败
     */
    private Date parseExpiryDate(String expiresDateStr) {
        if (expiresDateStr == null || expiresDateStr.isEmpty()) {
            throw new ObsException("Expires date is null or empty in temporary credentials response");
        }

        // IAM returns ISO 8601 format: 2024-01-15T09:30:00.000Z
        // Try multiple format patterns for compatibility
        String[] patterns = {
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss"
        };

        for (String pattern : patterns) {
            try {
                DateFormat df = new SimpleDateFormat(pattern);
                df.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                return df.parse(expiresDateStr);
            } catch (ParseException e) {
                // Try next pattern
            }
        }

        throw new ObsException("Failed to parse expires date: " + expiresDateStr);
    }

    /**
     * 校验并修正 credential_expires_seconds 值
     *
     * @param credentialExpiresSeconds 原始值（可能为 null 或超出范围）
     * @return 修正后的有效值
     */
    private int normalizeCredentialExpiresSeconds(Integer credentialExpiresSeconds) {
        if (credentialExpiresSeconds == null || credentialExpiresSeconds < MIN_CREDENTIAL_EXPIRES_SECONDS) {
            return DEFAULT_CREDENTIAL_EXPIRES_SECONDS;
        }
        if (credentialExpiresSeconds > MAX_CREDENTIAL_EXPIRES_SECONDS) {
            return MAX_CREDENTIAL_EXPIRES_SECONDS;
        }
        return credentialExpiresSeconds;
    }

    /**
     * 解析 IAM 端点
     *
     * <p>优先级：配置文件 > 构造函数参数 > 华为云公共端点</p>
     *
     * @param config 配置对象
     * @return IAM 端点地址
     */
    private String resolveIamEndpoint(IdTokenFileConfig config) {
        if (config.getIamEndpoint() != null && !config.getIamEndpoint().trim().isEmpty()) {
            return config.getIamEndpoint();
        }
        return this.defaultIamEndpoint;
    }

    /**
     * 判断是否需要刷新凭证
     *
     * @return true 表示需要刷新，false 表示当前凭证有效
     */
    private boolean needsRefresh() {
        if (securityKey == null) {
            return true;
        }
        long refreshBeforeMs = refreshBeforeSeconds * 1000L;
        return (System.currentTimeMillis() + refreshBeforeMs) >= securityKey.getExpiryDate().getTime();
    }

    /**
     * 判断错误是否不可重试
     *
     * @param e ObsException
     * @return true 表示不可重试（认证/授权/参数错误）
     */
    private boolean isNonRetriableError(ObsException e) {
        String message = e.getMessage();
        if (message == null) {
            return false;
        }
        // 401/403 are authentication/authorization errors - don't retry
        return message.contains("statusCode: 401") || message.contains("statusCode: 403");
    }

    /**
     * 处理 HTTP 错误响应
     *
     * @param context       错误上下文描述
     * @param statusCode    HTTP 状态码
     * @param responseBody  响应体内容
     * @throws ObsException 包含错误信息的异常
     */
    private void handleHttpError(String context, int statusCode, String responseBody) {
        String message = context + ", statusCode: " + statusCode;
        if (responseBody != null && !responseBody.isEmpty()) {
            message += ", body: " + responseBody;
        }
        ILOG.error(message);

        // 401/403 认证/授权失败不重试
        if (statusCode == 401 || statusCode == 403) {
            throw new ObsException(message);
        }

        throw new ObsException(message);
    }

}

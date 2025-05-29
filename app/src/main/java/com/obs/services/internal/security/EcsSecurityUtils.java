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

package com.obs.services.internal.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.AbstractClient;
import com.obs.services.internal.Constants;
import com.obs.services.internal.utils.PropertyManager;
import com.obs.services.model.HttpMethodEnum;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class EcsSecurityUtils {
    /**
     * Default root url for the openstack metadata apis.
     */
    private static final String OPENSTACK_METADATA_ROOT = "/openstack/latest";
    /**
     * Default root url for the metadata apis.
     */
    private static final String METADATA_ROOT = "/meta-data/latest";

    /**
     * Default endpoint for the ECS Instance Metadata Service.
     */
    private static final String ECS_METADATA_SERVICE_URL = PropertyManager.getInstance(Constants.PROPERTY_NAME_OBS)
            .getFormattedString("ecs.metadata.service.url");

    private static final String EC2_METADATA_SERVICE_OVERRIDE_URL = "ecsMetadataServiceOverrideEndpoint";

    private static final long HTTP_CONNECT_TIMEOUT_VALUE = 30 * 1000;

    private static OkHttpClient httpClient = new OkHttpClient.Builder().followRedirects(false)
            .retryOnConnectionFailure(true).cache(null)
            .connectTimeout(HTTP_CONNECT_TIMEOUT_VALUE, TimeUnit.MILLISECONDS)
            .writeTimeout(HTTP_CONNECT_TIMEOUT_VALUE, TimeUnit.MILLISECONDS)
            .readTimeout(HTTP_CONNECT_TIMEOUT_VALUE, TimeUnit.MILLISECONDS).build();

    private static final String METADATA_TOKEN_HEADER_KEY = "X-Metadata-Token";
    private static final String METADATA_TOKEN_TTL = METADATA_TOKEN_HEADER_KEY + "-Ttl-Seconds";
    public static final int DEFAULT_METADATA_TOKEN_TTL_SECONDS = 21600;
    private static final String METADATA_TOKEN_RESOURCE_PATH = METADATA_ROOT + "/api/token";
    private static final String OPENSTACK_SECURITY_KEY_RESOURCE_PATH = OPENSTACK_METADATA_ROOT + "/securitykey";
    private static final ILogger ILOG = LoggerBuilder.getLogger(AbstractClient.class);
    /**
     * Returns the temporary security credentials (access, secret,
     * securitytoken, and expires_at) associated with the IAM roles on the
     * instance.
     */
    public static String getSecurityKeyInfoWithDetail() throws IOException {
        return getSecurityKeyInfoWithDetail(DEFAULT_METADATA_TOKEN_TTL_SECONDS);
    }
    
    public static String getSecurityKeyInfoWithDetail(int metadataTokenTTLSeconds) throws IOException {
        // try get metadataToken
        String metadataApiToken = getMetadataApiToken(metadataTokenTTLSeconds);
        String securityKeyResourcePath = getEndpointForECSMetadataService() + OPENSTACK_SECURITY_KEY_RESOURCE_PATH;
        if(metadataApiToken.isEmpty()) {
            // failed to get metadataToken(404 or 405), use IMDSv1
            return getResourceWithDetail(securityKeyResourcePath);
        } else {
            // succeeded to get metadataToken(2xx), use IMDSv2
            return getResourceWithDetailWithMetaDataToken(securityKeyResourcePath, metadataApiToken);
        }
    }

    /**
     * Returns the host address of the ECS Instance Metadata Service.
     */
    public static String getEndpointForECSMetadataService() {
        String overridUrl = System.getProperty(EC2_METADATA_SERVICE_OVERRIDE_URL);
        return overridUrl != null ? overridUrl : ECS_METADATA_SERVICE_URL;
    }

    /**
     * Get resource and return contents from metadata service with the specify
     * path.
     */
    private static String getResourceWithDetail(String endpoint) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.header("Accept", "*/*");
        Request request = builder.url(endpoint).get().build();
        Call c = httpClient.newCall(request);
        Response res = null;
        String content = "";
        try {
            res = c.execute();
            String header = "";
            if (res.headers() != null) {
                header = res.headers().toString();
            }
            if (res.body() != null) {
                content = res.body().string();
            }

            if (!(res.code() >= 200 && res.code() < 300)) {
                String errorMessage = "Get securityKey from ECS failed, Code : " + res.code() + "; Headers : " + header
                        + "; Content : " + content;
                throw new IllegalArgumentException(errorMessage);
            }

            return content;
        } finally {
            if (res != null) {
                res.close();
            }
        }
    }

    private static String getMetadataApiToken(int metadataTokenTTLSeconds) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(METADATA_TOKEN_TTL, String.valueOf(metadataTokenTTLSeconds));
        ECSResult ecsResult =
            executeEcsRequest(getEndpointForECSMetadataService() + METADATA_TOKEN_RESOURCE_PATH
                , headers, HttpMethodEnum.PUT, "", null);
        if(ecsResult.code == 404 || ecsResult.code == 405) {
            ILOG.debug(METADATA_TOKEN_HEADER_KEY + " not supported," + ecsResult);
            return "";
        } else if (!(ecsResult.code >= 200 && ecsResult.code < 300)) {
            String errorMessage = "Get " + METADATA_TOKEN_HEADER_KEY+ " with " +
                METADATA_TOKEN_TTL + ":" + metadataTokenTTLSeconds
                + " from ECS failed," + ecsResult;
            ILOG.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        } else {
            ILOG.debug(METADATA_TOKEN_HEADER_KEY + " refreshed succeeded.");
            return ecsResult.content;
        }
    }

    private static String getResourceWithDetailWithMetaDataToken(String endpoint, String metadataApiToken) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(METADATA_TOKEN_HEADER_KEY, metadataApiToken);
        ECSResult ecsResult = executeEcsRequest(endpoint, headers, HttpMethodEnum.GET, "", null);
        // if not 2xx, throw exception
        if (!(ecsResult.code >= 200 && ecsResult.code < 300)) {
            String errorMessage = "Get securityKey by " + METADATA_TOKEN_HEADER_KEY +
                " from ECS failed," + ecsResult;
            ILOG.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        ILOG.debug("getResourceWithDetailWithMetaDataToken succeeded.");
        return ecsResult.content;
    }

    private static ECSResult executeEcsRequest(String url, Map<String, String> headers,
            HttpMethodEnum httpMethod, String body, MediaType contentType) throws IOException, IllegalArgumentException {
        Request.Builder builder = new Request.Builder();
        builder.header("Accept", "*/*");
        headers.forEach(builder::header);
        Request request;
        if (httpMethod == HttpMethodEnum.PUT) {
            request = builder.url(url).put(RequestBody.create(body, contentType)).build();
        } else {
            request = builder.url(url).get().build();
        }
        Call c = httpClient.newCall(request);
        try (Response res = c.execute()) {
            String header = "";
            String content = "";
            if (res.headers() != null) {
                header = res.headers().toString();
            }
            if (res.body() != null) {
                content = res.body().string();
            }

            return new ECSResult(res.code(), header, content);
        }
    }

    private static class ECSResult {
        public final int code;
        public final String header;
        public final String content;

        public ECSResult(int code, String header, String content) {
            this.code = code;
            this.header = header;
            this.content = content;
        }

        @Override
        public String toString() {
            return " Code : " + code + "; Headers : " + header
                + "; Content : " + content;
        }
    }
}

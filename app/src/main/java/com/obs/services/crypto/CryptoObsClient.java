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
package com.obs.services.crypto;

import static com.obs.services.crypto.CTRCipherGenerator.ENCRYPTED_ALGORITHM_META_NAME;
import static com.obs.services.crypto.CTRCipherGenerator.ENCRYPTED_START_META_NAME;
import static com.obs.services.crypto.CTRCipherGenerator.getBase64Info;
import static com.obs.services.crypto.CtrRSACipherGenerator.ENCRYPTED_AES_KEY_META_NAME;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.IObsCredentialsProvider;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.Constants;
import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.ProgressManager;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.SimpleProgressManager;
import com.obs.services.internal.io.ProgressInputStream;
import com.obs.services.internal.trans.NewTransResult;
import com.obs.services.internal.utils.JSONChange;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.fs.ObsFSAttribute;
import com.obs.services.model.fs.ObsFSFile;
import com.obs.services.model.fs.ReadFileResult;

import okhttp3.Response;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Objects;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CryptoObsClient extends ObsClient {
    private CTRCipherGenerator ctrCipherGenerator;
    private static final ILogger log = LoggerBuilder.getLogger(CryptoObsClient.class);

    public CryptoObsClient(String endPoint, CTRCipherGenerator ctrCipherGenerator) {
        super(endPoint);
        this.ctrCipherGenerator = ctrCipherGenerator;
    }

    public CryptoObsClient(ObsConfiguration config, CTRCipherGenerator ctrCipherGenerator) {
        super(config);
        this.ctrCipherGenerator = ctrCipherGenerator;
    }

    public CryptoObsClient(String accessKey, String secretKey, String endPoint, CTRCipherGenerator ctrCipherGenerator) {
        super(accessKey, secretKey, endPoint);
        this.ctrCipherGenerator = ctrCipherGenerator;
    }

    public CryptoObsClient(
            String accessKey, String secretKey, ObsConfiguration config, CTRCipherGenerator ctrCipherGenerator) {
        super(accessKey, secretKey, config);
        this.ctrCipherGenerator = ctrCipherGenerator;
    }

    public CryptoObsClient(
            String accessKey,
            String secretKey,
            String securityToken,
            String endPoint,
            CTRCipherGenerator ctrCipherGenerator) {
        super(accessKey, secretKey, securityToken, endPoint);
        this.ctrCipherGenerator = ctrCipherGenerator;
    }

    public CryptoObsClient(
            String accessKey,
            String secretKey,
            String securityToken,
            ObsConfiguration config,
            CTRCipherGenerator ctrCipherGenerator) {
        super(accessKey, secretKey, securityToken, config);
        this.ctrCipherGenerator = ctrCipherGenerator;
    }

    public CryptoObsClient(IObsCredentialsProvider provider, String endPoint, CTRCipherGenerator ctrCipherGenerator) {
        super(provider, endPoint);
        this.ctrCipherGenerator = ctrCipherGenerator;
    }

    public CryptoObsClient(
            IObsCredentialsProvider provider, ObsConfiguration config, CTRCipherGenerator ctrCipherGenerator) {
        super(provider, config);
        this.ctrCipherGenerator = ctrCipherGenerator;
    }

    @Override
    public PutObjectResult putObject(final PutObjectRequest request) throws ObsException {
        ServiceUtils.assertParameterNotNull(request, "PutObjectRequest is null");
        ServiceUtils.assertParameterNotNull2(request.getBucketName(), "bucketName is null");
        ServiceUtils.assertParameterNotNull2(request.getObjectKey(), "objectKey is null");

        return this.doActionWithResult(
                "putObject",
                request.getBucketName(),
                new ActionCallbackWithResult<PutObjectResult>() {
                    @Override
                    public PutObjectResult action() throws ServiceException {
                        if (null != request.getInput() && null != request.getFile()) {
                            throw new ServiceException("Both input and file are set, only one is allowed");
                        }
                        return CryptoObsClient.this.putObjectImpl(request);
                    }
                });
    }

    @Override
    protected ObsFSFile putObjectImpl(PutObjectRequest request) throws ServiceException {
        TransResult result = null;
        Response response;
        boolean isExtraAclPutRequired;
        AccessControlList acl = request.getAcl();
        NewTransResult newTransResult;
        if (request.getMetadata() == null) {
            request.setMetadata(new ObjectMetadata());
        }
        try {
            if (this.ctrCipherGenerator != null) {
                // 设计决策：所有新上传强制使用 HMAC EtM (Encrypt-then-MAC) 模式，
                // 以修复 CWE-353（缺少 HMAC 完整性验证）。旧 CTR-only 模式仅保留
                // 用于读取历史加密数据（见 getObjectImpl 中的解密分支）。
                // 获取AES密钥和初始值
                byte[] objectCryptoIvBytes = getOrGenerateCryptoIvBytes();
                byte[] objectCryptoKeyBytes = getOrGenerateCryptoKeyBytes();
                // 计算加密前后数据的sha256值
                // 注意: HMAC模式不需要额外的SHA256计算，HMAC已提供完整性保护
                if (request.getFile() != null) {
                    request.getMetadata()
                            .addUserMetadata(
                                    CTRCipherGenerator.PLAINTEXT_CONTENT_LENGTH_META_NAME,
                                    String.valueOf(request.getFile().length()));
                    FileInputStream fileInputStream = null;
                    boolean needCloseStream = true;
                    try {
                        fileInputStream = new FileInputStream(request.getFile());
                        // HMAC模式: 不计算SHA256，HMAC本身已提供完整性保护
                        // 流会在 getAES256CTRHMACEncryptedStream 中被消费并关闭
                        request.setInput(fileInputStream);
                        needCloseStream = false;
                    } catch (FileNotFoundException e) {
                        throw new IllegalArgumentException("File doesn't exist");
                    } catch (IOException e) {
                        throw new ServiceException(e);
                    } finally {
                        if (needCloseStream && fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (IOException e) {
                                if (log.isWarnEnabled()) {
                                    log.warn("close fileInputStream failed.", e);
                                }
                            }
                        }
                    }
                }

                // 设置加密数据流 (使用HMAC EtM模式提供完整性保护)
                // 注意: 需要记录原始Content-Length用于解密验证
                ObjectMetadata objectMetadata = request.getMetadata();
                if (objectMetadata == null) {
                    objectMetadata = new ObjectMetadata();
                    request.setMetadata(objectMetadata);
                }
                Long contentLength = objectMetadata.getContentLength();
                if (contentLength != null && contentLength > 0) {
                    // 记录原始内容长度（不含HMAC开销）
                    objectMetadata.addUserMetadata(
                            CTRCipherGenerator.PLAINTEXT_CONTENT_LENGTH_META_NAME,
                            String.valueOf(contentLength));
                }
                request.setInput(
                        ctrCipherGenerator.getAES256CTRHMACEncryptedStream(
                                request.getInput(), objectCryptoIvBytes, objectCryptoKeyBytes));
                // 设置加密信息的自定义头域
                objectMetadata.addUserMetadata(ENCRYPTED_START_META_NAME, getBase64Info(objectCryptoIvBytes));
                if (ctrCipherGenerator.getMasterKeyInfo() != null) {
                    objectMetadata.addUserMetadata(
                            CTRCipherGenerator.MASTER_KEY_INFO_META_NAME, ctrCipherGenerator.getMasterKeyInfo());
                }
                if (this.ctrCipherGenerator instanceof CtrRSACipherGenerator) {
                    // 附件HMAC EtM加密算法元数据信息
                    objectMetadata.addUserMetadata(
                            ENCRYPTED_ALGORITHM_META_NAME, CtrRSACipherGenerator.ENCRYPTED_ALGORITHM_HMAC);
                    // rsa 加密aesKey
                    CtrRSACipherGenerator ctrRSACipherGenerator = (CtrRSACipherGenerator) ctrCipherGenerator;
                    byte[] rsaEncryptedAESKey = ctrRSACipherGenerator.RSAEncrypted(objectCryptoKeyBytes);
                    // 将加密后的aesKey附加到元数据
                    objectMetadata.addUserMetadata(
                            ENCRYPTED_AES_KEY_META_NAME, ServiceUtils.toBase64(rsaEncryptedAESKey));
                } else {
                    // 附件HMAC EtM加密算法元数据信息
                    objectMetadata.addUserMetadata(
                            ENCRYPTED_ALGORITHM_META_NAME, CTRCipherGenerator.ENCRYPTED_ALGORITHM_CTR_HMAC);
                }
            }
            // 后面和普通上传一致
            result = this.transPutObjectRequest(request);
            isExtraAclPutRequired = !prepareRESTHeaderAcl(request.getBucketName(), result.getHeaders(), acl);
            if (request.getCallback() != null) {
                ServiceUtils.assertParameterNotNull(request.getCallback().getCallbackUrl(), "callbackUrl is null");
                ServiceUtils.assertParameterNotNull(request.getCallback().getCallbackBody(), "callbackBody is null");
                result.getHeaders()
                        .put(
                                (this.getProviderCredentials().getLocalAuthType(request.getBucketName())
                                                        != AuthTypeEnum.OBS
                                                ? Constants.V2_HEADER_PREFIX
                                                : Constants.OBS_HEADER_PREFIX)
                                        + Constants.CommonHeaders.CALLBACK,
                                ServiceUtils.toBase64(
                                        JSONChange.objToJson(request.getCallback()).getBytes(StandardCharsets.UTF_8)));
            }
            // todo prepareRESTHeaderAcl 也会操作头域，下次重构可以将其合并
            newTransResult = transObjectRequestWithResult(result, request);
            response = performRequest(newTransResult, true, false, false, false);
        } catch (InvalidAlgorithmParameterException
                | NoSuchPaddingException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException
                | NoSuchProviderException
                | IOException e) {
            throw new ServiceException(e);
        } finally {
            if (result != null && result.getBody() != null && request.isAutoClose()) {
                if (result.getBody() instanceof Closeable) {
                    ServiceUtils.closeStream((Closeable) result.getBody());
                }
            }
        }

        ObsFSFile ret =
                new ObsFSFile(
                        request.getBucketName(),
                        request.getObjectKey(),
                        response.header(Constants.CommonHeaders.ETAG),
                        response.header(this.getIHeaders(request.getBucketName()).versionIdHeader()),
                        StorageClassEnum.getValueFromCode(
                                response.header(this.getIHeaders(request.getBucketName()).storageClassHeader())),
                        this.getObjectUrl(request.getBucketName(), request.getObjectKey(), request.getIsIgnorePort()));
        if (request.getCallback() != null) {
            try {
                ret.setCallbackResponseBody(Objects.requireNonNull(response.body()).byteStream());
            } catch (Exception e) {
                throw new ServiceException(e);
            }
        }
        setHeadersAndStatus(ret, response);
        if (isExtraAclPutRequired && acl != null) {
            try {
                putAclImpl(request.getBucketName(), request.getObjectKey(), acl, null, request.isRequesterPays());
            } catch (Exception e) {
                log.warn("Try to set object acl error", e);
            }
        }
        return ret;
    }

    @Override
    public ObsObject getObject(final GetObjectRequest request) throws ObsException {
        ServiceUtils.assertParameterNotNull(request, "GetObjectRequest is null");
        ServiceUtils.assertParameterNotNull2(request.getObjectKey(), "objectKey is null");
        return this.doActionWithResult(
                "getObject",
                request.getBucketName(),
                new ActionCallbackWithResult<ObsObject>() {
                    @Override
                    public ObsObject action() throws ServiceException {
                        return CryptoObsClient.this.getObjectImpl(request);
                    }
                });
    }

    @Override
    protected ObsObject getObjectImpl(GetObjectRequest request) throws ServiceException {
        Response response;
        TransResult result = this.transGetObjectRequest(request);
        if (request.getRequestParameters() != null) {
            result.getParams().putAll(request.getRequestParameters());
        }
        response =
                performRestGet(
                        request.getBucketName(),
                        request.getObjectKey(),
                        result.getParams(),
                        result.getHeaders(),
                        request.getUserHeaders(),
                        false,
                        request.isEncodeHeaders());

        ObsFSAttribute objMetadata =
                this.getObsFSAttributeFromResponse(request.getBucketName(), response, request.isEncodeHeaders());

        ReadFileResult obsObject = new ReadFileResult();
        obsObject.setObjectKey(request.getObjectKey());
        obsObject.setBucketName(request.getBucketName());
        obsObject.setMetadata(objMetadata);

        // 该接口是下载对象，需要将流返回给客户（调用方），我们不能关闭这个流

        if (ctrCipherGenerator != null) {
            String headerMetaPrefix =
                    this.getProviderCredentials() != null &&
                            this.getProviderCredentials().getLocalAuthType(request.getBucketName()) != AuthTypeEnum.OBS
                    ? Constants.V2_HEADER_META_PREFIX : Constants.OBS_HEADER_META_PREFIX;
            String encryptedAlgorithm =
                    (String)
                            objMetadata
                                    .getOriginalHeaders()
                                    .get(headerMetaPrefix + ENCRYPTED_ALGORITHM_META_NAME);
            String encryptedStart =
                    (String)
                            objMetadata
                                    .getOriginalHeaders()
                                    .get(headerMetaPrefix + ENCRYPTED_START_META_NAME);

            if (isValidEncryptedAlgorithm(encryptedAlgorithm)) {
                byte[] cryptoKeyBytes = null;
                // 修复：检查是否为RSA加密方式（包含"RSA"字符串，包括HMAC+RSA模式）
                if (encryptedAlgorithm.contains("RSA")) {
                    // 如果是rsa加密方式
                    if (!(ctrCipherGenerator instanceof CtrRSACipherGenerator)) {
                        throw new ServiceException(
                                "wrong CipherGenerator ,need " + CtrRSACipherGenerator.class.getSimpleName());
                    } else {
                        try {
                            CtrRSACipherGenerator ctrRSACipherGenerator = (CtrRSACipherGenerator) ctrCipherGenerator;
                            String aesEncryptedKey =
                                    (String)
                                            objMetadata
                                                    .getOriginalHeaders()
                                                    .get(
                                                            headerMetaPrefix
                                                                    + ENCRYPTED_AES_KEY_META_NAME);
                            if (aesEncryptedKey == null) {
                                throw new ServiceException("RSA encrypted AES key not found in metadata");
                            }
                            // 解密rsa加密后的主密钥
                            cryptoKeyBytes =
                                    ctrRSACipherGenerator.RSADecrypted(ServiceUtils.fromBase64(aesEncryptedKey));
                        } catch (NoSuchPaddingException
                                | IllegalBlockSizeException
                                | UnsupportedEncodingException
                                | NoSuchAlgorithmException
                                | BadPaddingException
                                | InvalidKeyException
                                | NoSuchProviderException e) {
                            throw new ServiceException(e);
                        }
                    }
                } else {
                    // 非RSA模式使用预设密钥
                    cryptoKeyBytes = ctrCipherGenerator.getCryptoKeyBytes();
                }
                try {
                    byte[] iv = CTRCipherGenerator.getBytesFromBase64(encryptedStart);

                    // 检测是否为HMAC EtM模式
                    if (CTRCipherGenerator.isCTRHMACAlgorithm(encryptedAlgorithm)) {
                        // HMAC EtM模式：使用HMAC验证后解密
                        obsObject.setObjectContent(
                                ctrCipherGenerator.getAES256CTRHMACDecryptedStream(
                                        response.body().byteStream(), iv, cryptoKeyBytes));
                    } else {
                        // 旧CTR模式：直接解密（无完整性保护）
                        obsObject.setObjectContent(
                                ctrCipherGenerator.getAES256DecryptedStream(
                                        response.body().byteStream(), iv, cryptoKeyBytes));
                    }
                } catch (HMACVerificationException e) {
                    // HMAC验证失败，抛出明确的完整性错误
                    throw new ServiceException("HMAC verification failed: data integrity violation detected", e);
                } catch (InvalidAlgorithmParameterException
                        | NoSuchPaddingException
                        | NoSuchAlgorithmException
                        | InvalidKeyException
                        | NoSuchProviderException
                        | IllegalBlockSizeException
                        | BadPaddingException
                        | IOException e) {
                    throw new ServiceException(e);
                }
            } else {
                log.warn("no encrypted-algorithm metadata received");
                obsObject.setObjectContent(response.body().byteStream());
            }
        } else {
            log.warn("CipherGenerator is null");
            obsObject.setObjectContent(response.body().byteStream());
        }
        if (request.getProgressListener() != null) {
            ProgressManager progressManager =
                    new SimpleProgressManager(
                            objMetadata.getContentLength(),
                            0,
                            request.getProgressListener(),
                            request.getProgressInterval() > 0
                                    ? request.getProgressInterval()
                                    : ObsConstraint.DEFAULT_PROGRESS_INTERVAL);
            obsObject.setObjectContent(new ProgressInputStream(obsObject.getObjectContent(), progressManager));
        }

        int readBufferSize =
                obsProperties.getIntProperty(ObsConstraint.READ_BUFFER_SIZE, ObsConstraint.DEFAULT_READ_BUFFER_STREAM);
        if (readBufferSize > 0) {
            obsObject.setObjectContent(new BufferedInputStream(obsObject.getObjectContent(), readBufferSize));
        }
        return obsObject;
    }

    public boolean isValidEncryptedAlgorithm(String encryptedAlgorithm) {
        return encryptedAlgorithm != null && (encryptedAlgorithm.equals(CtrRSACipherGenerator.ENCRYPTED_ALGORITHM)
                || encryptedAlgorithm.equals(CTRCipherGenerator.ENCRYPTED_ALGORITHM)
                // 新增: 支持HMAC EtM模式
                || encryptedAlgorithm.equals(CtrRSACipherGenerator.ENCRYPTED_ALGORITHM_HMAC)
                || encryptedAlgorithm.equals(CTRCipherGenerator.ENCRYPTED_ALGORITHM_CTR_HMAC));
    }

    protected byte[] getOrGenerateCryptoIvBytes() {
        if (ctrCipherGenerator.getCryptoIvBytes() != null) {
            log.info("get user-set AES iv");
            return ctrCipherGenerator.getCryptoIvBytes();
        } else {
            log.info("get random AES iv");
            return ctrCipherGenerator.getRandomCryptoIvBytes();
        }
    }

    protected byte[] getOrGenerateCryptoKeyBytes() {
        if (ctrCipherGenerator.getCryptoKeyBytes() != null) {
            log.info("get user-set AES key");
            return ctrCipherGenerator.getCryptoKeyBytes();
        } else {
            log.info("get random AES key");
            return ctrCipherGenerator.getRandomCryptoKeyBytes();
        }
    }

    public CTRCipherGenerator getCtrCipherGenerator() {
        return ctrCipherGenerator;
    }

    public void setCtrCipherGenerator(CTRCipherGenerator ctrCipherGenerator) {
        this.ctrCipherGenerator = ctrCipherGenerator;
    }
}

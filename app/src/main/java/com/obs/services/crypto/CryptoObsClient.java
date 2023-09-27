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
import com.obs.services.crypto.CTRCipherGenerator.SHA256Info;
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
                // 获取AES密钥和初始值
                byte[] object_CryptoIvBytes = getOrGenerateCryptoIvBytes();
                byte[] object_CryptoKeyBytes = getOrGenerateCryptoKeyBytes();
                // 计算加密前后数据的sha256值
                if (request.getFile() != null && ctrCipherGenerator.isNeedSha256()) {
                    request.getMetadata()
                            .addUserMetadata(
                                    CTRCipherGenerator.PLAINTEXT_CONTENT_LENGTH_META_NAME,
                                    String.valueOf(request.getFile().length()));
                    try (FileInputStream fileInputStream = new FileInputStream(request.getFile())) {
                        String content_sha256_header = Constants.OBS_HEADER_PREFIX + Constants.CONTENT_SHA256;
                        if (request.getUserHeaders().containsKey(content_sha256_header)) {
                            // 用户已设置明文数据的sha256.直接到设置自定义加密元数据中
                            request.getMetadata()
                                    .addUserMetadata(
                                            CTRCipherGenerator.PLAINTEXT_SHA_256_META_NAME,
                                            request.getUserHeaders().get(content_sha256_header));
                            // 计算加密后的数据的sha256
                            SHA256Info sha256Info =
                                    ctrCipherGenerator.computeSHA256HashAES(
                                            fileInputStream, object_CryptoIvBytes, object_CryptoKeyBytes, false);

                            // 设置自定义加密元数据中的密文数据的sha256
                            request.getMetadata()
                                    .addUserMetadata(
                                            CTRCipherGenerator.ENCRYPTED_SHA_256_META_NAME,
                                            sha256Info.getSha256ForAESEncrypted());
                            // 设置头域中的sha256用于验证数据完整性
                            request.addUserHeaders(content_sha256_header, sha256Info.getSha256ForAESEncrypted());
                        } else {
                            // 计算文件sha256和加密后的sha256
                            SHA256Info sha256Info =
                                    ctrCipherGenerator.computeSHA256HashAES(
                                            fileInputStream, object_CryptoIvBytes, object_CryptoKeyBytes, true);
                            // 设置自定义加密元数据中的明文数据的sha256
                            request.getMetadata()
                                    .addUserMetadata(
                                            CTRCipherGenerator.PLAINTEXT_SHA_256_META_NAME,
                                            sha256Info.getSha256ForPlainText());
                            // 设置自定义加密元数据中的密文数据的sha256
                            request.getMetadata()
                                    .addUserMetadata(
                                            CTRCipherGenerator.ENCRYPTED_SHA_256_META_NAME,
                                            sha256Info.getSha256ForAESEncrypted());
                            // 设置头域中的sha256用于验证数据完整性
                            request.addUserHeaders(content_sha256_header, sha256Info.getSha256ForAESEncrypted());
                        }
                        request.setInput(new FileInputStream(request.getFile()));
                    } catch (FileNotFoundException e) {
                        throw new IllegalArgumentException("File doesn't exist");
                    } catch (IOException e) {
                        throw new ServiceException(e);
                    }
                }

                // 设置加密数据流
                request.setInput(
                        ctrCipherGenerator.getAES256EncryptedStream(
                                request.getInput(), object_CryptoIvBytes, object_CryptoKeyBytes));
                ObjectMetadata objectMetadata = request.getMetadata();
                // 设置加密信息的自定义头域
                objectMetadata.addUserMetadata(ENCRYPTED_START_META_NAME, getBase64Info(object_CryptoIvBytes));
                if (ctrCipherGenerator.getMasterKeyInfo() != null) {
                    objectMetadata.addUserMetadata(
                            CTRCipherGenerator.MASTER_KEY_INFO_META_NAME, ctrCipherGenerator.getMasterKeyInfo());
                }
                if (this.ctrCipherGenerator instanceof CtrRSACipherGenerator) {
                    // 附件加密算法元数据信息
                    objectMetadata.addUserMetadata(
                            ENCRYPTED_ALGORITHM_META_NAME, CtrRSACipherGenerator.ENCRYPTED_ALGORITHM);
                    // rsa 加密aesKey
                    CtrRSACipherGenerator ctrRSACipherGenerator = (CtrRSACipherGenerator) ctrCipherGenerator;
                    byte[] rsaEncryptedAESKey = ctrRSACipherGenerator.RSAEncrypted(object_CryptoKeyBytes);
                    // 将加密后的aesKey附加到元数据
                    objectMetadata.addUserMetadata(
                            ENCRYPTED_AES_KEY_META_NAME, ServiceUtils.toBase64(rsaEncryptedAESKey));
                } else {
                    // 附件加密算法元数据信息
                    objectMetadata.addUserMetadata(
                            ENCRYPTED_ALGORITHM_META_NAME, CTRCipherGenerator.ENCRYPTED_ALGORITHM);
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
                | BadPaddingException e) {
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
            String encryptedAlgorithm =
                    (String)
                            objMetadata
                                    .getOriginalHeaders()
                                    .get(Constants.OBS_HEADER_META_PREFIX + ENCRYPTED_ALGORITHM_META_NAME);
            String encryptedStart =
                    (String)
                            objMetadata
                                    .getOriginalHeaders()
                                    .get(Constants.OBS_HEADER_META_PREFIX + ENCRYPTED_START_META_NAME);

            if (isValidEncryptedAlgorithm(encryptedAlgorithm)) {
                byte[] cryptoKeyBytes = ctrCipherGenerator.getCryptoKeyBytes();
                if (encryptedAlgorithm.equals(CtrRSACipherGenerator.ENCRYPTED_ALGORITHM)) {
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
                                                            Constants.OBS_HEADER_META_PREFIX
                                                                    + ENCRYPTED_AES_KEY_META_NAME);
                            // 解密rsa加密后的主密钥
                            cryptoKeyBytes =
                                    ctrRSACipherGenerator.RSADecrypted(ServiceUtils.fromBase64(aesEncryptedKey));
                        } catch (NoSuchPaddingException
                                | IllegalBlockSizeException
                                | UnsupportedEncodingException
                                | NoSuchAlgorithmException
                                | BadPaddingException
                                | InvalidKeyException e) {
                            throw new ServiceException(e);
                        }
                    }
                }
                try {
                    byte[] iv = CTRCipherGenerator.getBytesFromBase64(encryptedStart);

                    // 设置解密流
                    obsObject.setObjectContent(
                            ctrCipherGenerator.getAES256DecryptedStream(
                                    response.body().byteStream(), iv, cryptoKeyBytes));
                } catch (UnsupportedEncodingException
                        | InvalidAlgorithmParameterException
                        | NoSuchPaddingException
                        | NoSuchAlgorithmException
                        | InvalidKeyException e) {
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
        return encryptedAlgorithm.equals(CtrRSACipherGenerator.ENCRYPTED_ALGORITHM)
                || encryptedAlgorithm.equals(CTRCipherGenerator.ENCRYPTED_ALGORITHM);
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

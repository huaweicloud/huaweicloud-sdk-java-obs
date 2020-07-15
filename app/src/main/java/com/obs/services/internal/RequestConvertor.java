/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.obs.services.internal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.Constants.CommonHeaders;
import com.obs.services.internal.Constants.ObsRequestParams;
import com.obs.services.internal.io.ProgressInputStream;
import com.obs.services.internal.utils.AbstractAuthentication;
import com.obs.services.internal.utils.Mimetypes;
import com.obs.services.internal.utils.RestUtils;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.AppendObjectRequest;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.AvailableZoneEnum;
import com.obs.services.model.BucketTypeEnum;
import com.obs.services.model.CopyObjectRequest;
import com.obs.services.model.CopyPartRequest;
import com.obs.services.model.CreateBucketRequest;
import com.obs.services.model.ExtensionBucketPermissionEnum;
import com.obs.services.model.ExtensionObjectPermissionEnum;
import com.obs.services.model.GenericRequest;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.InitiateMultipartUploadRequest;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ListVersionsRequest;
import com.obs.services.model.ModifyObjectRequest;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.PutObjectBasicRequest;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.RestoreObjectResult;
import com.obs.services.model.SetObjectMetadataRequest;
import com.obs.services.model.SpecialParamEnum;
import com.obs.services.model.SseCHeader;
import com.obs.services.model.SseKmsHeader;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.UploadPartRequest;
import com.obs.services.model.RestoreObjectRequest.RestoreObjectStatus;
import com.obs.services.model.fs.FSStatusEnum;
import com.obs.services.model.fs.GetBucketFSStatusResult;
import com.obs.services.model.fs.ListContentSummaryRequest;
import com.obs.services.model.fs.NewBucketRequest;
import com.obs.services.model.fs.WriteFileRequest;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestConvertor extends RestStorageService {
    private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.ObsClient");

    protected static class TransResult {
        private Map<String, String> headers;

        private Map<String, String> params;

        private RequestBody body;

        TransResult(Map<String, String> headers) {
            this(headers, null, null);
        }

        TransResult(Map<String, String> headers, RequestBody body) {
            this(headers, null, body);
        }

        TransResult(Map<String, String> headers, Map<String, String> params, RequestBody body) {
            this.headers = headers;
            this.params = params;
            this.body = body;
        }

        Map<String, String> getHeaders() {
            if (this.headers == null) {
                headers = new HashMap<String, String>();
            }
            return this.headers;
        }

        Map<String, String> getParams() {
            if (this.params == null) {
                params = new HashMap<String, String>();
            }
            return this.params;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }

        public void setBody(RequestBody body) {
            this.body = body;
        }

        public RequestBody getBody() {
            return body;
        }
    }

    TransResult transListVersionsRequest(ListVersionsRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SpecialParamEnum.VERSIONS.getOriginalStringCode(), "");
        if (request.getPrefix() != null) {
            params.put(ObsRequestParams.PREFIX, request.getPrefix());
        }
        if (request.getDelimiter() != null) {
            params.put(ObsRequestParams.DELIMITER, request.getDelimiter());
        }
        if (request.getMaxKeys() > 0) {
            params.put(ObsRequestParams.MAX_KEYS, String.valueOf(request.getMaxKeys()));
        }
        if (request.getKeyMarker() != null) {
            params.put(ObsRequestParams.KEY_MARKER, request.getKeyMarker());
        }
        if (request.getVersionIdMarker() != null) {
            params.put(ObsRequestParams.VERSION_ID_MARKER, request.getVersionIdMarker());
        }
        Map<String, String> headers = new HashMap<String, String>();
        if (request.getListTimeout() > 0) {
            putHeader(headers, this.getIHeaders().listTimeoutHeader(), String.valueOf(request.getListTimeout()));
        }

        this.transRequestPaymentHeaders(request, headers, this.getIHeaders());

        return new TransResult(headers, params, null);
    }

    /**
     * set requestHeader for requestPayment
     * 
     * @param request
     * @param headers
     * @param iheaders
     * @throws ServiceException
     */
    Map<String, String> transRequestPaymentHeaders(boolean isRequesterPays, Map<String, String> headers,
            IHeaders iheaders) throws ServiceException {
        if (isRequesterPays) {
            if (null == headers) {
                headers = new HashMap<String, String>();
            }
            putHeader(headers, iheaders.requestPaymentHeader(), "requester");
        }

        return headers;
    }

    /**
     * set requestHeader for requestPayment
     * 
     * @param request
     * @param headers
     * @param iheaders
     * @throws ServiceException
     */
    Map<String, String> transRequestPaymentHeaders(GenericRequest request, Map<String, String> headers,
            IHeaders iheaders) throws ServiceException {
        if (null != request) {
            return transRequestPaymentHeaders(request.isRequesterPays(), headers, iheaders);
        }

        return null;
    }

    TransResult transInitiateMultipartUploadRequest(InitiateMultipartUploadRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        IHeaders iheaders = this.getIHeaders();
        IConvertor iconvertor = this.getIConvertor();

        ObjectMetadata objectMetadata = request.getMetadata() == null ? new ObjectMetadata() : request.getMetadata();

        for (Map.Entry<String, Object> entry : objectMetadata.getMetadata().entrySet()) {
            String key = entry.getKey();
            if (!ServiceUtils.isValid(key)) {
                continue;
            }
            key = key.trim();
            if ((CAN_USE_STANDARD_HTTP_HEADERS.get() == null || (CAN_USE_STANDARD_HTTP_HEADERS.get() != null
                    && !CAN_USE_STANDARD_HTTP_HEADERS.get().booleanValue()))
                    && Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key.toLowerCase())) {
                continue;
            }
            headers.put(key, entry.getValue() == null ? "" : entry.getValue().toString());
        }

        if (objectMetadata.getObjectStorageClass() != null) {
            putHeader(headers, iheaders.storageClassHeader(),
                    iconvertor.transStorageClass(objectMetadata.getObjectStorageClass()));
        }

        if (request.getExpires() > 0) {
            putHeader(headers, iheaders.expiresHeader(), String.valueOf(request.getExpires()));
        }

        if (ServiceUtils.isValid(objectMetadata.getWebSiteRedirectLocation())) {
            putHeader(headers, iheaders.websiteRedirectLocationHeader(), objectMetadata.getWebSiteRedirectLocation());
        }

        if (ServiceUtils.isValid(request.getSuccessRedirectLocation())) {
            putHeader(headers, iheaders.successRedirectLocationHeader(), request.getSuccessRedirectLocation());
        }

        if (ServiceUtils.isValid(objectMetadata.getContentEncoding())) {
            headers.put(CommonHeaders.CONTENT_ENCODING, objectMetadata.getContentEncoding().trim());
        }

        transRequestPaymentHeaders(request, headers, iheaders);

        transExtensionPermissions(request, headers);

        transSseHeaders(request, headers, iheaders);

        Object contentType = objectMetadata.getContentType() == null
                ? objectMetadata.getValue(CommonHeaders.CONTENT_TYPE) : objectMetadata.getContentType();
        if (contentType == null) {
            contentType = Mimetypes.getInstance().getMimetype(request.getObjectKey());
        }

        String contentTypeStr = contentType.toString().trim();
        headers.put(CommonHeaders.CONTENT_TYPE, contentTypeStr);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SpecialParamEnum.UPLOADS.getOriginalStringCode(), "");

        return new TransResult(headers, params, null);
    }

    String getHeaderByMethodName(String code) {
        try {
            IHeaders iheaders = this.getIHeaders();
            Method m = iheaders.getClass().getMethod(code);
            Object result = m.invoke(iheaders);
            return result == null ? "" : result.toString();
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Invoke getHeaderByMethodName error", e);
            }
        }
        return null;
    }

    void transExtensionPermissions(PutObjectBasicRequest request, Map<String, String> headers) {
        Set<ExtensionObjectPermissionEnum> extensionPermissionEnums = request.getAllGrantPermissions();
        if (!extensionPermissionEnums.isEmpty()) {
            for (ExtensionObjectPermissionEnum extensionPermissionEnum : extensionPermissionEnums) {
                Set<String> domainIds = request.getDomainIdsByGrantPermission(extensionPermissionEnum);
                List<String> domainIdList = new ArrayList<String>(domainIds.size());
                for (String domainId : domainIds) {
                    domainIdList.add("id=" + domainId);
                }
                putHeader(headers, getHeaderByMethodName(extensionPermissionEnum.getCode()),
                        ServiceUtils.join(domainIdList, ","));
            }
        }
    }

    void transSseHeaders(PutObjectBasicRequest request, Map<String, String> headers, IHeaders iheaders)
            throws ServiceException {
        if (null != request.getSseCHeader()) {
            this.transSseCHeaders(request.getSseCHeader(), headers, iheaders);
        } else if (null != request.getSseKmsHeader()) {
            this.transSseKmsHeaders(request.getSseKmsHeader(), headers, iheaders);
        }
    }

    void transSseCHeaders(SseCHeader cHeader, Map<String, String> headers, IHeaders iheaders) throws ServiceException {
        if (cHeader == null) {
            return;
        }

        String sseCAlgorithm = cHeader.getSSEAlgorithm().getCode();

        putHeader(headers, iheaders.sseCHeader(), ServiceUtils.toValid(sseCAlgorithm));
        if (cHeader.getSseCKeyBase64() != null) {
            try {
                putHeader(headers, iheaders.sseCKeyHeader(), cHeader.getSseCKeyBase64());
                putHeader(headers, iheaders.sseCKeyMd5Header(), ServiceUtils
                        .toBase64(ServiceUtils.computeMD5Hash(ServiceUtils.fromBase64(cHeader.getSseCKeyBase64()))));
            } catch (IOException e) {
                throw new IllegalStateException("fail to read sseCkey", e);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("fail to read sseCkey", e);
            }
        } else if (null != cHeader.getSseCKey()) {
            try {
                byte[] data = cHeader.getSseCKey();
                putHeader(headers, iheaders.sseCKeyHeader(), ServiceUtils.toBase64(data));
                putHeader(headers, iheaders.sseCKeyMd5Header(),
                        ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(data)));
            } catch (IOException e) {
                throw new IllegalStateException("fail to read sseCkey", e);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("fail to read sseCkey", e);
            }
        }
    }

    TransResult transCreateBucketRequest(CreateBucketRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        IConvertor convertor = this.getIConvertor();

        if (request.getBucketStorageClass() != null) {
            putHeader(headers, getIHeaders().defaultStorageClassHeader(),
                    convertor.transStorageClass(request.getBucketStorageClass()));
        }

        if (request.getEpid() != null) {
            putHeader(headers, getIHeaders().epidHeader(), request.getEpid());
        }

        if (request instanceof NewBucketRequest) {
            putHeader(headers, getIHeaders().fsFileInterfaceHeader(), Constants.ENABLED);
        }

        if (null != request.getBucketType() && BucketTypeEnum.PFS == request.getBucketType()) {
            putHeader(headers, getIHeaders().fsFileInterfaceHeader(), Constants.ENABLED);
        }

        if (request.getAvailableZone() != null) {
            putHeader(headers, getIHeaders().azRedundancyHeader(), request.getAvailableZone().getCode());
        }

        Set<ExtensionBucketPermissionEnum> extensionPermissionEnums = request.getAllGrantPermissions();
        if (!extensionPermissionEnums.isEmpty()) {
            for (ExtensionBucketPermissionEnum extensionPermissionEnum : extensionPermissionEnums) {
                Set<String> domainIds = request.getDomainIdsByGrantPermission(extensionPermissionEnum);
                List<String> domainIdList = new ArrayList<String>(domainIds.size());
                for (String domainId : domainIds) {
                    domainIdList.add("id=" + domainId);
                }
                putHeader(headers, getHeaderByMethodName(extensionPermissionEnum.getCode()),
                        ServiceUtils.join(domainIdList, ","));
            }
        }

        if (request.getExtensionHeaderMap() != null) {
            for (Entry<String, String> kv : request.getExtensionHeaderMap().entrySet()) {
                putHeader(headers, kv.getKey(), kv.getValue());
            }
        }

        String contentType = Mimetypes.MIMETYPE_XML;
        headers.put(Constants.CommonHeaders.CONTENT_TYPE, contentType);
        TransResult result = new TransResult(headers);
        if (ServiceUtils.isValid(request.getLocation())) {
            String configXml = convertor.transBucketLoction(request.getLocation());
            headers.put(Constants.CommonHeaders.CONTENT_LENGTH, String.valueOf(configXml.length()));
            RequestBody requestEntity = createRequestBody(contentType, configXml);
            result.setBody(requestEntity);
        }
        return result;
    }

    RestoreObjectStatus transRestoreObjectResultToRestoreObjectStatus(RestoreObjectResult result) {
        RestoreObjectStatus ret = RestoreObjectStatus.valueOf(result.getStatusCode());
        ret.setResponseHeaders(result.getResponseHeaders());
        ret.setStatusCode(result.getStatusCode());

        return ret;
    }

    TransResult transPutObjectRequest(PutObjectRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        IConvertor iconvertor = this.getIConvertor();
        IHeaders iheaders = this.getIHeaders();

        ObjectMetadata objectMetadata = request.getMetadata() == null ? new ObjectMetadata() : request.getMetadata();

        for (Map.Entry<String, Object> entry : objectMetadata.getMetadata().entrySet()) {
            String key = entry.getKey();
            if (!ServiceUtils.isValid(key)) {
                continue;
            }
            key = key.trim();
            if ((CAN_USE_STANDARD_HTTP_HEADERS.get() == null || (CAN_USE_STANDARD_HTTP_HEADERS.get() != null
                    && !CAN_USE_STANDARD_HTTP_HEADERS.get().booleanValue()))
                    && Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key.toLowerCase())) {
                continue;
            }
            headers.put(key, entry.getValue() == null ? "" : entry.getValue().toString());
        }

        if (ServiceUtils.isValid(objectMetadata.getContentMd5())) {
            headers.put(CommonHeaders.CONTENT_MD5, objectMetadata.getContentMd5().trim());
        }

        if (ServiceUtils.isValid(objectMetadata.getContentEncoding())) {
            headers.put(CommonHeaders.CONTENT_ENCODING, objectMetadata.getContentEncoding().trim());
        }

        if (objectMetadata.getObjectStorageClass() != null) {
            putHeader(headers, iheaders.storageClassHeader(),
                    iconvertor.transStorageClass(objectMetadata.getObjectStorageClass()));
        }

        if (request.getExpires() >= 0) {
            putHeader(headers, iheaders.expiresHeader(), String.valueOf(request.getExpires()));
        }

        if (objectMetadata.getWebSiteRedirectLocation() != null) {
            putHeader(headers, iheaders.websiteRedirectLocationHeader(), objectMetadata.getWebSiteRedirectLocation());
        }

        if (request.getSuccessRedirectLocation() != null) {
            putHeader(headers, iheaders.successRedirectLocationHeader(), request.getSuccessRedirectLocation());
        }

        transRequestPaymentHeaders(request, headers, iheaders);

        transExtensionPermissions(request, headers);

        transSseHeaders(request, headers, iheaders);

        Object contentType = objectMetadata.getContentType() == null
                ? objectMetadata.getValue(CommonHeaders.CONTENT_TYPE) : objectMetadata.getContentType();
        if (contentType == null) {
            contentType = Mimetypes.getInstance().getMimetype(request.getObjectKey());
        }
        Object contentLength = objectMetadata.getContentLength();

        if (contentLength == null) {
            contentLength = objectMetadata.getValue(CommonHeaders.CONTENT_LENGTH);
        }

        long contentLengthValue = contentLength == null ? -1L : Long.parseLong(contentLength.toString());

        if (request.getFile() != null) {
            if (Mimetypes.MIMETYPE_OCTET_STREAM.equals(contentType)) {
                contentType = Mimetypes.getInstance().getMimetype(request.getFile());
            }

            long fileSize = request.getFile().length();
            
            try {
                request.setInput(new FileInputStream(request.getFile()));
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("File doesnot exist");
            }
            
            if (request.getOffset() > 0 && request.getOffset() < fileSize) {
                contentLengthValue = (contentLengthValue > 0 && contentLengthValue <= fileSize - request.getOffset())
                        ? contentLengthValue : fileSize - request.getOffset();
                try {
                    long skipByte = request.getInput().skip(request.getOffset());
                    if (log.isDebugEnabled()) {
                        log.debug("Skip " + skipByte + " bytes; offset : " + request.getOffset());
                    }
                } catch (IOException e) {
                    ServiceUtils.closeStream(request.getInput());
                    throw new ServiceException(e);
                }
            } else if (contentLengthValue < 0 || contentLengthValue > fileSize) {
                contentLengthValue = fileSize;
            }
        }

        String contentTypeStr = contentType.toString().trim();
        headers.put(CommonHeaders.CONTENT_TYPE, contentTypeStr);

        if (contentLengthValue > -1) {
            this.putHeader(headers, CommonHeaders.CONTENT_LENGTH, String.valueOf(contentLengthValue));
        }

        if (request.getInput() != null && request.getProgressListener() != null) {
            ProgressManager progressManager = new SimpleProgressManager(contentLengthValue, 0,
                    request.getProgressListener(), request.getProgressInterval() > 0 ? request.getProgressInterval()
                            : ObsConstraint.DEFAULT_PROGRESS_INTERVAL);
            request.setInput(new ProgressInputStream(request.getInput(), progressManager));
        }

        RequestBody body = request.getInput() == null ? null
                : new RepeatableRequestEntity(request.getInput(), contentTypeStr, contentLengthValue,
                        this.obsProperties);

        return new TransResult(headers, body);
    }

    TransResult transWriteFileRequest(WriteFileRequest request) throws ServiceException {
        TransResult result = this.transPutObjectRequest(request);
        if (request.getPosition() > 0) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(SpecialParamEnum.MODIFY.getOriginalStringCode(), "");
            params.put(ObsRequestParams.POSITION, String.valueOf(request.getPosition()));
            result.setParams(params);
        }
        return result;
    }

    TransResult transModifyObjectRequest(ModifyObjectRequest request) throws ServiceException {
        TransResult result = this.transPutObjectRequest(request);
        if (request.getPosition() > 0) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(SpecialParamEnum.MODIFY.getOriginalStringCode(), "");
            params.put(ObsRequestParams.POSITION, String.valueOf(request.getPosition()));
            result.setParams(params);
        }
        return result;
    }

    TransResult transAppendObjectRequest(AppendObjectRequest request) throws ServiceException {
        TransResult result = this.transPutObjectRequest(request);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SpecialParamEnum.APPEND.getOriginalStringCode(), "");
        params.put(ObsRequestParams.POSITION, String.valueOf(request.getPosition()));
        result.setParams(params);
        return result;
    }

    TransResult transCopyObjectRequest(CopyObjectRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        IConvertor iconvertor = this.getIConvertor();
        IHeaders iheaders = this.getIHeaders();

        ObjectMetadata objectMetadata = request.getNewObjectMetadata() == null ? new ObjectMetadata()
                : request.getNewObjectMetadata();

        putHeader(headers, iheaders.metadataDirectiveHeader(),
                request.isReplaceMetadata() ? Constants.DERECTIVE_REPLACE : Constants.DERECTIVE_COPY);
        if (request.isReplaceMetadata()) {
            objectMetadata.getMetadata().remove(iheaders.requestIdHeader());
            objectMetadata.getMetadata().remove(iheaders.requestId2Header());
            for (Map.Entry<String, Object> entry : objectMetadata.getMetadata().entrySet()) {
                String key = entry.getKey();
                if (!ServiceUtils.isValid(key)) {
                    continue;
                }
                key = key.trim();
                if (Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key.toLowerCase())) {
                    continue;
                }
                headers.put(key, entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }

        if (objectMetadata.getContentType() != null) {
            headers.put(CommonHeaders.CONTENT_TYPE, objectMetadata.getContentType().trim());
        }

        if (objectMetadata.getContentEncoding() != null) {
            headers.put(CommonHeaders.CONTENT_ENCODING, objectMetadata.getContentEncoding().trim());
        }

        if (objectMetadata.getObjectStorageClass() != null) {
            putHeader(headers, iheaders.storageClassHeader(),
                    iconvertor.transStorageClass(objectMetadata.getObjectStorageClass()));
        }

        if (objectMetadata.getWebSiteRedirectLocation() != null) {
            putHeader(headers, iheaders.websiteRedirectLocationHeader(), objectMetadata.getWebSiteRedirectLocation());
        }

        if (request.getSuccessRedirectLocation() != null) {
            putHeader(headers, iheaders.successRedirectLocationHeader(), request.getSuccessRedirectLocation());
        }

        this.transRequestPaymentHeaders(request, headers, iheaders);
        this.transExtensionPermissions(request, headers);
        this.transSseHeaders(request, headers, iheaders);

        transSseCSourceHeaders(request.getSseCHeaderSource(), headers, iheaders);

        transConditionCopyHeaders(request, headers, iheaders);

        String sourceKey = RestUtils.encodeUrlString(request.getSourceBucketName()) + "/"
                + RestUtils.encodeUrlString(request.getSourceObjectKey());
        if (ServiceUtils.isValid(request.getVersionId())) {
            sourceKey += "?versionId=" + request.getVersionId().trim();
        }
        putHeader(headers, iheaders.copySourceHeader(), sourceKey);

        return new TransResult(headers);
    }

    void transSseCSourceHeaders(SseCHeader sseCHeader, Map<String, String> headers, IHeaders iheaders)
            throws ServiceException {
        if (sseCHeader != null) {
            String algorithm = sseCHeader.getSSEAlgorithm().getCode();
            putHeader(headers, iheaders.copySourceSseCHeader(), ServiceUtils.toValid(algorithm));
            if (sseCHeader.getSseCKeyBase64() != null) {
                try {
                    putHeader(headers, iheaders.copySourceSseCKeyHeader(), sseCHeader.getSseCKeyBase64());
                    putHeader(headers, iheaders.copySourceSseCKeyMd5Header(), ServiceUtils.toBase64(
                            ServiceUtils.computeMD5Hash(ServiceUtils.fromBase64(sseCHeader.getSseCKeyBase64()))));
                } catch (IOException e) {
                    throw new IllegalStateException("fail to read sseCkey", e);
                } catch (NoSuchAlgorithmException e) {
                    throw new IllegalStateException("fail to read sseCkey", e);
                }
            } else if (null != sseCHeader.getSseCKey()) {
                try {
                    byte[] data = sseCHeader.getSseCKey();
                    putHeader(headers, iheaders.copySourceSseCKeyHeader(), ServiceUtils.toBase64(data));
                    putHeader(headers, iheaders.copySourceSseCKeyMd5Header(),
                            ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(data)));
                } catch (IOException e) {
                    throw new IllegalStateException("fail to read sseCkey", e);
                } catch (NoSuchAlgorithmException e) {
                    throw new IllegalStateException("fail to read sseCkey", e);
                }
            }
        }
    }

    void transConditionCopyHeaders(CopyObjectRequest request, Map<String, String> headers, IHeaders iheaders) {
        if (request.getIfModifiedSince() != null) {
            putHeader(headers, iheaders.copySourceIfModifiedSinceHeader(),
                    ServiceUtils.formatRfc822Date(request.getIfModifiedSince()));
        }
        if (request.getIfUnmodifiedSince() != null) {
            putHeader(headers, iheaders.copySourceIfUnmodifiedSinceHeader(),
                    ServiceUtils.formatRfc822Date(request.getIfUnmodifiedSince()));
        }
        if (ServiceUtils.isValid(request.getIfMatchTag())) {
            putHeader(headers, iheaders.copySourceIfMatchHeader(), request.getIfMatchTag().trim());
        }
        if (ServiceUtils.isValid(request.getIfNoneMatchTag())) {
            putHeader(headers, iheaders.copySourceIfNoneMatchHeader(), request.getIfNoneMatchTag().trim());
        }
    }

    TransResult transGetObjectRequest(GetObjectRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        this.transSseCHeaders(request.getSseCHeader(), headers, this.getIHeaders());
        this.transConditionGetObjectHeaders(request, headers);

        this.transRequestPaymentHeaders(request, headers, this.getIHeaders());
        transRangeHeader(request, headers);

        Map<String, String> params = new HashMap<String, String>();
        this.transGetObjectParams(request, params);

        return new TransResult(headers, params, null);
    }

    /**
     *
     * @param request
     * @param headers
     */
    void transRangeHeader(GetObjectRequest request, Map<String, String> headers) {
        String start = "";
        String end = "";

        if (null != request.getRangeStart()) {
            ServiceUtils.assertParameterNotNegative(request.getRangeStart().longValue(),
                    "start range should not be negative.");
            start = String.valueOf(request.getRangeStart());
        }

        if (null != request.getRangeEnd()) {
            ServiceUtils.assertParameterNotNegative(request.getRangeEnd().longValue(),
                    "end range should not be negative.");
            end = String.valueOf(request.getRangeEnd());
        }

        if (null != request.getRangeStart() && null != request.getRangeEnd()) {
            if (request.getRangeStart().longValue() > request.getRangeEnd().longValue()) {
                throw new IllegalArgumentException("start must be less than end.");
            }
        }

        if (!"".equals(start) || !"".equals(end)) {
            String range = String.format("bytes=%s-%s", start, end);
            headers.put(CommonHeaders.RANGE, range);
        }
    }

    void transGetObjectParams(GetObjectRequest request, Map<String, String> params) {
        if (null != request.getReplaceMetadata()) {
            if (ServiceUtils.isValid(request.getReplaceMetadata().getCacheControl())) {
                params.put(ObsRequestParams.RESPONSE_CACHE_CONTROL, request.getReplaceMetadata().getCacheControl());
            }
            if (ServiceUtils.isValid(request.getReplaceMetadata().getContentDisposition())) {
                params.put(ObsRequestParams.RESPONSE_CONTENT_DISPOSITION,
                        request.getReplaceMetadata().getContentDisposition());
            }
            if (ServiceUtils.isValid(request.getReplaceMetadata().getContentEncoding())) {
                params.put(ObsRequestParams.RESPONSE_CONTENT_ENCODING,
                        request.getReplaceMetadata().getContentEncoding());
            }
            if (ServiceUtils.isValid(request.getReplaceMetadata().getContentLanguage())) {
                params.put(ObsRequestParams.RESPONSE_CONTENT_LANGUAGE,
                        request.getReplaceMetadata().getContentLanguage());
            }
            if (ServiceUtils.isValid(request.getReplaceMetadata().getContentType())) {
                params.put(ObsRequestParams.RESPONSE_CONTENT_TYPE, request.getReplaceMetadata().getContentType());
            }
            if (ServiceUtils.isValid(request.getReplaceMetadata().getExpires())) {
                params.put(ObsRequestParams.RESPONSE_EXPIRES, request.getReplaceMetadata().getExpires());
            }
        }
        if (ServiceUtils.isValid(request.getImageProcess())) {
            params.put(ObsRequestParams.X_IMAGE_PROCESS, request.getImageProcess());
        }
        if (request.getVersionId() != null) {
            params.put(ObsRequestParams.VERSION_ID, request.getVersionId());
        }
        if (request.getCacheOption() != null) {
            String cacheControl = request.getCacheOption().getCode() + ", ttl=" + request.getTtl();
            params.put(ObsRequestParams.X_CACHE_CONTROL, cacheControl);
        }
    }

    void transConditionGetObjectHeaders(GetObjectRequest request, Map<String, String> headers) {
        if (request.getIfModifiedSince() != null) {
            headers.put(CommonHeaders.IF_MODIFIED_SINCE, ServiceUtils.formatRfc822Date(request.getIfModifiedSince()));
        }
        if (request.getIfUnmodifiedSince() != null) {
            headers.put(CommonHeaders.IF_UNMODIFIED_SINCE,
                    ServiceUtils.formatRfc822Date(request.getIfUnmodifiedSince()));
        }
        if (ServiceUtils.isValid(request.getIfMatchTag())) {
            headers.put(CommonHeaders.IF_MATCH, request.getIfMatchTag().trim());
        }
        if (ServiceUtils.isValid(request.getIfNoneMatchTag())) {
            headers.put(CommonHeaders.IF_NONE_MATCH, request.getIfNoneMatchTag().trim());
        }
        if (!request.isAutoUnzipResponse()) {
            headers.put(CommonHeaders.ACCETP_ENCODING, "identity");
        }
    }

    TransResult transSetObjectMetadataRequest(SetObjectMetadataRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        IHeaders iheaders = this.getIHeaders();
        IConvertor iconvertor = this.getIConvertor();

        for (Map.Entry<String, String> entry : request.getMetadata().entrySet()) {
            String key = entry.getKey();
            if (!ServiceUtils.isValid(key)) {
                continue;
            }
            key = key.trim();
            headers.put(key, entry.getValue() == null ? "" : entry.getValue());
        }

        if (request.getObjectStorageClass() != null) {
            putHeader(headers, iheaders.storageClassHeader(),
                    iconvertor.transStorageClass(request.getObjectStorageClass()));
        }

        if (request.getWebSiteRedirectLocation() != null) {
            putHeader(headers, iheaders.websiteRedirectLocationHeader(), request.getWebSiteRedirectLocation());
        }

        if (request.getContentDisposition() != null) {
            putHeader(headers, Constants.CommonHeaders.CONTENT_DISPOSITION, request.getContentDisposition());
        }

        if (request.getContentEncoding() != null) {
            putHeader(headers, Constants.CommonHeaders.CONTENT_ENCODING, request.getContentEncoding());
        }

        if (request.getContentLanguage() != null) {
            putHeader(headers, Constants.CommonHeaders.CONTENT_LANGUAGE, request.getContentLanguage());
        }

        if (request.getContentType() != null) {
            putHeader(headers, Constants.CommonHeaders.CONTENT_TYPE, request.getContentType());
        }

        if (request.getCacheControl() != null) {
            putHeader(headers, Constants.CommonHeaders.CACHE_CONTROL, request.getCacheControl());
        }

        if (request.getExpires() != null) {
            putHeader(headers, Constants.CommonHeaders.EXPIRES, request.getExpires());
        }

        this.transRequestPaymentHeaders(request, headers, iheaders);
        putHeader(headers, iheaders.metadataDirectiveHeader(),
                request.isRemoveUnset() ? Constants.DERECTIVE_REPLACE : Constants.DERECTIVE_REPLACE_NEW);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SpecialParamEnum.METADATA.getOriginalStringCode(), "");
        if (request.getVersionId() != null) {
            params.put(ObsRequestParams.VERSION_ID, request.getVersionId());
        }

        return new TransResult(headers, params, null);
    }

    TransResult transCopyPartRequest(CopyPartRequest request) throws ServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(ObsRequestParams.PART_NUMBER, String.valueOf(request.getPartNumber()));
        params.put(ObsRequestParams.UPLOAD_ID, request.getUploadId());

        Map<String, String> headers = new HashMap<String, String>();
        IHeaders iheaders = this.getIHeaders();

        String sourceKey = RestUtils.encodeUrlString(request.getSourceBucketName()) + "/"
                + RestUtils.encodeUrlString(request.getSourceObjectKey());
        if (ServiceUtils.isValid(request.getVersionId())) {
            sourceKey += "?versionId=" + request.getVersionId().trim();
        }
        putHeader(headers, iheaders.copySourceHeader(), sourceKey);

        if (request.getByteRangeStart() != null) {
            String rangeEnd = request.getByteRangeEnd() != null ? String.valueOf(request.getByteRangeEnd()) : "";
            String range = String.format("bytes=%s-%s", request.getByteRangeStart(), rangeEnd);
            putHeader(headers, iheaders.copySourceRangeHeader(), range);
        }

        this.transRequestPaymentHeaders(request, headers, iheaders);
        this.transSseCHeaders(request.getSseCHeaderDestination(), headers, iheaders);
        this.transSseCSourceHeaders(request.getSseCHeaderSource(), headers, iheaders);

        return new TransResult(headers, params, null);
    }

    TransResult transListObjectsRequest(ListObjectsRequest listObjectsRequest) {
        Map<String, String> params = new HashMap<String, String>();
        if (listObjectsRequest.getPrefix() != null) {
            params.put(ObsRequestParams.PREFIX, listObjectsRequest.getPrefix());
        }
        if (listObjectsRequest.getDelimiter() != null) {
            params.put(ObsRequestParams.DELIMITER, listObjectsRequest.getDelimiter());
        }
        if (listObjectsRequest.getMaxKeys() > 0) {
            params.put(ObsRequestParams.MAX_KEYS, String.valueOf(listObjectsRequest.getMaxKeys()));
        }

        if (listObjectsRequest.getMarker() != null) {
            params.put(ObsRequestParams.MARKER, listObjectsRequest.getMarker());
        }

        Map<String, String> headers = new HashMap<String, String>();
        transRequestPaymentHeaders(listObjectsRequest, headers, this.getIHeaders());
        if (listObjectsRequest.getListTimeout() > 0) {
            putHeader(headers, this.getIHeaders().listTimeoutHeader(),
                    String.valueOf(listObjectsRequest.getListTimeout()));
        }

        return new TransResult(headers, params, null);
    }

    TransResult transListContentSummaryRequest(ListContentSummaryRequest listContentSummaryRequest) {
        Map<String, String> params = new HashMap<String, String>();
        if (listContentSummaryRequest.getPrefix() != null) {
            params.put(ObsRequestParams.PREFIX, listContentSummaryRequest.getPrefix());
        }
        if (listContentSummaryRequest.getDelimiter() != null) {
            params.put(ObsRequestParams.DELIMITER, listContentSummaryRequest.getDelimiter());
        }
        if (listContentSummaryRequest.getMaxKeys() > 0) {
            params.put(ObsRequestParams.MAX_KEYS, String.valueOf(listContentSummaryRequest.getMaxKeys()));
        }

        if (listContentSummaryRequest.getMarker() != null) {
            params.put(ObsRequestParams.MARKER, listContentSummaryRequest.getMarker());
        }

        params.put(SpecialParamEnum.LISTCONTENTSUMMARY.getOriginalStringCode(), "");

        Map<String, String> headers = new HashMap<String, String>();
        transRequestPaymentHeaders(listContentSummaryRequest, headers, this.getIHeaders());
        if (listContentSummaryRequest.getListTimeout() > 0) {
            putHeader(headers, this.getIHeaders().listTimeoutHeader(),
                    String.valueOf(listContentSummaryRequest.getListTimeout()));
        }
        return new TransResult(headers, params, null);
    }

    TransResult transUploadPartRequest(UploadPartRequest request) throws ServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(ObsRequestParams.PART_NUMBER, String.valueOf(request.getPartNumber()));
        params.put(ObsRequestParams.UPLOAD_ID, request.getUploadId());

        Map<String, String> headers = new HashMap<String, String>();
        IHeaders iheaders = this.getIHeaders();

        if (ServiceUtils.isValid(request.getContentMd5())) {
            headers.put(CommonHeaders.CONTENT_MD5, request.getContentMd5().trim());
        }

        this.transRequestPaymentHeaders(request, headers, iheaders);
        this.transSseCHeaders(request.getSseCHeader(), headers, iheaders);

        long contentLength = -1L;
        if (null != request.getFile()) {
            long fileSize = request.getFile().length();
            long offset = (request.getOffset() >= 0 && request.getOffset() < fileSize) ? request.getOffset() : 0;
            long partSize = (request.getPartSize() != null && request.getPartSize() > 0
                    && request.getPartSize() <= (fileSize - offset)) ? request.getPartSize() : fileSize - offset;
            contentLength = partSize;

            try {
                if (request.isAttachMd5() && !ServiceUtils.isValid(request.getContentMd5())) {
                    headers.put(CommonHeaders.CONTENT_MD5, ServiceUtils.toBase64(
                            ServiceUtils.computeMD5Hash(new FileInputStream(request.getFile()), partSize, offset)));
                }
                request.setInput(new FileInputStream(request.getFile()));
                long skipByte = request.getInput().skip(offset);
                if (log.isDebugEnabled()) {
                    log.debug("Skip " + skipByte + " bytes; offset : " + offset);
                }
            } catch (Exception e) {
                ServiceUtils.closeStream(request.getInput());
                throw new ServiceException(e);
            }
        } else if (null != request.getInput()) {
            if (request.getPartSize() != null && request.getPartSize() > 0) {
                contentLength = request.getPartSize();
            }
        }

        if (request.getInput() != null && request.getProgressListener() != null) {
            ProgressManager progressManager = new SimpleProgressManager(contentLength, 0, request.getProgressListener(),
                    request.getProgressInterval() > 0 ? request.getProgressInterval()
                            : ObsConstraint.DEFAULT_PROGRESS_INTERVAL);
            request.setInput(new ProgressInputStream(request.getInput(), progressManager));
        }

        String contentType = Mimetypes.getInstance().getMimetype(request.getObjectKey());
        headers.put(CommonHeaders.CONTENT_TYPE, contentType);

        if (contentLength > -1) {
            this.putHeader(headers, CommonHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        }
        RequestBody body = request.getInput() == null ? null
                : new RepeatableRequestEntity(request.getInput(), contentType, contentLength, this.obsProperties);
        return new TransResult(headers, params, body);
    }

    void transSseKmsHeaders(SseKmsHeader kmsHeader, Map<String, String> headers, IHeaders iheaders) {
        if (kmsHeader == null) {
            return;
        }

        String sseKmsEncryption = this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS
                ? "aws:" + kmsHeader.getSSEAlgorithm().getCode() : kmsHeader.getSSEAlgorithm().getCode();
        putHeader(headers, iheaders.sseKmsHeader(), ServiceUtils.toValid(sseKmsEncryption));
        if (ServiceUtils.isValid(kmsHeader.getKmsKeyId())) {
            putHeader(headers, iheaders.sseKmsKeyHeader(), kmsHeader.getKmsKeyId());
        }

        if (ServiceUtils.isValid(kmsHeader.getProjectId())) {
            putHeader(headers, iheaders.sseKmsProjectIdHeader(), kmsHeader.getProjectId());
        }
    }

    RequestBody createRequestBody(String mimeType, String content) throws ServiceException {
        try {
            if (log.isTraceEnabled()) {
                try {
                    log.trace("Entity Content:" + content);
                } catch (Exception e) {
                }
            }
            return RequestBody.create(MediaType.parse(mimeType), content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(e);
        }
    }

    void putHeader(Map<String, String> headers, String key, String value) {
        if (ServiceUtils.isValid(key)) {
            headers.put(key, value);
        }
    }
    
    HeaderResponse build(Response res) {
        HeaderResponse response = new HeaderResponse();
        setResponseHeaders(response, this.cleanResponseHeaders(res));
        setStatusCode(response, res.code());
        return response;
    }
    
    static HeaderResponse build(Map<String, Object> responseHeaders) {
        HeaderResponse response = new HeaderResponse();
        setResponseHeaders(response, responseHeaders);
        return response;
    }
    
    GetBucketFSStatusResult getOptionInfoResult(Response response) {

        Headers headers = response.headers();

        Map<String, List<String>> map = headers.toMultimap();
        String maxAge = headers.get(Constants.CommonHeaders.ACCESS_CONTROL_MAX_AGE);

        IHeaders iheaders = this.getIHeaders();
        FSStatusEnum status = FSStatusEnum.getValueFromCode(headers.get(iheaders.fsFileInterfaceHeader()));

        BucketTypeEnum bucketType = BucketTypeEnum.OBJECT;
        if (FSStatusEnum.ENABLED == status) {
            bucketType = BucketTypeEnum.PFS;
        }

        GetBucketFSStatusResult output = new GetBucketFSStatusResult(
                headers.get(Constants.CommonHeaders.ACCESS_CONTROL_ALLOW_ORIGIN),
                map.get(Constants.CommonHeaders.ACCESS_CONTROL_ALLOW_HEADERS),
                maxAge == null ? 0 : Integer.parseInt(maxAge),
                map.get(Constants.CommonHeaders.ACCESS_CONTROL_ALLOW_METHODS),
                map.get(Constants.CommonHeaders.ACCESS_CONTROL_EXPOSE_HEADERS),
                StorageClassEnum.getValueFromCode(headers.get(iheaders.defaultStorageClassHeader())),
                headers.get(iheaders.bucketRegionHeader()), headers.get(iheaders.serverVersionHeader()), status,
                AvailableZoneEnum.getValueFromCode(headers.get(iheaders.azRedundancyHeader())),
                headers.get(iheaders.epidHeader()), bucketType);

        setResponseHeaders(output, this.cleanResponseHeaders(response));
        setStatusCode(output, response.code());
        return output;
    }
    
    Map<String, Object> cleanResponseHeaders(Response response) {
        Map<String, List<String>> map = response.headers().toMultimap();
        return ServiceUtils.cleanRestMetadataMap(map, this.getIHeaders().headerPrefix(),
                this.getIHeaders().headerMetaPrefix());
    }
    
    static void setStatusCode(HeaderResponse response, int statusCode) {
        response.setStatusCode(statusCode);
    }
    
    AbstractAuthentication getAuthentication() {
        return Constants.AUTHTICATION_MAP.get(this.getProviderCredentials().getAuthType());
    }

    SpecialParamEnum getSpecialParamForStorageClass() {
        return this.getProviderCredentials().getAuthType() == AuthTypeEnum.OBS ? SpecialParamEnum.STORAGECLASS
                : SpecialParamEnum.STORAGEPOLICY;
    }
    
    boolean prepareRESTHeaderAclForV2(Map<String, String> metadata, AccessControlList acl) {
        String restHeaderAclValue = null;
        if (acl == AccessControlList.REST_CANNED_PRIVATE) {
            restHeaderAclValue = Constants.ACL_PRIVATE;
        } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ) {
            restHeaderAclValue = Constants.ACL_PUBLIC_READ;
        } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ_WRITE) {
            restHeaderAclValue = Constants.ACL_PUBLIC_READ_WRITE;
        } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ_DELIVERED) {
            restHeaderAclValue = Constants.ACL_PUBLIC_READ;
        } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ_WRITE_DELIVERED) {
            restHeaderAclValue = Constants.ACL_PUBLIC_READ_WRITE;
        } else if (acl == AccessControlList.REST_CANNED_AUTHENTICATED_READ) {
            restHeaderAclValue = Constants.ACL_AUTHENTICATED_READ;
        } else if (acl == AccessControlList.REST_CANNED_BUCKET_OWNER_READ) {
            restHeaderAclValue = Constants.ACL_BUCKET_OWNER_READ;
        } else if (acl == AccessControlList.REST_CANNED_BUCKET_OWNER_FULL_CONTROL) {
            restHeaderAclValue = Constants.ACL_BUCKET_OWNER_FULL_CONTROL;
        } else if (acl == AccessControlList.REST_CANNED_LOG_DELIVERY_WRITE) {
            restHeaderAclValue = Constants.ACL_LOG_DELIVERY_WRITE;
        }
        String aclHeader = this.getIHeaders().aclHeader();
        if (restHeaderAclValue != null) {
            metadata.put(aclHeader, restHeaderAclValue);
        }
        return metadata.containsKey(aclHeader);
    }
    
    boolean prepareRESTHeaderAclForOBS(Map<String, String> metadata, AccessControlList acl) throws ServiceException {
        String restHeaderAclValue = null;
        boolean invalid = false;
        if (acl == AccessControlList.REST_CANNED_PRIVATE) {
            restHeaderAclValue = Constants.ACL_PRIVATE;
        } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ) {
            restHeaderAclValue = Constants.ACL_PUBLIC_READ;
        } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ_WRITE) {
            restHeaderAclValue = Constants.ACL_PUBLIC_READ_WRITE;
        } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ_DELIVERED) {
            restHeaderAclValue = Constants.ACL_PUBLIC_READ_DELIVERED;
        } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ_WRITE_DELIVERED) {
            restHeaderAclValue = Constants.ACL_PUBLIC_READ_WRITE_DELIVERED;
        } else if (acl == AccessControlList.REST_CANNED_AUTHENTICATED_READ) {
            restHeaderAclValue = Constants.ACL_AUTHENTICATED_READ;
            invalid = true;
        } else if (acl == AccessControlList.REST_CANNED_BUCKET_OWNER_READ) {
            restHeaderAclValue = Constants.ACL_BUCKET_OWNER_READ;
            invalid = true;
        } else if (acl == AccessControlList.REST_CANNED_BUCKET_OWNER_FULL_CONTROL) {
            restHeaderAclValue = Constants.ACL_BUCKET_OWNER_FULL_CONTROL;
            invalid = true;
        } else if (acl == AccessControlList.REST_CANNED_LOG_DELIVERY_WRITE) {
            restHeaderAclValue = Constants.ACL_LOG_DELIVERY_WRITE;
            invalid = true;
        }
        if (invalid) {
            log.info("Invalid Canned ACL:" + restHeaderAclValue);
        }

        String aclHeader = this.getIHeaders().aclHeader();
        if (restHeaderAclValue != null) {
            metadata.put(aclHeader, restHeaderAclValue);
        }
        return metadata.containsKey(aclHeader);
    }

    boolean prepareRESTHeaderAclObject(Map<String, String> metadata, AccessControlList acl) throws ServiceException {
        return this.getProviderCredentials().getAuthType() == AuthTypeEnum.OBS
                ? this.prepareRESTHeaderAclForOBSObject(metadata, acl) : this.prepareRESTHeaderAclForV2(metadata, acl);
    }

    boolean prepareRESTHeaderAclForOBSObject(Map<String, String> metadata, AccessControlList acl)
            throws ServiceException {
        String restHeaderAclValue = null;
        boolean invalid = false;
        if (acl == AccessControlList.REST_CANNED_PRIVATE) {
            restHeaderAclValue = Constants.ACL_PRIVATE;
        } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ) {
            restHeaderAclValue = Constants.ACL_PUBLIC_READ;
        } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ_WRITE) {
            restHeaderAclValue = Constants.ACL_PUBLIC_READ_WRITE;
        } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ_DELIVERED) {
            restHeaderAclValue = Constants.ACL_PUBLIC_READ;
        } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ_WRITE_DELIVERED) {
            restHeaderAclValue = Constants.ACL_PUBLIC_READ_WRITE;
        } else if (acl == AccessControlList.REST_CANNED_AUTHENTICATED_READ) {
            restHeaderAclValue = Constants.ACL_AUTHENTICATED_READ;
            invalid = true;
        } else if (acl == AccessControlList.REST_CANNED_BUCKET_OWNER_READ) {
            restHeaderAclValue = Constants.ACL_BUCKET_OWNER_READ;
            invalid = true;
        } else if (acl == AccessControlList.REST_CANNED_BUCKET_OWNER_FULL_CONTROL) {
            restHeaderAclValue = Constants.ACL_BUCKET_OWNER_FULL_CONTROL;
            invalid = true;
        } else if (acl == AccessControlList.REST_CANNED_LOG_DELIVERY_WRITE) {
            restHeaderAclValue = Constants.ACL_LOG_DELIVERY_WRITE;
            invalid = true;
        }
        if (invalid) {
            log.info("Invalid Canned ACL:" + restHeaderAclValue);
        }

        String aclHeader = this.getIHeaders().aclHeader();
        if (restHeaderAclValue != null) {
            metadata.put(aclHeader, restHeaderAclValue);
        }
        return metadata.containsKey(aclHeader);
    }

    boolean prepareRESTHeaderAcl(Map<String, String> metadata, AccessControlList acl) throws ServiceException {
        return this.getProviderCredentials().getAuthType() == AuthTypeEnum.OBS
                ? this.prepareRESTHeaderAclForOBS(metadata, acl) : this.prepareRESTHeaderAclForV2(metadata, acl);
    }
    
    protected String getCredential(String shortDate, String accessKey) {
        return new StringBuilder(accessKey).append("/").append(shortDate).append("/")
                .append(ObsConstraint.DEFAULT_BUCKET_LOCATION_VALUE).append("/").append(Constants.SERVICE).append("/")
                .append(Constants.REQUEST_TAG).toString();
    }
    
    protected AuthTypeEnum getApiVersion(String bucketName) throws ServiceException {
        if (!ServiceUtils.isValid(bucketName)) {
            return parseAuthTypeInResponse("");
        }
        AuthTypeEnum apiVersion = apiVersionCache.getApiVersionInCache(bucketName);
        if (apiVersion == null) {
            try {
                segmentLock.lock(bucketName);
                apiVersion = apiVersionCache.getApiVersionInCache(bucketName);
                if (apiVersion == null) {
                    apiVersion = parseAuthTypeInResponse(bucketName);
                    apiVersionCache.addApiVersion(bucketName, apiVersion);
                }
            } finally {
                segmentLock.unlock(bucketName);
            }
        }
        return apiVersion;
    }
    
    protected static void setResponseHeaders(HeaderResponse response, Map<String, Object> responseHeaders) {
        response.setResponseHeaders(responseHeaders);
    }
    
    private AuthTypeEnum parseAuthTypeInResponse(String bucketName) throws ServiceException {
        Response response;
        try {
            response = getAuthTypeNegotiationResponseImpl(bucketName);
        } catch (ServiceException e) {
            if (e.getResponseCode() == 404 || e.getResponseCode() <= 0 || e.getResponseCode() == 408
                    || e.getResponseCode() >= 500) {
                throw e;
            } else {
                return AuthTypeEnum.V2;
            }
        }
        String apiVersion;
        return (response.code() == 200 && (apiVersion = response.headers().get("x-obs-api")) != null
                && apiVersion.compareTo("3.0") >= 0) ? AuthTypeEnum.OBS : AuthTypeEnum.V2;
    }
    
    private Response getAuthTypeNegotiationResponseImpl(String bucketName) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("apiversion", "");
        return performRestForApiVersion(bucketName, null, requestParameters, null);
    }
}

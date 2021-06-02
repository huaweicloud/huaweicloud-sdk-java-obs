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


package com.obs.services.internal.service;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.Constants;
import com.obs.services.internal.Constants.CommonHeaders;
import com.obs.services.internal.Constants.ObsRequestParams;
import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.ProgressManager;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.SimpleProgressManager;
import com.obs.services.internal.handler.XmlResponsesSaxParser;
import com.obs.services.internal.io.HttpMethodReleaseInputStream;
import com.obs.services.internal.io.ProgressInputStream;
import com.obs.services.internal.utils.Mimetypes;
import com.obs.services.internal.utils.RestUtils;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.CopyObjectRequest;
import com.obs.services.model.CopyObjectResult;
import com.obs.services.model.DeleteObjectRequest;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.DeleteObjectsRequest;
import com.obs.services.model.DeleteObjectsResult;
import com.obs.services.model.GetObjectAclRequest;
import com.obs.services.model.GetObjectMetadataRequest;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObsObject;
import com.obs.services.model.ProgressListener;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.SetObjectAclRequest;
import com.obs.services.model.SetObjectMetadataRequest;
import com.obs.services.model.SpecialParamEnum;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.fs.DropFileResult;
import com.obs.services.model.fs.ObsFSAttribute;
import com.obs.services.model.fs.ObsFSFile;
import com.obs.services.model.fs.ReadFileResult;

import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class ObsObjectBaseService extends ObsBucketAdvanceService {
    private static final ILogger log = LoggerBuilder.getLogger(ObsObjectBaseService.class);
    
    protected boolean doesObjectExistImpl(GetObjectMetadataRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        this.transSseCHeaders(request.getSseCHeader(), headers, this.getIHeaders());
        this.transRequestPaymentHeaders(request, headers, this.getIHeaders());

        Map<String, String> params = new HashMap<String, String>();
        if (request.getVersionId() != null) {
            params.put(ObsRequestParams.VERSION_ID, request.getVersionId());
        }
        boolean doesObjectExist = false;
        try {
            Response response = performRestHead(request.getBucketName(), request.getObjectKey(), params, headers);
            if (200 == response.code()) {
                doesObjectExist = true;
            }
        } catch (ServiceException ex) {
            if (404 == ex.getResponseCode()) {
                doesObjectExist = false;
            } else {
                throw ex;
            }
        }
        return doesObjectExist;
    }
    
    protected ObsFSFile putObjectImpl(PutObjectRequest request) throws ServiceException {

        TransResult result = null;
        Response response;
        boolean isExtraAclPutRequired;
        AccessControlList acl = request.getAcl();
        try {
            result = this.transPutObjectRequest(request);

            isExtraAclPutRequired = !prepareRESTHeaderAcl(result.getHeaders(), acl);

            response = performRestPut(request.getBucketName(), request.getObjectKey(), result.getHeaders(), null,
                    result.getBody(), true);
        } finally {
            if (result != null && result.getBody() != null && request.isAutoClose()) {
                if (result.getBody() instanceof Closeable) {
                    ServiceUtils.closeStream((Closeable) result.getBody());
                }
            }
        }

        ObsFSFile ret = new ObsFSFile(request.getBucketName(), request.getObjectKey(),
                response.header(CommonHeaders.ETAG), response.header(this.getIHeaders().versionIdHeader()),
                StorageClassEnum.getValueFromCode(response.header(this.getIHeaders().storageClassHeader())),
                this.getObjectUrl(request.getBucketName(), request.getObjectKey()));
        Map<String, Object> map = this.cleanResponseHeaders(response);
        setResponseHeaders(ret, map);
        setStatusCode(ret, response.code());
        if (isExtraAclPutRequired && acl != null) {
            try {
                putAclImpl(request.getBucketName(), request.getObjectKey(), acl, null, request.isRequesterPays());
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("Try to set object acl error", e);
                }
            }
        }
        return ret;
    }
    
    protected ObsObject getObjectImpl(GetObjectRequest request) throws ServiceException {
        TransResult result = this.transGetObjectRequest(request);
        if (request.getRequestParameters() != null) {
            result.getParams().putAll(request.getRequestParameters());
        }
        return (ObsObject) this.getObjectImpl(false, request.getBucketName(), request.getObjectKey(),
                result.getHeaders(), result.getParams(), request.getProgressListener(), request.getProgressInterval());
    }
    
    protected Object getObjectImpl(boolean headOnly, String bucketName, String objectKey, Map<String, String> headers,
            Map<String, String> params, ProgressListener progressListener, long progressInterval)
                    throws ServiceException {
        Response response;
        if (headOnly) {
            response = performRestHead(bucketName, objectKey, params, headers);
        } else {
            response = performRestGet(bucketName, objectKey, params, headers);
        }

        ObsFSAttribute objMetadata = this.getObsFSAttributeFromResponse(response);

        if (headOnly) {
            response.close();
            return objMetadata;
        }
        ReadFileResult obsObject = new ReadFileResult();
        obsObject.setObjectKey(objectKey);
        obsObject.setBucketName(bucketName);
        obsObject.setMetadata(objMetadata);
        // pmd error message: CloseResource - Ensure that resources like this
        // InputStream object are closed after use
        // 该接口是下载对象，需要将流返回给客户（调用方），我们不能关闭这个流
        InputStream input = response.body().byteStream(); // NOPMD
        if (progressListener != null) {
            ProgressManager progressManager = new SimpleProgressManager(objMetadata.getContentLength(), 0,
                    progressListener,
                    progressInterval > 0 ? progressInterval : ObsConstraint.DEFAULT_PROGRESS_INTERVAL);
            input = new ProgressInputStream(input, progressManager);
        }

        int readBufferSize = obsProperties.getIntProperty(ObsConstraint.READ_BUFFER_SIZE,
                ObsConstraint.DEFAULT_READ_BUFFER_STREAM);
        if (readBufferSize > 0) {
            input = new BufferedInputStream(input, readBufferSize);
        }

        obsObject.setObjectContent(input);
        return obsObject;
    }
    
    protected DeleteObjectsResult deleteObjectsImpl(DeleteObjectsRequest deleteObjectsRequest) throws ServiceException {
        String xml = this.getIConvertor().transKeyAndVersion(deleteObjectsRequest.getKeyAndVersions(),
                deleteObjectsRequest.isQuiet());
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(xml));
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.DELETE.getOriginalStringCode(), "");

        transRequestPaymentHeaders(deleteObjectsRequest, metadata, this.getIHeaders());
        Response httpResponse = performRestPost(deleteObjectsRequest.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), false);
        this.verifyResponseContentType(httpResponse);

        DeleteObjectsResult ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.DeleteObjectsHandler.class, true).getMultipleDeleteResult();
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }
    
    protected DeleteObjectResult deleteObjectImpl(DeleteObjectRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        if (request.getVersionId() != null) {
            requestParameters.put(ObsRequestParams.VERSION_ID, request.getVersionId());
        }

        Response response = performRestDelete(request.getBucketName(), request.getObjectKey(), requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        DropFileResult result = new DropFileResult(
                Boolean.valueOf(response.header(this.getIHeaders().deleteMarkerHeader())), request.getObjectKey(),
                response.header(this.getIHeaders().versionIdHeader()));
        Map<String, Object> map = this.cleanResponseHeaders(response);
        setResponseHeaders(result, map);
        setStatusCode(result, response.code());
        return result;
    }
    
    protected CopyObjectResult copyObjectImpl(CopyObjectRequest request) throws ServiceException {

        TransResult result = this.transCopyObjectRequest(request);

        AccessControlList acl = request.getAcl();
        boolean isExtraAclPutRequired = !prepareRESTHeaderAcl(result.getHeaders(), acl);

        Response response = performRestPut(request.getDestinationBucketName(), request.getDestinationObjectKey(),
                result.getHeaders(), null, null, false);

        this.verifyResponseContentType(response);

        XmlResponsesSaxParser.CopyObjectResultHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(response), XmlResponsesSaxParser.CopyObjectResultHandler.class, false);
        CopyObjectResult copyRet = new CopyObjectResult(handler.getETag(), handler.getLastModified(),
                response.header(this.getIHeaders().versionIdHeader()),
                response.header(this.getIHeaders().copySourceVersionIdHeader()),
                StorageClassEnum.getValueFromCode(response.header(this.getIHeaders().storageClassHeader())));
        Map<String, Object> map = this.cleanResponseHeaders(response);
        setResponseHeaders(copyRet, map);
        setStatusCode(copyRet, response.code());
        if (isExtraAclPutRequired && acl != null) {
            if (log.isDebugEnabled()) {
                log.debug("Creating object with a non-canned ACL using REST, so an extra ACL Put is required");
            }
            try {
                putAclImpl(request.getDestinationBucketName(), request.getDestinationObjectKey(), acl, null,
                        request.isRequesterPays());
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("Try to set object acl error", e);
                }
            }
        }

        return copyRet;
    }
    
    protected ObjectMetadata setObjectMetadataImpl(SetObjectMetadataRequest request) {
        TransResult result = this.transSetObjectMetadataRequest(request);
        Response response = performRestPut(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
                result.getParams(), result.getBody(), true);
        return this.getObsFSAttributeFromResponse(response);
    }
    
    protected ObsFSAttribute getObjectMetadataImpl(GetObjectMetadataRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        this.transSseCHeaders(request.getSseCHeader(), headers, this.getIHeaders());
        this.transRequestPaymentHeaders(request, headers, this.getIHeaders());

        Map<String, String> params = new HashMap<String, String>();
        if (request.getVersionId() != null) {
            params.put(ObsRequestParams.VERSION_ID, request.getVersionId());
        }
        return (ObsFSAttribute) this.getObjectImpl(true, request.getBucketName(), request.getObjectKey(), headers,
                params, null, -1);
    }
    
    protected HeaderResponse setObjectAclImpl(SetObjectAclRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");
        if (request.getVersionId() != null) {
            requestParameters.put(ObsRequestParams.VERSION_ID, request.getVersionId());
        }
        RequestBody entity = null;
        if (ServiceUtils.isValid(request.getCannedACL())) {
            request.setAcl(this.getIConvertor().transCannedAcl(request.getCannedACL().trim()));
        }
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        boolean isExtraAclPutRequired = !prepareRESTHeaderAclObject(metadata, request.getAcl());
        if (isExtraAclPutRequired) {
            String aclAsXml = request.getAcl() == null ? ""
                    : this.getIConvertor().transAccessControlList(request.getAcl(), false);
            metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(aclAsXml.length()));
            entity = createRequestBody(Mimetypes.MIMETYPE_XML, aclAsXml);
        }

        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), request.getObjectKey(), metadata, requestParameters,
                entity, true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }
    
    protected AccessControlList getObjectAclImpl(GetObjectAclRequest getObjectAclRequest) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");
        if (ServiceUtils.isValid(getObjectAclRequest.getVersionId())) {
            requestParameters.put(ObsRequestParams.VERSION_ID, getObjectAclRequest.getVersionId().trim());
        }

        Response httpResponse = performRestGet(getObjectAclRequest.getBucketName(), getObjectAclRequest.getObjectKey(),
                requestParameters, transRequestPaymentHeaders(getObjectAclRequest, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        AccessControlList ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.AccessControlListHandler.class, false).getAccessControlList();
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }
    
    protected String getObjectUrl(String bucketName, String objectKey) {
        boolean pathStyle = this.isPathStyle();
        boolean https = this.getHttpsOnly();
        boolean isCname = this.isCname();
        return new StringBuilder().append(https ? "https://" : "http://")
                .append(pathStyle || isCname ? "" : bucketName + ".").append(this.getEndpoint()).append(":")
                .append(https ? this.getHttpsPort() : this.getHttpPort()).append("/")
                .append(pathStyle ? bucketName + "/" : "").append(RestUtils.uriEncode(objectKey, false)).toString();
    }
    
    private ObsFSAttribute getObsFSAttributeFromResponse(Response response) {
        Date lastModifiedDate = null;
        String lastModified = response.header(CommonHeaders.LAST_MODIFIED);
        if (lastModified != null) {
            try {
                lastModifiedDate = ServiceUtils.parseRfc822Date(lastModified);
            } catch (ParseException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Response last-modified is not well-format", e);
                }
            }
        }
        ObsFSAttribute objMetadata = new ObsFSAttribute();
        objMetadata.setLastModified(lastModifiedDate);
        objMetadata.setContentEncoding(response.header(CommonHeaders.CONTENT_ENCODING));
        objMetadata.setContentType(response.header(CommonHeaders.CONTENT_TYPE));
        
        objMetadata.setContentDisposition(response.header(CommonHeaders.CONTENT_DISPOSITION));
        objMetadata.setContentLanguage(response.header(CommonHeaders.CONTENT_LANGUAGE));
        objMetadata.setCacheControl(response.header(CommonHeaders.CACHE_CONTROL));
        objMetadata.setExpires(response.header(CommonHeaders.EXPIRES));
        
        String contentLength = response.header(CommonHeaders.CONTENT_LENGTH);
        if (contentLength != null) {
            try {
                objMetadata.setContentLength(Long.parseLong(contentLength));
            } catch (NumberFormatException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Response content-length is not well-format", e);
                }
            }
        }
        String fsMode;
        if ((fsMode = response.header(this.getIHeaders().fsModeHeader())) != null) {
            objMetadata.setMode(Integer.parseInt(fsMode));
        }
        objMetadata.setWebSiteRedirectLocation(response.header(this.getIHeaders().websiteRedirectLocationHeader()));
        objMetadata.setObjectStorageClass(
                StorageClassEnum.getValueFromCode(response.header(this.getIHeaders().storageClassHeader())));

        String etag = response.header(CommonHeaders.ETAG);
        objMetadata.setEtag(etag);
        if (etag != null && !etag.contains("-")) {
            String md5 = etag;
            if (md5.startsWith("\"")) {
                md5 = md5.substring(1);
            }
            if (md5.endsWith("\"")) {
                md5 = md5.substring(0, md5.length() - 1);
            }
            try {
                objMetadata.setContentMd5(ServiceUtils.toBase64(ServiceUtils.fromHex(md5)));
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getMessage(), e);
                }
            }
        }

        objMetadata.setAppendable("Appendable".equals(response.header(this.getIHeaders().objectTypeHeader())));
        String nextPosition = response.header(this.getIHeaders().nextPositionHeader(), "-1");
        objMetadata.setNextPosition(Long.parseLong(nextPosition));
        if (objMetadata.getNextPosition() == -1L) {
            objMetadata.setNextPosition(Long.parseLong(response.header(Constants.CommonHeaders.CONTENT_LENGTH, "-1")));
        }

        objMetadata.getMetadata().putAll(this.cleanResponseHeaders(response));
        setStatusCode(objMetadata, response.code());
        return objMetadata;
    }
}

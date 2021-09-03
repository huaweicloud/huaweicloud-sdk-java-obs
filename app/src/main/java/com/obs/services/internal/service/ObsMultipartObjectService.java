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

import java.util.HashMap;
import java.util.Map;

import com.obs.services.internal.Constants.CommonHeaders;
import com.obs.services.internal.Constants.ObsRequestParams;
import com.obs.services.internal.RepeatableRequestEntity;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.handler.XmlResponsesSaxParser;
import com.obs.services.internal.io.HttpMethodReleaseInputStream;
import com.obs.services.internal.utils.Mimetypes;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AbortMultipartUploadRequest;
import com.obs.services.model.CompleteMultipartUploadRequest;
import com.obs.services.model.CompleteMultipartUploadResult;
import com.obs.services.model.CopyPartRequest;
import com.obs.services.model.CopyPartResult;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.InitiateMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.ListMultipartUploadsRequest;
import com.obs.services.model.ListPartsRequest;
import com.obs.services.model.ListPartsResult;
import com.obs.services.model.MultipartUploadListing;
import com.obs.services.model.SpecialParamEnum;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.UploadPartRequest;
import com.obs.services.model.UploadPartResult;

import okhttp3.Response;

public abstract class ObsMultipartObjectService extends ObsObjectBaseService {
    protected InitiateMultipartUploadResult initiateMultipartUploadImpl(InitiateMultipartUploadRequest request)
            throws ServiceException {

        TransResult result = this.transInitiateMultipartUploadRequest(request);

        this.prepareRESTHeaderAcl(result.getHeaders(), request.getAcl());

        Response httpResponse = performRestPost(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
                result.getParams(), null, false, false, request.isEncodeHeaders());

        this.verifyResponseContentType(httpResponse);

        InitiateMultipartUploadResult multipartUpload = getXmlResponseSaxParser()
                .parse(new HttpMethodReleaseInputStream(httpResponse),
                        XmlResponsesSaxParser.InitiateMultipartUploadHandler.class, true)
                .getInitiateMultipartUploadResult();
        setHeadersAndStatus(multipartUpload, httpResponse);
        return multipartUpload;
    }
    
    protected HeaderResponse abortMultipartUploadImpl(AbortMultipartUploadRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(ObsRequestParams.UPLOAD_ID, request.getUploadId());

        Response response = performRestDelete(request.getBucketName(), request.getObjectKey(), requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        return build(response);
    }

    protected CompleteMultipartUploadResult completeMultipartUploadImpl(CompleteMultipartUploadRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(ObsRequestParams.UPLOAD_ID, request.getUploadId());
        if (request.getEncodingType() != null) {
            requestParameters.put(ObsRequestParams.ENCODING_TYPE, request.getEncodingType());
        }

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPost(request.getBucketName(), request.getObjectKey(), metadata,
                requestParameters, createRequestBody(Mimetypes.MIMETYPE_XML,
                        this.getIConvertor().transCompleteMultipartUpload(request.getPartEtag())),
                false);

        this.verifyResponseContentType(response);

        XmlResponsesSaxParser.CompleteMultipartUploadHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(response), XmlResponsesSaxParser.CompleteMultipartUploadHandler.class,
                true);

        String versionId = response.header(this.getIHeaders().versionIdHeader());

        CompleteMultipartUploadResult ret = new CompleteMultipartUploadResult(handler.getBucketName(),
                handler.getObjectKey(), handler.getEtag(), handler.getLocation(), versionId,
                this.getObjectUrl(handler.getBucketName(), handler.getObjectKey()));

        setHeadersAndStatus(ret, response);
        return ret;
    }

    protected MultipartUploadListing listMultipartUploadsImpl(ListMultipartUploadsRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.UPLOADS.getOriginalStringCode(), "");
        if (request.getPrefix() != null) {
            requestParameters.put(ObsRequestParams.PREFIX, request.getPrefix());
        }
        if (request.getDelimiter() != null) {
            requestParameters.put(ObsRequestParams.DELIMITER, request.getDelimiter());
        }
        if (request.getMaxUploads() != null) {
            requestParameters.put(ObsRequestParams.MAX_UPLOADS, request.getMaxUploads().toString());
        }
        if (request.getKeyMarker() != null) {
            requestParameters.put(ObsRequestParams.KEY_MARKER, request.getKeyMarker());
        }
        if (request.getUploadIdMarker() != null) {
            requestParameters.put(ObsRequestParams.UPLOAD_ID_MARKER, request.getUploadIdMarker());
        }
        if (request.getEncodingType() != null) {
            requestParameters.put(ObsRequestParams.ENCODING_TYPE, request.getEncodingType());
        }

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListMultipartUploadsHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListMultipartUploadsHandler.class,
                true);

        MultipartUploadListing listResult = new MultipartUploadListing.Builder()
                .bucketName(handler.getBucketName() == null 
                        ? request.getBucketName() : handler.getBucketName())
                .keyMarker(handler.getKeyMarker() == null 
                        ? request.getKeyMarker() : handler.getKeyMarker())
                .uploadIdMarker(handler.getUploadIdMarker() == null 
                        ? request.getUploadIdMarker() : handler.getUploadIdMarker())
                .nextKeyMarker(handler.getNextKeyMarker())
                .nextUploadIdMarker(handler.getNextUploadIdMarker())
                .prefix(handler.getPrefix() == null 
                        ? request.getPrefix() : handler.getPrefix())
                .maxUploads(handler.getMaxUploads())
                .truncated(handler.isTruncated())
                .multipartTaskList(handler.getMultipartUploadList())
                .delimiter(handler.getDelimiter() == null 
                        ? request.getDelimiter() : handler.getDelimiter())
                .commonPrefixes(handler.getCommonPrefixes().toArray(
                        new String[handler.getCommonPrefixes().size()]))
                .builder();
        setHeadersAndStatus(listResult, httpResponse);
        return listResult;
    }
    
    protected ListPartsResult listPartsImpl(ListPartsRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(ObsRequestParams.UPLOAD_ID, request.getUploadId());
        if (null != request.getMaxParts()) {
            requestParameters.put(ObsRequestParams.MAX_PARTS, request.getMaxParts().toString());
        }
        if (null != request.getPartNumberMarker()) {
            requestParameters.put(ObsRequestParams.PART_NUMBER_MARKER, request.getPartNumberMarker().toString());
        }
        if (null != request.getEncodingType()) {
            requestParameters.put(ObsRequestParams.ENCODING_TYPE, request.getEncodingType());
        }

        Response httpResponse = performRestGet(request.getBucketName(), request.getKey(), requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListPartsHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListPartsHandler.class, true);

        ListPartsResult result = new ListPartsResult.Builder()
                .bucket(handler.getBucketName() == null ? request.getBucketName() : handler.getBucketName())
                .key(handler.getObjectKey() == null ? request.getKey() : handler.getObjectKey())
                .uploadId(handler.getUploadId() == null ? request.getUploadId() : handler.getUploadId())
                .initiator(handler.getInitiator())
                .owner(handler.getOwner())
                .storageClass(StorageClassEnum.getValueFromCode(handler.getStorageClass()))
                .multipartList(handler.getMultiPartList())
                .maxParts(handler.getMaxParts())
                .isTruncated(handler.isTruncated())
                .partNumberMarker(handler.getPartNumberMarker() == null
                        ? (request.getPartNumberMarker() == null ? null : request.getPartNumberMarker().toString())
                        : handler.getPartNumberMarker())
                .nextPartNumberMarker(handler.getNextPartNumberMarker()).builder();

        setHeadersAndStatus(result, httpResponse);
        return result;
    }
    
    protected UploadPartResult uploadPartImpl(UploadPartRequest request) throws ServiceException {
        TransResult result = null;
        Response response;
        try {
            result = this.transUploadPartRequest(request);
            response = performRestPut(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
                    result.getParams(), result.getBody(), true);
        } finally {
            if (result != null && result.getBody() != null && request.isAutoClose()) {
                RepeatableRequestEntity entity = (RepeatableRequestEntity) result.getBody();
                ServiceUtils.closeStream(entity);
            }
        }
        UploadPartResult ret = new UploadPartResult();
        ret.setEtag(response.header(CommonHeaders.ETAG));
        ret.setPartNumber(request.getPartNumber());
        setHeadersAndStatus(ret, response);
        return ret;
    }
    
    protected CopyPartResult copyPartImpl(CopyPartRequest request) throws ServiceException {

        TransResult result = this.transCopyPartRequest(request);
        Response response = this.performRestPut(request.getDestinationBucketName(), request.getDestinationObjectKey(),
                result.getHeaders(), result.getParams(), null, false);
        this.verifyResponseContentType(response);

        CopyPartResult ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
                XmlResponsesSaxParser.CopyPartResultHandler.class, true).getCopyPartResult(request.getPartNumber());

        setHeadersAndStatus(ret, response);
        return ret;
    }
}

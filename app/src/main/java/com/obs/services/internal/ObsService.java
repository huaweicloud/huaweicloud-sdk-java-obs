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

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.Constants.CommonHeaders;
import com.obs.services.internal.Constants.ObsRequestParams;
import com.obs.services.internal.handler.XmlResponsesSaxParser;
import com.obs.services.internal.io.HttpMethodReleaseInputStream;
import com.obs.services.internal.io.ProgressInputStream;
import com.obs.services.internal.security.BasicSecurityKey;
import com.obs.services.internal.task.BlockRejectedExecutionHandler;
import com.obs.services.internal.task.DefaultTaskProgressStatus;
import com.obs.services.internal.utils.AbstractAuthentication;
import com.obs.services.internal.utils.JSONChange;
import com.obs.services.internal.utils.Mimetypes;
import com.obs.services.internal.utils.RestUtils;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.internal.utils.V2Authentication;
import com.obs.services.internal.utils.V4Authentication;
import com.obs.services.model.AbortMultipartUploadRequest;
import com.obs.services.model.AbstractBulkRequest;
import com.obs.services.model.AbstractTemporarySignatureRequest;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.AppendObjectRequest;
import com.obs.services.model.AppendObjectResult;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.BucketCors;
import com.obs.services.model.BucketEncryption;
import com.obs.services.model.BucketLocationResponse;
import com.obs.services.model.BucketLoggingConfiguration;
import com.obs.services.model.BucketMetadataInfoRequest;
import com.obs.services.model.BucketMetadataInfoResult;
import com.obs.services.model.BucketNotificationConfiguration;
import com.obs.services.model.BucketPolicyResponse;
import com.obs.services.model.BucketTagInfo;
import com.obs.services.model.BucketVersioningConfiguration;
import com.obs.services.model.CompleteMultipartUploadRequest;
import com.obs.services.model.CompleteMultipartUploadResult;
import com.obs.services.model.CopyObjectRequest;
import com.obs.services.model.CopyPartRequest;
import com.obs.services.model.CopyPartResult;
import com.obs.services.model.CreateBucketRequest;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.DeleteObjectsRequest;
import com.obs.services.model.DeleteObjectsResult;
import com.obs.services.model.GrantAndPermission;
import com.obs.services.model.GroupGrantee;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.InitiateMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.LifecycleConfiguration;
import com.obs.services.model.ListBucketsRequest;
import com.obs.services.model.ListBucketsResult;
import com.obs.services.model.ListMultipartUploadsRequest;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ListPartsRequest;
import com.obs.services.model.ListPartsResult;
import com.obs.services.model.ListVersionsRequest;
import com.obs.services.model.ListVersionsResult;
import com.obs.services.model.ModifyObjectRequest;
import com.obs.services.model.ModifyObjectResult;
import com.obs.services.model.MultipartUploadListing;
import com.obs.services.model.ObjectListing;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObsBucket;
import com.obs.services.model.OptionsInfoRequest;
import com.obs.services.model.Permission;
import com.obs.services.model.PolicyTempSignatureRequest;
import com.obs.services.model.PostSignatureRequest;
import com.obs.services.model.PostSignatureResponse;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.ReadAheadRequest;
import com.obs.services.model.ReadAheadResult;
import com.obs.services.model.RenameObjectRequest;
import com.obs.services.model.RenameObjectResult;
import com.obs.services.model.ReplicationConfiguration;
import com.obs.services.model.RequestPaymentConfiguration;
import com.obs.services.model.RestoreObjectRequest;
import com.obs.services.model.RestoreObjectRequest.RestoreObjectStatus;
import com.obs.services.model.RestoreObjectResult;
import com.obs.services.model.SetBucketAclRequest;
import com.obs.services.model.SetBucketCorsRequest;
import com.obs.services.model.SetBucketDirectColdAccessRequest;
import com.obs.services.model.SetBucketEncryptionRequest;
import com.obs.services.model.SetBucketLifecycleRequest;
import com.obs.services.model.SetBucketLoggingRequest;
import com.obs.services.model.SetBucketNotificationRequest;
import com.obs.services.model.SetBucketPolicyRequest;
import com.obs.services.model.SetBucketQuotaRequest;
import com.obs.services.model.SetBucketReplicationRequest;
import com.obs.services.model.SetBucketRequestPaymentRequest;
import com.obs.services.model.SetBucketStoragePolicyRequest;
import com.obs.services.model.SetBucketTaggingRequest;
import com.obs.services.model.SetBucketVersioningRequest;
import com.obs.services.model.SetBucketWebsiteRequest;
import com.obs.services.model.SetObjectAclRequest;
import com.obs.services.model.SpecialParamEnum;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.TaskCallback;
import com.obs.services.model.TaskProgressListener;
import com.obs.services.model.TemporarySignatureRequest;
import com.obs.services.model.TemporarySignatureResponse;
import com.obs.services.model.TruncateObjectRequest;
import com.obs.services.model.TruncateObjectResult;
import com.obs.services.model.UploadPartRequest;
import com.obs.services.model.UploadPartResult;
import com.obs.services.model.V4PostSignatureResponse;
import com.obs.services.model.VersionOrDeleteMarker;
import com.obs.services.model.WebsiteConfiguration;
import com.obs.services.model.fs.DropFileResult;
import com.obs.services.model.fs.GetBucketFSStatusResult;
import com.obs.services.model.fs.ListContentSummaryRequest;
import com.obs.services.model.fs.ListContentSummaryResult;
import com.obs.services.model.fs.ObsFSAttribute;
import com.obs.services.model.fs.ObsFSFile;
import com.obs.services.model.fs.ReadFileResult;
import com.obs.services.model.fs.RenameRequest;
import com.obs.services.model.fs.RenameResult;
import com.obs.services.model.fs.SetBucketFSStatusRequest;
import com.obs.services.model.fs.TruncateFileRequest;
import com.obs.services.model.fs.TruncateFileResult;
import com.obs.services.model.fs.WriteFileRequest;
import com.obs.services.model.CopyObjectResult;
import com.obs.services.model.GetObjectMetadataRequest;
import com.obs.services.model.ProgressListener;
import com.obs.services.model.SetObjectMetadataRequest;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.ObsObject;
import com.obs.services.model.BucketQuota;
import com.obs.services.model.BucketStoragePolicyConfiguration;
import com.obs.services.model.BucketStorageInfo;
import com.obs.services.model.GetObjectAclRequest;
import com.obs.services.model.DeleteObjectRequest;
import com.obs.services.model.ReadAheadQueryResult;
import com.obs.services.model.BucketDirectColdAccess;
import com.oef.services.model.CreateAsynchFetchJobsResult;
import com.oef.services.model.DisPolicy;
import com.oef.services.model.GetDisPolicyResult;
import com.oef.services.model.QueryAsynchFetchJobsResult;
import com.oef.services.model.QueryExtensionPolicyResult;
import com.oef.services.model.RequestParamEnum;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ObsService extends RequestConvertor {

    private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.ObsClient");

    protected ObsService() {

    }

    protected HeaderResponse setBucketVersioningImpl(SetBucketVersioningRequest request) throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(SpecialParamEnum.VERSIONING.getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        String xml = this.getIConvertor().transVersioningConfiguration(request.getBucketName(),
                request.getStatus() != null ? request.getStatus().getCode() : null);

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParams,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
        return this.build(response);
    }

    protected void verifyResponseContentType(Response response) throws ServiceException {
        if (this.obsProperties.getBoolProperty(ObsConstraint.VERIFY_RESPONSE_CONTENT_TYPE, true)) {
            String contentType = response.header(Constants.CommonHeaders.CONTENT_TYPE);
            if (!Mimetypes.MIMETYPE_XML.equalsIgnoreCase(contentType)
                    && !Mimetypes.MIMETYPE_TEXT_XML.equalsIgnoreCase(contentType)) {
                throw new ServiceException(
                        "Expected XML document response from OBS but received content type " + contentType);
            }
        }
    }

    protected BucketVersioningConfiguration getBucketVersioningImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(SpecialParamEnum.VERSIONING.getOriginalStringCode(), "");

        Response response = performRestGet(request.getBucketName(), null, requestParams,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(response);

        BucketVersioningConfiguration ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
                XmlResponsesSaxParser.BucketVersioningHandler.class, false).getVersioningStatus();
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse setBucketRequestPaymentImpl(SetBucketRequestPaymentRequest request)
            throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(SpecialParamEnum.REQUEST_PAYMENT.getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        String xml = this.getIConvertor().transRequestPaymentConfiguration(request.getBucketName(),
                request.getPayer() != null ? request.getPayer().getCode() : null);

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParams,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
        return this.build(response);
    }

    protected RequestPaymentConfiguration getBucketRequestPaymentImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(SpecialParamEnum.REQUEST_PAYMENT.getOriginalStringCode(), "");

        Response response = performRestGet(request.getBucketName(), null, requestParams,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(response);

        RequestPaymentConfiguration ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
                XmlResponsesSaxParser.RequestPaymentHandler.class, false).getRequestPaymentConfiguration();
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected ListVersionsResult listVersionsImpl(ListVersionsRequest request) throws ServiceException {

        TransResult result = this.transListVersionsRequest(request);

        Response response = performRestGet(request.getBucketName(), null, result.getParams(), result.getHeaders());

        this.verifyResponseContentType(response);

        XmlResponsesSaxParser.ListVersionsHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(response), XmlResponsesSaxParser.ListVersionsHandler.class, true);
        List<VersionOrDeleteMarker> partialItems = handler.getItems();

        ListVersionsResult listVersionsResult = new ListVersionsResult(
                handler.getBucketName() == null ? request.getBucketName() : handler.getBucketName(),
                handler.getRequestPrefix() == null ? request.getPrefix() : handler.getRequestPrefix(),
                handler.getKeyMarker() == null ? request.getKeyMarker() : handler.getKeyMarker(),
                handler.getNextKeyMarker(),
                handler.getVersionIdMarker() == null ? request.getVersionIdMarker() : handler.getVersionIdMarker(),
                handler.getNextVersionIdMarker(), String.valueOf(handler.getRequestMaxKeys()),
                handler.isListingTruncated(), partialItems.toArray(new VersionOrDeleteMarker[partialItems.size()]),
                handler.getCommonPrefixes(), response.header(this.getIHeaders().bucketRegionHeader()),
                handler.getDelimiter() == null ? request.getDelimiter() : handler.getDelimiter());
        setResponseHeaders(listVersionsResult, this.cleanResponseHeaders(response));
        setStatusCode(listVersionsResult, response.code());
        return listVersionsResult;

    }

    protected BucketPolicyResponse getBucketPolicyImpl(BaseBucketRequest request) throws ServiceException {
        try {
            Map<String, String> requestParameters = new HashMap<String, String>();
            requestParameters.put(SpecialParamEnum.POLICY.getOriginalStringCode(), "");

            Response response = performRestGet(request.getBucketName(), null, requestParameters,
                    transRequestPaymentHeaders(request, null, this.getIHeaders()));
            BucketPolicyResponse ret = new BucketPolicyResponse(response.body().string());
            setResponseHeaders(ret, this.cleanResponseHeaders(response));
            setStatusCode(ret, response.code());
            return ret;
        } catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    protected BucketNotificationConfiguration getBucketNotificationConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.NOTIFICATION.getOriginalStringCode(), "");
        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketNotificationConfiguration result = getXmlResponseSaxParser()
                .parse(new HttpMethodReleaseInputStream(httpResponse),
                        XmlResponsesSaxParser.BucketNotificationConfigurationHandler.class, false)
                .getBucketNotificationConfiguration();
        setResponseHeaders(result, this.cleanResponseHeaders(httpResponse));
        setStatusCode(result, httpResponse.code());
        return result;
    }

    protected HeaderResponse setBucketNotificationImpl(SetBucketNotificationRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.NOTIFICATION.getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        String xml = this.getIConvertor()
                .transBucketNotificationConfiguration(request.getBucketNotificationConfiguration());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);

        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse setBucketPolicyImpl(SetBucketPolicyRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.POLICY.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_TEXT_PLAIN);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_TEXT_PLAIN, request.getPolicy()), true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse deleteBucketPolicyImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.POLICY.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected InitiateMultipartUploadResult initiateMultipartUploadImpl(InitiateMultipartUploadRequest request)
            throws ServiceException {

        TransResult result = this.transInitiateMultipartUploadRequest(request);

        this.prepareRESTHeaderAcl(result.getHeaders(), request.getAcl());

        Response httpResponse = performRestPost(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
                result.getParams(), null, false);

        this.verifyResponseContentType(httpResponse);

        InitiateMultipartUploadResult multipartUpload = getXmlResponseSaxParser()
                .parse(new HttpMethodReleaseInputStream(httpResponse),
                        XmlResponsesSaxParser.InitiateMultipartUploadHandler.class, true)
                .getInitiateMultipartUploadResult();
        setResponseHeaders(multipartUpload, this.cleanResponseHeaders(httpResponse));
        setStatusCode(multipartUpload, httpResponse.code());
        return multipartUpload;
    }

    protected HeaderResponse abortMultipartUploadImpl(AbortMultipartUploadRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(ObsRequestParams.UPLOAD_ID, request.getUploadId());

        Response response = performRestDelete(request.getBucketName(), request.getObjectKey(), requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected CompleteMultipartUploadResult completeMultipartUploadImpl(CompleteMultipartUploadRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(ObsRequestParams.UPLOAD_ID, request.getUploadId());

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
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
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

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListMultipartUploadsHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListMultipartUploadsHandler.class,
                true);

        MultipartUploadListing listResult = new MultipartUploadListing(
                handler.getBucketName() == null ? request.getBucketName() : handler.getBucketName(),
                handler.getKeyMarker() == null ? request.getKeyMarker() : handler.getKeyMarker(),
                handler.getUploadIdMarker() == null ? request.getUploadIdMarker() : handler.getUploadIdMarker(),
                handler.getNextKeyMarker(), handler.getNextUploadIdMarker(),
                handler.getPrefix() == null ? request.getPrefix() : handler.getPrefix(), handler.getMaxUploads(),
                handler.isTruncated(), handler.getMultipartUploadList(),
                handler.getDelimiter() == null ? request.getDelimiter() : handler.getDelimiter(),
                handler.getCommonPrefixes().toArray(new String[handler.getCommonPrefixes().size()]));
        setResponseHeaders(listResult, this.cleanResponseHeaders(httpResponse));
        setStatusCode(listResult, httpResponse.code());
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

        Response httpResponse = performRestGet(request.getBucketName(), request.getKey(), requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListPartsHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListPartsHandler.class, true);

        ListPartsResult result = new ListPartsResult(
                handler.getBucketName() == null ? request.getBucketName() : handler.getBucketName(),
                handler.getObjectKey() == null ? request.getKey() : handler.getObjectKey(),
                handler.getUploadId() == null ? request.getUploadId() : handler.getUploadId(),
                handler.getInitiator(), handler
                        .getOwner(),
                StorageClassEnum.getValueFromCode(handler.getStorageClass()), handler.getMultiPartList(),
                handler.getMaxParts(), handler.isTruncated(),
                handler.getPartNumberMarker() == null
                        ? (request.getPartNumberMarker() == null ? null : request.getPartNumberMarker().toString())
                        : handler.getPartNumberMarker(),
                handler.getNextPartNumberMarker());
        setResponseHeaders(result, this.cleanResponseHeaders(httpResponse));
        setStatusCode(result, httpResponse.code());
        return result;
    }

    protected WebsiteConfiguration getBucketWebsiteConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.WEBSITE.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        WebsiteConfiguration ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketWebsiteConfigurationHandler.class, false).getWebsiteConfig();
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }

    protected HeaderResponse setBucketWebsiteConfigurationImpl(SetBucketWebsiteRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.WEBSITE.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        String xml = this.getIConvertor().transWebsiteConfiguration(request.getWebsiteConfig());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse deleteBucketWebsiteConfigurationImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.WEBSITE.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected LifecycleConfiguration getBucketLifecycleConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LIFECYCLE.getOriginalStringCode(), "");

        Response response = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(response);

        LifecycleConfiguration ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
                XmlResponsesSaxParser.BucketLifecycleConfigurationHandler.class, false).getLifecycleConfig();
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse setBucketLifecycleConfigurationImpl(SetBucketLifecycleRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LIFECYCLE.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        String xml = this.getIConvertor().transLifecycleConfiguration(request.getLifecycleConfig());
        metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(xml));
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);

        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse deleteBucketLifecycleConfigurationImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LIFECYCLE.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
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

    protected boolean headBucketImpl(BaseBucketRequest request) throws ServiceException {
        try {
            performRestHead(request.getBucketName(), null, null,
                    transRequestPaymentHeaders(request, null, this.getIHeaders()));
            return true;
        } catch (ServiceException e) {
            if (e.getResponseCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    protected HeaderResponse setBucketFSStatusImpl(SetBucketFSStatusRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.FILEINTERFACE.getOriginalStringCode(), "");
        String xml = this.getIConvertor().transBucketFileInterface(request.getStatus());
        Response response = performRestPut(request.getBucketName(), null,
                transRequestPaymentHeaders(request, null, this.getIHeaders()), requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected TruncateFileResult truncateFileImpl(TruncateFileRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.TRUNCATE.getOriginalStringCode(), "");
        requestParameters.put(Constants.ObsRequestParams.LENGTH, String.valueOf(request.getNewLength()));

        Response response = performRestPut(request.getBucketName(), request.getObjectKey(),
                transRequestPaymentHeaders(request, null, this.getIHeaders()), requestParameters, null, true);
        TruncateFileResult result = new TruncateFileResult();
        setResponseHeaders(result, this.cleanResponseHeaders(response));
        setStatusCode(result, response.code());
        return result;
    }

    protected TruncateObjectResult truncateObjectImpl(TruncateObjectRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.TRUNCATE.getOriginalStringCode(), "");
        requestParameters.put(Constants.ObsRequestParams.LENGTH, String.valueOf(request.getNewLength()));

        Response response = performRestPut(request.getBucketName(), request.getObjectKey(),
                transRequestPaymentHeaders(request, null, this.getIHeaders()), requestParameters, null, true);
        TruncateObjectResult result = new TruncateObjectResult();
        setResponseHeaders(result, this.cleanResponseHeaders(response));
        setStatusCode(result, response.code());
        return result;
    }

    protected RenameResult renameFileImpl(RenameRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.RENAME.getOriginalStringCode(), "");
        requestParameters.put(Constants.ObsRequestParams.NAME, request.getNewObjectKey());

        Response response = performRestPost(request.getBucketName(), request.getObjectKey(),
                transRequestPaymentHeaders(request, null, this.getIHeaders()), requestParameters, null, true);
        RenameResult result = new RenameResult();
        setResponseHeaders(result, this.cleanResponseHeaders(response));
        setStatusCode(result, response.code());
        return result;
    }

    protected RenameObjectResult renameObjectImpl(RenameObjectRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.RENAME.getOriginalStringCode(), "");
        requestParameters.put(Constants.ObsRequestParams.NAME, request.getNewObjectKey());

        Response response = performRestPost(request.getBucketName(), request.getObjectKey(),
                transRequestPaymentHeaders(request, null, this.getIHeaders()), requestParameters, null, true);
        RenameObjectResult result = new RenameObjectResult();
        setResponseHeaders(result, this.cleanResponseHeaders(response));
        setStatusCode(result, response.code());
        return result;
    }

    protected GetBucketFSStatusResult getBucketMetadataImpl(BucketMetadataInfoRequest bucketMetadataInfoRequest)
            throws ServiceException {
        GetBucketFSStatusResult output = null;
        String origin = bucketMetadataInfoRequest.getOrigin();
        List<String> requestHeaders = bucketMetadataInfoRequest.getRequestHeaders();
        if (origin != null && requestHeaders != null && requestHeaders.size() > 0) {
            for (int i = 0; i < requestHeaders.size(); i++) {
                String value = requestHeaders.get(i);
                Map<String, String> headers = new HashMap<String, String>();
                headers.put(Constants.CommonHeaders.ORIGIN, origin);
                headers.put(Constants.CommonHeaders.ACCESS_CONTROL_REQUEST_HEADERS, value);
                transRequestPaymentHeaders(bucketMetadataInfoRequest, headers, this.getIHeaders());

                Response response = performRestHead(bucketMetadataInfoRequest.getBucketName(), null, null, headers);

                if (output == null) {
                    output = this.getOptionInfoResult(response);
                } else {
                    String header = response.header(Constants.CommonHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
                    if (header != null) {
                        if (!output.getAllowHeaders().contains(header)) {
                            output.getAllowHeaders().add(header);
                        }
                    }
                }
                response.close();
            }
        } else {
            Map<String, String> headers = new HashMap<String, String>();
            if (origin != null) {
                headers.put(Constants.CommonHeaders.ORIGIN, origin);
            }
            transRequestPaymentHeaders(bucketMetadataInfoRequest, headers, this.getIHeaders());

            Response response = performRestHead(bucketMetadataInfoRequest.getBucketName(), null, null, headers);
            output = this.getOptionInfoResult(response);
            response.close();
        }

        return output;
    }

    protected RestoreObjectStatus restoreObjectImpl(RestoreObjectRequest restoreObjectRequest) throws ServiceException {
        RestoreObjectResult restoreObjectResult = restoreObjectV2Impl(restoreObjectRequest);
        return transRestoreObjectResultToRestoreObjectStatus(restoreObjectResult);
    }

    protected RestoreObjectResult restoreObjectV2Impl(RestoreObjectRequest restoreObjectRequest)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.RESTORE.getOriginalStringCode(), "");
        if (restoreObjectRequest.getVersionId() != null) {
            requestParameters.put(ObsRequestParams.VERSION_ID, restoreObjectRequest.getVersionId());
        }
        Map<String, String> metadata = new HashMap<String, String>();
        String requestXmlElement = this.getIConvertor().transRestoreObjectRequest(restoreObjectRequest);
        metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(restoreObjectRequest, metadata, this.getIHeaders());

        Response response = this.performRestPost(restoreObjectRequest.getBucketName(),
                restoreObjectRequest.getObjectKey(), metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
        RestoreObjectResult ret = new RestoreObjectResult(restoreObjectRequest.getBucketName(),
                restoreObjectRequest.getObjectKey(), restoreObjectRequest.getVersionId());
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected BucketTagInfo getBucketTaggingImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.TAGGING.getOriginalStringCode(), "");
        Response httpResponse = this.performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketTagInfo result = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketTagInfoHandler.class, false).getBucketTagInfo();
        setResponseHeaders(result, this.cleanResponseHeaders(httpResponse));
        setStatusCode(result, httpResponse.code());
        return result;
    }

    protected HeaderResponse setBucketTaggingImpl(SetBucketTaggingRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.TAGGING.getOriginalStringCode(), "");
        Map<String, String> headers = new HashMap<String, String>();

        String requestXmlElement = this.getIConvertor().transBucketTagInfo(request.getBucketTagInfo());

        headers.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
        headers.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

        transRequestPaymentHeaders(request, headers, this.getIHeaders());

        Response response = this.performRestPut(request.getBucketName(), null, headers, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse deleteBucketTaggingImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.TAGGING.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected BucketEncryption getBucketEncryptionImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ENCRYPTION.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketEncryption ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketEncryptionHandler.class, false).getEncryption();

        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }

    protected HeaderResponse setBucketEncryptionImpl(SetBucketEncryptionRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ENCRYPTION.getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

        String encryptAsXml = request.getBucketEncryption() == null ? ""
                : this.getIConvertor().transBucketEcryption(request.getBucketEncryption());
        metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(encryptAsXml.length()));
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, encryptAsXml), true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse deleteBucketEncryptionImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ENCRYPTION.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected ReplicationConfiguration getBucketReplicationConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.REPLICATION.getOriginalStringCode(), "");
        Response httpResponse = this.performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        ReplicationConfiguration result = getXmlResponseSaxParser()
                .parse(new HttpMethodReleaseInputStream(httpResponse),
                        XmlResponsesSaxParser.BucketReplicationConfigurationHandler.class, false)
                .getReplicationConfiguration();
        setResponseHeaders(result, this.cleanResponseHeaders(httpResponse));
        setStatusCode(result, httpResponse.code());
        return result;
    }

    protected HeaderResponse setBucketReplicationConfigurationImpl(SetBucketReplicationRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.REPLICATION.getOriginalStringCode(), "");
        Map<String, String> headers = new HashMap<String, String>();

        String requestXmlElement = this.getIConvertor()
                .transReplicationConfiguration(request.getReplicationConfiguration());

        headers.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
        headers.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, headers, this.getIHeaders());

        Response response = this.performRestPut(request.getBucketName(), null, headers, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse deleteBucketReplicationConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.REPLICATION.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected TemporarySignatureResponse createTemporarySignatureResponse(AbstractTemporarySignatureRequest request)
            throws Exception {
        String requestMethod = request.getMethod() != null ? request.getMethod().getOperationType() : "GET";

        Map<String, Object> queryParams = new TreeMap<String, Object>();
        queryParams.putAll(request.getQueryParams());
        BasicSecurityKey securityKey = this.getProviderCredentials().getSecurityKey();
        String accessKey = securityKey.getAccessKey();
        String secretKey = securityKey.getSecretKey();
        String securityToken = securityKey.getSecurityToken();
        if (!queryParams.containsKey(this.getIHeaders().securityTokenHeader())) {
            if (ServiceUtils.isValid(securityToken)) {
                queryParams.put(this.getIHeaders().securityTokenHeader(), securityToken);
            }
        }

        String endpoint = this.getEndpoint();
        String bucketName = request.getBucketName();
        String objectKey = request.getObjectKey();
        String hostname = ServiceUtils.generateHostnameForBucket(bucketName, this.isPathStyle(), endpoint);
        String virtualBucketPath = "";
        String uriPath = "";
        String objectKeyPath = (objectKey != null) ? RestUtils.encodeUrlPath(objectKey, "/") : "";
        if (!endpoint.equals(hostname)) {
            int subdomainOffset = hostname.lastIndexOf("." + endpoint);
            if (subdomainOffset > 0) {
                virtualBucketPath = hostname.substring(0, subdomainOffset) + "/";
            }
            uriPath = objectKeyPath;
        } else {
            uriPath = (((!ServiceUtils.isValid(bucketName)) ? "" : bucketName.trim()) + "/" + objectKeyPath);
        }

        if (this.isCname()) {
            hostname = endpoint;
            uriPath = objectKeyPath;
            virtualBucketPath = endpoint + "/";
        }

        uriPath += "?";
        if (request.getSpecialParam() != null) {
            if (request.getSpecialParam() == SpecialParamEnum.STORAGECLASS
                    || request.getSpecialParam() == SpecialParamEnum.STORAGEPOLICY) {
                request.setSpecialParam(this.getSpecialParamForStorageClass());
            }
            uriPath += request.getSpecialParam().getOriginalStringCode() + "&";
        }

        String accessKeyIdPrefix = this.getProviderCredentials().getAuthType() == AuthTypeEnum.OBS ? "AccessKeyId="
                : "AWSAccessKeyId=";
        uriPath += accessKeyIdPrefix + accessKey;

        String expiresOrPolicy = "";
        String uriExpiresOrPolicy = "";
        if (request instanceof TemporarySignatureRequest) {
            TemporarySignatureRequest tempRequest = (TemporarySignatureRequest) request;
            long secondsSinceEpoch = tempRequest.getExpires() <= 0 ? ObsConstraint.DEFAULT_EXPIRE_SECONEDS
                    : tempRequest.getExpires();
            secondsSinceEpoch += System.currentTimeMillis() / 1000;
            expiresOrPolicy = String.valueOf(secondsSinceEpoch);
            uriExpiresOrPolicy = "&Expires=" + expiresOrPolicy;
        } else if (request instanceof PolicyTempSignatureRequest) {
            PolicyTempSignatureRequest policyRequest = (PolicyTempSignatureRequest) request;
            String policy = policyRequest.generatePolicy();
            expiresOrPolicy = ServiceUtils.toBase64(policy.getBytes(Constants.DEFAULT_ENCODING));
            uriExpiresOrPolicy = "&Policy=" + expiresOrPolicy;
        }
        uriPath += uriExpiresOrPolicy;

        StringBuilder temp = new StringBuilder(uriPath);
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                String key = RestUtils.uriEncode(entry.getKey(), false);

                temp.append("&");
                temp.append(key);
                temp.append("=");
                String value = RestUtils.uriEncode(entry.getValue().toString(), false);
                temp.append(value);
            }
        }
        uriPath = temp.toString();

        Map<String, String> headers = new HashMap<String, String>();
        headers.putAll(request.getHeaders());
        headers.put(CommonHeaders.HOST,
                hostname + ":" + (this.getHttpsOnly() ? this.getHttpsPort() : this.getHttpPort()));
        Map<String, String> actualSignedRequestHeaders = new TreeMap<String, String>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (ServiceUtils.isValid(entry.getKey())) {
                String key = entry.getKey().toLowerCase().trim();
                boolean validKey = false;
                if (Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key)
                        || key.startsWith(this.getRestHeaderPrefix()) || key.startsWith(Constants.OBS_HEADER_PREFIX)) {
                    validKey = true;
                } else if (requestMethod.equals("PUT") || requestMethod.equals("POST")) {
                    key = this.getRestMetadataPrefix() + key;
                    validKey = true;
                }
                if (validKey) {
                    String value = entry.getValue() == null ? "" : entry.getValue().trim();
                    if (key.startsWith(this.getRestMetadataPrefix())) {
                        value = RestUtils.uriEncode(value, true);
                    }
                    actualSignedRequestHeaders.put(entry.getKey().trim(), value);
                }
            }
        }

        String resource = "";
        if (request instanceof TemporarySignatureRequest) {
            resource = "/" + virtualBucketPath + uriPath;
        }

        AbstractAuthentication authentication = this.getAuthentication();
        if (authentication == null) {
            authentication = V2Authentication.getInstance();
        }
        String canonicalString = authentication.makeServiceCanonicalString(requestMethod, resource,
                actualSignedRequestHeaders, expiresOrPolicy, Constants.ALLOWED_RESOURCE_PARAMTER_NAMES);

        String signedCanonical = ServiceUtils.signWithHmacSha1(secretKey, canonicalString);
        String encodedCanonical = RestUtils.encodeUrlString(signedCanonical);
        uriPath += "&Signature=" + encodedCanonical;

        String signedUrl;
        if (this.getHttpsOnly()) {
            signedUrl = "https://";
        } else {
            signedUrl = "http://";
        }
        signedUrl += headers.get(CommonHeaders.HOST) + "/" + uriPath;
        TemporarySignatureResponse response = new TemporarySignatureResponse(signedUrl);
        response.getActualSignedRequestHeaders().putAll(actualSignedRequestHeaders);
        return response;
    }

    protected PostSignatureResponse createPostSignatureResponse(PostSignatureRequest request, boolean isV4)
            throws Exception {
        BasicSecurityKey securityKey = this.getProviderCredentials().getSecurityKey();
        String accessKey = securityKey.getAccessKey();
        String secretKey = securityKey.getSecretKey();
        String securityToken = securityKey.getSecurityToken();
        Date requestDate = request.getRequestDate() != null ? request.getRequestDate() : new Date();
        SimpleDateFormat expirationDateFormat = ServiceUtils.getExpirationDateFormat();
        Date expiryDate = request.getExpiryDate() == null ? new Date(requestDate.getTime()
                + (request.getExpires() <= 0 ? ObsConstraint.DEFAULT_EXPIRE_SECONEDS : request.getExpires()) * 1000)
                : request.getExpiryDate();

        String expiration = expirationDateFormat.format(expiryDate);

        StringBuilder originPolicy = new StringBuilder();
        originPolicy.append("{\"expiration\":").append("\"").append(expiration).append("\",")
                .append("\"conditions\":[");

        String shortDate = ServiceUtils.getShortDateFormat().format(requestDate);
        String longDate = ServiceUtils.getLongDateFormat().format(requestDate);
        String credential = this.getCredential(shortDate, accessKey);
        if (request.getConditions() != null && !request.getConditions().isEmpty()) {
            originPolicy.append(ServiceUtils.join(request.getConditions(), ",")).append(",");
        } else {
            Map<String, Object> params = new TreeMap<String, Object>();
            if (isV4) {
                params.put(Constants.V2_HEADER_PREFIX_CAMEL + "Algorithm", Constants.V4_ALGORITHM);
                params.put(Constants.V2_HEADER_PREFIX_CAMEL + "Date", longDate);
                params.put(Constants.V2_HEADER_PREFIX_CAMEL + "Credential", credential);
            }

            params.putAll(request.getFormParams());

            if (!params.containsKey(this.getIHeaders().securityTokenHeader())) {
                if (ServiceUtils.isValid(securityToken)) {
                    params.put(this.getIHeaders().securityTokenHeader(), securityToken);
                }
            }

            if (ServiceUtils.isValid(request.getBucketName())) {
                params.put("bucket", request.getBucketName());
            }

            if (ServiceUtils.isValid(request.getObjectKey())) {
                params.put("key", request.getObjectKey());
            }

            boolean matchAnyBucket = true;
            boolean matchAnyKey = true;

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (ServiceUtils.isValid(entry.getKey())) {
                    String key = entry.getKey().toLowerCase().trim();

                    if (key.equals("bucket")) {
                        matchAnyBucket = false;
                    } else if (key.equals("key")) {
                        matchAnyKey = false;
                    }

                    if (!Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key)
                            && !key.startsWith(this.getRestHeaderPrefix())
                            && !key.startsWith(Constants.OBS_HEADER_PREFIX) && !key.equals("acl")
                            && !key.equals("bucket") && !key.equals("key") && !key.equals("success_action_redirect")
                            && !key.equals("redirect") && !key.equals("success_action_status")) {
                        continue;
                    }
                    String value = entry.getValue() == null ? "" : entry.getValue().toString();
                    originPolicy.append("{\"").append(key).append("\":").append("\"").append(value).append("\"},");
                }
            }

            if (matchAnyBucket) {
                originPolicy.append("[\"starts-with\", \"$bucket\", \"\"],");
            }

            if (matchAnyKey) {
                originPolicy.append("[\"starts-with\", \"$key\", \"\"],");
            }

        }

        originPolicy.append("]}");
        String policy = ServiceUtils.toBase64(originPolicy.toString().getBytes(Constants.DEFAULT_ENCODING));

        if (isV4) {
            String signature = V4Authentication.caculateSignature(policy, shortDate, secretKey);
            return new V4PostSignatureResponse(policy, originPolicy.toString(), Constants.V4_ALGORITHM, credential,
                    longDate, signature, expiration);
        } else {
            String signature = AbstractAuthentication.caculateSignature(policy, secretKey);
            return new PostSignatureResponse(policy, originPolicy.toString(), signature, expiration, accessKey);
        }

    }

    protected TemporarySignatureResponse createV4TemporarySignature(TemporarySignatureRequest request)
            throws Exception {
        StringBuilder canonicalUri = new StringBuilder();
        String bucketName = request.getBucketName();
        String endpoint = this.getEndpoint();
        String objectKey = request.getObjectKey();

        if (!this.isCname()) {
            if (ServiceUtils.isValid(bucketName)) {
                if (this.isPathStyle() || !ServiceUtils.isBucketNameValidDNSName(bucketName)) {
                    canonicalUri.append("/").append(bucketName.trim());
                } else {
                    endpoint = bucketName.trim() + "." + endpoint;
                }
                if (ServiceUtils.isValid(objectKey)) {
                    canonicalUri.append("/").append(RestUtils.uriEncode(objectKey, false));
                }
            }
        } else {
            if (ServiceUtils.isValid(objectKey)) {
                canonicalUri.append("/").append(RestUtils.uriEncode(objectKey, false));
            }
        }

        if (this.isCname()) {
            endpoint = this.getEndpoint();
        }

        Map<String, String> headers = new TreeMap<String, String>();
        headers.putAll(request.getHeaders());
        Map<String, Object> queryParams = new TreeMap<String, Object>();
        queryParams.putAll(request.getQueryParams());

        Date requestDate = request.getRequestDate();
        if (requestDate == null) {
            requestDate = new Date();
        }
        if ((this.getHttpsOnly() && this.getHttpsPort() == 443) || (!this.getHttpsOnly() && this.getHttpPort() == 80)) {
            headers.put(CommonHeaders.HOST, endpoint);
        } else {
            headers.put(CommonHeaders.HOST,
                    endpoint + ":" + (this.getHttpsOnly() ? this.getHttpsPort() : this.getHttpPort()));
        }

        BasicSecurityKey securityKey = this.getProviderCredentials().getSecurityKey();
        String accessKey = securityKey.getAccessKey();
        String secretKey = securityKey.getSecretKey();
        String securityToken = securityKey.getSecurityToken();
        if (!queryParams.containsKey(this.getIHeaders().securityTokenHeader())) {
            if (ServiceUtils.isValid(securityToken)) {
                queryParams.put(this.getIHeaders().securityTokenHeader(), securityToken);
            }
        }

        String requestMethod = request.getMethod() != null ? request.getMethod().getOperationType() : "GET";

        StringBuilder signedHeaders = new StringBuilder();
        StringBuilder canonicalHeaders = new StringBuilder();
        int index = 0;
        Map<String, String> actualSignedRequestHeaders = new TreeMap<String, String>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (ServiceUtils.isValid(entry.getKey())) {
                String key = entry.getKey().toLowerCase().trim();
                boolean validKey = false;
                if (Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key)
                        || key.startsWith(this.getRestHeaderPrefix()) || key.startsWith(Constants.OBS_HEADER_PREFIX)) {
                    validKey = true;
                } else if (requestMethod.equals("PUT") || requestMethod.equals("POST")) {
                    key = this.getRestMetadataPrefix() + key;
                    validKey = true;
                }
                if (validKey) {
                    String value = entry.getValue() == null ? "" : entry.getValue().trim();
                    if (key.startsWith(this.getRestMetadataPrefix())) {
                        value = RestUtils.uriEncode(value, true);
                    }
                    signedHeaders.append(key);
                    canonicalHeaders.append(key).append(":").append(value).append("\n");
                    if (index++ != headers.size() - 1) {
                        signedHeaders.append(";");
                    }
                    actualSignedRequestHeaders.put(entry.getKey().trim(), value);
                }
            }
        }

        String shortDate = ServiceUtils.getShortDateFormat().format(requestDate);
        String longDate = ServiceUtils.getLongDateFormat().format(requestDate);

        queryParams.put(Constants.V2_HEADER_PREFIX_CAMEL + "Algorithm", Constants.V4_ALGORITHM);
        queryParams.put(Constants.V2_HEADER_PREFIX_CAMEL + "Credential", this.getCredential(shortDate, accessKey));
        queryParams.put(Constants.V2_HEADER_PREFIX_CAMEL + "Date", longDate);
        queryParams.put(Constants.V2_HEADER_PREFIX_CAMEL + "Expires",
                request.getExpires() <= 0 ? ObsConstraint.DEFAULT_EXPIRE_SECONEDS : request.getExpires());
        queryParams.put(Constants.V2_HEADER_PREFIX_CAMEL + "SignedHeaders", signedHeaders.toString());

        StringBuilder canonicalQueryString = new StringBuilder();

        StringBuilder signedUrl = new StringBuilder();
        if (this.getHttpsOnly()) {
            String securePortStr = this.getHttpsPort() == 443 ? "" : ":" + this.getHttpsPort();
            signedUrl.append("https://").append(endpoint).append(securePortStr);
        } else {
            String insecurePortStr = this.getHttpPort() == 80 ? "" : ":" + this.getHttpPort();
            signedUrl.append("http://").append(endpoint).append(insecurePortStr);
        }
        signedUrl.append(canonicalUri).append("?");

        if (request.getSpecialParam() != null) {
            if (request.getSpecialParam() == SpecialParamEnum.STORAGECLASS
                    || request.getSpecialParam() == SpecialParamEnum.STORAGEPOLICY) {
                request.setSpecialParam(this.getSpecialParamForStorageClass());
            }
            queryParams.put(request.getSpecialParam().getOriginalStringCode(), null);
        }

        index = 0;
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            if (ServiceUtils.isValid(entry.getKey())) {
                String key = RestUtils.uriEncode(entry.getKey(), false);

                canonicalQueryString.append(key).append("=");
                signedUrl.append(key);
                if (entry.getValue() != null) {
                    String value = RestUtils.uriEncode(entry.getValue().toString(), false);
                    canonicalQueryString.append(value);
                    signedUrl.append("=").append(value);
                } else {
                    canonicalQueryString.append("");
                }
                if (index++ != queryParams.size() - 1) {
                    canonicalQueryString.append("&");
                    signedUrl.append("&");
                }
            }
        }

        StringBuilder canonicalRequest = new StringBuilder(requestMethod).append("\n")
                .append(canonicalUri.length() == 0 ? "/" : canonicalUri).append("\n").append(canonicalQueryString)
                .append("\n").append(canonicalHeaders).append("\n").append(signedHeaders).append("\n")
                .append("UNSIGNED-PAYLOAD");

        StringBuilder stringToSign = new StringBuilder(Constants.V4_ALGORITHM).append("\n").append(longDate)
                .append("\n").append(shortDate).append("/").append(ObsConstraint.DEFAULT_BUCKET_LOCATION_VALUE)
                .append("/").append(Constants.SERVICE).append("/").append(Constants.REQUEST_TAG).append("\n")
                .append(V4Authentication.byteToHex((V4Authentication.sha256encode(canonicalRequest.toString()))));
        signedUrl.append("&").append(Constants.V2_HEADER_PREFIX_CAMEL).append("Signature=")
                .append(V4Authentication.caculateSignature(stringToSign.toString(), shortDate, secretKey));
        TemporarySignatureResponse response = new TemporarySignatureResponse(signedUrl.toString());
        response.getActualSignedRequestHeaders().putAll(actualSignedRequestHeaders);
        return response;
    }

    protected BucketMetadataInfoResult optionsImpl(String bucketName, String objectName, OptionsInfoRequest option)
            throws ServiceException {
        Map<String, String> metadata = new IdentityHashMap<String, String>();

        if (ServiceUtils.isValid(option.getOrigin())) {
            metadata.put(CommonHeaders.ORIGIN, option.getOrigin().trim());
        }

        for (int i = 0; option.getRequestMethod() != null && i < option.getRequestMethod().size(); i++) {
            metadata.put(new String(new StringBuilder(CommonHeaders.ACCESS_CONTROL_REQUEST_METHOD)),
                    option.getRequestMethod().get(i));
        }
        for (int i = 0; option.getRequestHeaders() != null && i < option.getRequestHeaders().size(); i++) {
            metadata.put(new String(new StringBuilder(CommonHeaders.ACCESS_CONTROL_REQUEST_HEADERS)),
                    option.getRequestHeaders().get(i));
        }
        transRequestPaymentHeaders(option.isRequesterPays(), metadata, this.getIHeaders());

        Response rsult = performRestOptions(bucketName, objectName, metadata, null, true);
        return getOptionInfoResult(rsult);

    }

    protected HeaderResponse deleteBucketCorsImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.CORS.getOriginalStringCode(), "");

        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected BucketCors getBucketCorsImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.CORS.getOriginalStringCode(), "");
        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);
        BucketCors ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketCorsHandler.class, false).getConfiguration();
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;

    }

    protected HeaderResponse setBucketCorsImpl(SetBucketCorsRequest request) throws ServiceException {
        String corsXML = request.getBucketCors() == null ? ""
                : this.getIConvertor().transBucketCors(request.getBucketCors());

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.CORS.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(corsXML));

        metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(corsXML.length()));
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, corsXML), true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse setBucketQuotaImpl(SetBucketQuotaRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.QUOTA.getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

        String quotaAsXml = request.getBucketQuota() == null ? ""
                : this.getIConvertor().transBucketQuota(request.getBucketQuota());
        metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(quotaAsXml.length()));
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, quotaAsXml), true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse setBucketStorageImpl(SetBucketStoragePolicyRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(this.getSpecialParamForStorageClass().getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        String xml = request.getBucketStorage() == null ? ""
                : this.getIConvertor().transStoragePolicy(request.getBucketStorage());
        metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(xml.length()));
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    /**
     * @param bucketName
     * @param objectKey
     * @param acl
     * @param versionId
     * @param isRequesterPays
     * @throws ServiceException
     */
    void putAclImpl(String bucketName, String objectKey, AccessControlList acl, String versionId,
            boolean isRequesterPays) throws ServiceException {
        if (acl != null) {
            Map<String, String> requestParameters = new HashMap<String, String>();
            requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");
            if (versionId != null) {
                requestParameters.put(Constants.ObsRequestParams.VERSION_ID, versionId);
            }

            Map<String, String> metadata = new HashMap<String, String>();
            metadata.put(Constants.CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
            String aclAsXml = this.getIConvertor().transAccessControlList(acl, !ServiceUtils.isValid(objectKey));
            metadata.put(Constants.CommonHeaders.CONTENT_LENGTH, String.valueOf(aclAsXml.length()));

            transRequestPaymentHeaders(isRequesterPays, metadata, this.getIHeaders());

            performRestPut(bucketName, objectKey, metadata, requestParameters,
                    createRequestBody(Mimetypes.MIMETYPE_XML, aclAsXml), true);
        }
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

    protected HeaderResponse setBucketAclImpl(SetBucketAclRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");
        RequestBody entity = null;
        if (ServiceUtils.isValid(request.getCannedACL())) {
            request.setAcl(this.getIConvertor().transCannedAcl(request.getCannedACL().trim()));
        }
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        boolean isExtraAclPutRequired = !prepareRESTHeaderAcl(metadata, request.getAcl());
        if (isExtraAclPutRequired) {
            String aclAsXml = request.getAcl() == null ? ""
                    : this.getIConvertor().transAccessControlList(request.getAcl(), true);
            metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(aclAsXml.length()));
            entity = createRequestBody(Mimetypes.MIMETYPE_XML, aclAsXml);
        }

        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters, entity, true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected ObsBucket createBucketImpl(CreateBucketRequest request) throws ServiceException {
        TransResult result = this.transCreateBucketRequest(request);
        String bucketName = request.getBucketName();
        AccessControlList acl = request.getAcl();

        boolean isExtraAclPutRequired = !prepareRESTHeaderAcl(result.getHeaders(), acl);

        Response response = performRestPut(bucketName, null, result.getHeaders(), null, result.getBody(), true);

        if (isExtraAclPutRequired && acl != null) {
            if (log.isDebugEnabled()) {
                log.debug("Creating bucket with a non-canned ACL using REST, so an extra ACL Put is required");
            }
            try {
                putAclImpl(bucketName, null, acl, null, false);
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("Try to set bucket acl error", e);
                }
            }
        }

        Map<String, Object> map = this.cleanResponseHeaders(response);
        ObsBucket bucket = new ObsBucket();
        bucket.setBucketName(bucketName);
        bucket.setLocation(request.getLocation());
        bucket.setAcl(acl);
        bucket.setBucketStorageClass(request.getBucketStorageClass());
        setResponseHeaders(bucket, map);
        setStatusCode(bucket, response.code());
        return bucket;
    }

    protected BucketLocationResponse getBucketLocationImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LOCATION.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketLocationResponse ret = new BucketLocationResponse(
                getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                        XmlResponsesSaxParser.BucketLocationHandler.class, false).getLocation());
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }

    protected BucketLoggingConfiguration getBucketLoggingConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LOGGING.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketLoggingConfiguration ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketLoggingHandler.class, false).getBucketLoggingStatus();

        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }

    protected AppendObjectResult appendObjectImpl(AppendObjectRequest request) throws ServiceException {
        TransResult result = null;
        Response response;
        boolean isExtraAclPutRequired;
        AccessControlList acl = request.getAcl();
        try {
            result = this.transAppendObjectRequest(request);

            isExtraAclPutRequired = !prepareRESTHeaderAcl(result.getHeaders(), acl);

            response = performRestPost(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
                    result.getParams(), result.getBody(), true);
        } finally {
            if (result != null && result.getBody() != null && request.isAutoClose()) {
                RepeatableRequestEntity entity = (RepeatableRequestEntity) result.getBody();
                ServiceUtils.closeStream(entity);
            }
        }
        String nextPosition = response.header(this.getIHeaders().nextPositionHeader());
        AppendObjectResult ret = new AppendObjectResult(request.getBucketName(), request.getObjectKey(),
                response.header(CommonHeaders.ETAG), nextPosition != null ? Long.parseLong(nextPosition) : -1,
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

    protected ObsFSFile writeFileImpl(WriteFileRequest request) throws ServiceException {
        TransResult result = null;
        Response response;
        boolean isExtraAclPutRequired;
        AccessControlList acl = request.getAcl();
        try {
            result = this.transWriteFileRequest(request);

            isExtraAclPutRequired = !prepareRESTHeaderAcl(result.getHeaders(), acl);

            response = performRestPut(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
                    result.getParams(), result.getBody(), true);
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

    protected ModifyObjectResult modifyObjectImpl(ModifyObjectRequest request) throws ServiceException {

        TransResult result = null;
        Response response;
        boolean isExtraAclPutRequired;
        AccessControlList acl = request.getAcl();
        try {
            result = this.transModifyObjectRequest(request);

            isExtraAclPutRequired = !prepareRESTHeaderAcl(result.getHeaders(), acl);

            response = performRestPut(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
                    result.getParams(), result.getBody(), true);
        } finally {
            if (result != null && result.getBody() != null && request.isAutoClose()) {
                if (result.getBody() instanceof Closeable) {
                    ServiceUtils.closeStream((Closeable) result.getBody());
                }
            }
        }
        ModifyObjectResult ret = new ModifyObjectResult();
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

    protected ObjectMetadata setObjectMetadataImpl(SetObjectMetadataRequest request) {
        TransResult result = this.transSetObjectMetadataRequest(request);
        Response response = performRestPut(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
                result.getParams(), result.getBody(), true);
        return this.getObsFSAttributeFromResponse(response);
    }

    protected ObsObject getObjectImpl(GetObjectRequest request) throws ServiceException {
        TransResult result = this.transGetObjectRequest(request);
        if (request.getRequestParameters() != null) {
            result.getParams().putAll(request.getRequestParameters());
        }
        return (ObsObject) this.getObjectImpl(false, request.getBucketName(), request.getObjectKey(),
                result.getHeaders(), result.getParams(), request.getProgressListener(), request.getProgressInterval());
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

    protected BucketQuota getBucketQuotaImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.QUOTA.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketQuota ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketQuotaHandler.class, false).getQuota();
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }

    protected BucketStoragePolicyConfiguration getBucketStoragePolicyImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(this.getSpecialParamForStorageClass().getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketStoragePolicyConfiguration ret = getXmlResponseSaxParser()
                .parse(new HttpMethodReleaseInputStream(httpResponse),
                        XmlResponsesSaxParser.BucketStoragePolicyHandler.class, false)
                .getStoragePolicy();
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }

    protected BucketStorageInfo getBucketStorageInfoImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.STORAGEINFO.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketStorageInfo ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketStorageInfoHandler.class, false).getStorageInfo();
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }

    protected AccessControlList getBucketAclImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        AccessControlList ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.AccessControlListHandler.class, false).getAccessControlList();
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
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

    protected ReadAheadResult readAheadObjectsImpl(ReadAheadRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(Constants.ObsRequestParams.READAHEAD, "");
        requestParameters.put(Constants.ObsRequestParams.PREFIX, request.getPrefix());

        Map<String, String> metadata = new HashMap<String, String>();
        String cacheControl = request.getCacheOption().getCode() + ", ttl=" + request.getTtl();
        metadata.put(ObsRequestParams.X_CACHE_CONTROL, cacheControl);

        Response response = performRestPost(request.getBucketName(), null, metadata, requestParameters, null, false);

        this.verifyResponseContentTypeForJson(response);

        String body;
        try {
            body = response.body().string();
        } catch (IOException e) {
            throw new ServiceException(e);
        }

        ReadAheadResult result = (ReadAheadResult) JSONChange.jsonToObj(new ReadAheadResult(), body);
        result.setResponseHeaders(this.cleanResponseHeaders(response));
        setStatusCode(result, response.code());

        return result;
    }

    protected ReadAheadResult deleteReadAheadObjectsImpl(String bucketName, String prefix) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(Constants.ObsRequestParams.READAHEAD, "");
        requestParameters.put(Constants.ObsRequestParams.PREFIX, prefix);

        Response response = performRestDelete(bucketName, null, requestParameters, false);

        this.verifyResponseContentTypeForJson(response);

        String body;
        try {
            body = response.body().string();
        } catch (IOException e) {
            throw new ServiceException(e);
        }

        ReadAheadResult result = (ReadAheadResult) JSONChange.jsonToObj(new ReadAheadResult(), body);
        result.setResponseHeaders(this.cleanResponseHeaders(response));
        setStatusCode(result, response.code());

        return result;
    }

    protected ReadAheadQueryResult queryReadAheadObjectsTaskImpl(String bucketName, String taskId)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(Constants.ObsRequestParams.READAHEAD, "");
        requestParameters.put(Constants.ObsRequestParams.TASKID, taskId);

        Response response = performRestGet(bucketName, null, requestParameters, null);

        this.verifyResponseContentTypeForJson(response);

        String body;
        try {
            body = response.body().string();
        } catch (IOException e) {
            throw new ServiceException(e);
        }

        ReadAheadQueryResult result = (ReadAheadQueryResult) JSONChange.jsonToObj(new ReadAheadQueryResult(), body);
        result.setResponseHeaders(this.cleanResponseHeaders(response));
        setStatusCode(result, response.code());

        return result;
    }

    protected BucketDirectColdAccess getBucketDirectColdAccessImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.DIRECTCOLDACCESS.getOriginalStringCode(), "");
        Response httpResponse = this.performRestGet(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        this.verifyResponseContentType(httpResponse);

        BucketDirectColdAccess result = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.BucketDirectColdAccessHandler.class, false).getBucketDirectColdAccess();
        setResponseHeaders(result, this.cleanResponseHeaders(httpResponse));
        setStatusCode(result, httpResponse.code());
        return result;
    }

    protected HeaderResponse setBucketDirectColdAccessImpl(SetBucketDirectColdAccessRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.DIRECTCOLDACCESS.getOriginalStringCode(), "");
        Map<String, String> headers = new HashMap<String, String>();

        String requestXmlElement = this.getIConvertor().transBucketDirectColdAccess(request.getAccess());

        headers.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
        headers.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, headers, this.getIHeaders());

        Response response = this.performRestPut(request.getBucketName(), null, headers, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse deleteBucketDirectColdAccessImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.DIRECTCOLDACCESS.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));

        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected ObjectListing listObjectsImpl(ListObjectsRequest listObjectsRequest) throws ServiceException {

        TransResult result = this.transListObjectsRequest(listObjectsRequest);

        Response httpResponse = performRestGet(listObjectsRequest.getBucketName(), null, result.getParams(),
                result.getHeaders());

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListObjectsHandler listObjectsHandler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListObjectsHandler.class, true);

        ObjectListing objList = new ObjectListing(listObjectsHandler.getObjects(),
                listObjectsHandler.getCommonPrefixes(),
                listObjectsHandler.getBucketName() == null ? listObjectsRequest.getBucketName()
                        : listObjectsHandler.getBucketName(),
                listObjectsHandler.isListingTruncated(),
                listObjectsHandler.getRequestPrefix() == null ? listObjectsRequest.getPrefix()
                        : listObjectsHandler.getRequestPrefix(),
                listObjectsHandler.getRequestMarker() == null ? listObjectsRequest.getMarker()
                        : listObjectsHandler.getRequestMarker(),
                listObjectsHandler.getRequestMaxKeys(),
                listObjectsHandler.getRequestDelimiter() == null ? listObjectsRequest.getDelimiter()
                        : listObjectsHandler.getRequestDelimiter(),
                listObjectsHandler.getMarkerForNextListing(),
                httpResponse.header(this.getIHeaders().bucketRegionHeader()),
                listObjectsHandler.getExtenedCommonPrefixes());
        setResponseHeaders(objList, this.cleanResponseHeaders(httpResponse));
        setStatusCode(objList, httpResponse.code());
        return objList;
    }

    protected ListContentSummaryResult listContentSummaryImpl(ListContentSummaryRequest listContentSummaryRequest)
            throws ServiceException {

        TransResult result = this.transListContentSummaryRequest(listContentSummaryRequest);

        Response httpResponse = performRestGet(listContentSummaryRequest.getBucketName(), null, result.getParams(),
                null);

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListContentSummaryHandler listContentSummaryHandler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListContentSummaryHandler.class,
                true);

        ListContentSummaryResult contentSummaryResult = new ListContentSummaryResult(
                listContentSummaryHandler.getFolderContentSummaries(),
                listContentSummaryHandler.getBucketName() == null ? listContentSummaryRequest.getBucketName()
                        : listContentSummaryHandler.getBucketName(),
                listContentSummaryHandler.isListingTruncated(),
                listContentSummaryHandler.getRequestPrefix() == null ? listContentSummaryRequest.getPrefix()
                        : listContentSummaryHandler.getRequestPrefix(),
                listContentSummaryHandler.getRequestMarker() == null ? listContentSummaryRequest.getMarker()
                        : listContentSummaryHandler.getRequestMarker(),
                listContentSummaryHandler.getRequestMaxKeys(),
                listContentSummaryHandler.getRequestDelimiter() == null ? listContentSummaryRequest.getDelimiter()
                        : listContentSummaryHandler.getRequestDelimiter(),
                listContentSummaryHandler.getMarkerForNextListing(),
                httpResponse.header(this.getIHeaders().bucketRegionHeader()));
        setResponseHeaders(contentSummaryResult, this.cleanResponseHeaders(httpResponse));
        setStatusCode(contentSummaryResult, httpResponse.code());
        return contentSummaryResult;
    }

    protected ListBucketsResult listAllBucketsImpl(ListBucketsRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        if (request != null && request.isQueryLocation()) {
            this.putHeader(headers, this.getIHeaders().locationHeader(), Constants.TRUE);
        }
        if (request != null && request.getBucketType() != null) {
            this.putHeader(headers, this.getIHeaders().bucketTypeHeader(), request.getBucketType().getCode());
        }
        Response httpResponse = performRestGetForListBuckets("", null, null, headers);

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListBucketsHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListBucketsHandler.class, true);

        Map<String, Object> responseHeaders = this.cleanResponseHeaders(httpResponse);

        ListBucketsResult result = new ListBucketsResult(handler.getBuckets(), handler.getOwner());
        setResponseHeaders(result, responseHeaders);
        setStatusCode(result, httpResponse.code());

        return result;
    }

    protected HeaderResponse setBucketLoggingConfigurationImpl(SetBucketLoggingRequest request)
            throws ServiceException {
        if (request.getLoggingConfiguration().isLoggingEnabled() && request.isUpdateTargetACLifRequired()
                && this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS) {
            boolean isSetLoggingGroupWrite = false;
            boolean isSetLoggingGroupReadACP = false;
            String groupIdentifier = Constants.LOG_DELIVERY_URI;

            BaseBucketRequest getBucketAclRequest = new BaseBucketRequest(
                    request.getLoggingConfiguration().getTargetBucketName());
            getBucketAclRequest.setRequesterPays(request.isRequesterPays());
            AccessControlList logBucketACL = getBucketAclImpl(getBucketAclRequest);

            for (GrantAndPermission gap : logBucketACL.getGrantAndPermissions()) {
                if (gap.getGrantee() instanceof GroupGrantee) {
                    GroupGrantee grantee = (GroupGrantee) gap.getGrantee();
                    if (groupIdentifier.equals(this.getIConvertor().transGroupGrantee(grantee.getGroupGranteeType()))) {
                        if (Permission.PERMISSION_WRITE.equals(gap.getPermission())) {
                            isSetLoggingGroupWrite = true;
                        } else if (Permission.PERMISSION_READ_ACP.equals(gap.getPermission())) {
                            isSetLoggingGroupReadACP = true;
                        }
                    }
                }
            }

            if (!isSetLoggingGroupWrite || !isSetLoggingGroupReadACP) {
                if (log.isWarnEnabled()) {
                    log.warn("Target logging bucket '" + request.getLoggingConfiguration().getTargetBucketName()
                            + "' does not have the necessary ACL settings, updating ACL now");
                }
                if (logBucketACL.getOwner() != null) {
                    logBucketACL.getOwner().setDisplayName(null);
                }
                logBucketACL.grantPermission(GroupGrantee.LOG_DELIVERY, Permission.PERMISSION_WRITE);
                logBucketACL.grantPermission(GroupGrantee.LOG_DELIVERY, Permission.PERMISSION_READ_ACP);

                SetBucketAclRequest aclReqeust = new SetBucketAclRequest(request.getBucketName(), logBucketACL);
                aclReqeust.setRequesterPays(request.isRequesterPays());
                setBucketAclImpl(aclReqeust);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Target logging bucket '" + request.getLoggingConfiguration().getTargetBucketName()
                            + "' has the necessary ACL settings");
                }
            }
        }

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LOGGING.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        transRequestPaymentHeaders(request, metadata, this.getIHeaders());

        String statusAsXml = request.getLoggingConfiguration() == null ? ""
                : this.getIConvertor().transBucketLoggingConfiguration(request.getLoggingConfiguration());

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, statusAsXml), true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse deleteBucketImpl(BaseBucketRequest request) throws ServiceException {
        Response response = performRestDelete(request.getBucketName(), null, null,
                transRequestPaymentHeaders(request, null, this.getIHeaders()));
        return this.build(response);
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
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected CopyPartResult copyPartImpl(CopyPartRequest request) throws ServiceException {

        TransResult result = this.transCopyPartRequest(request);
        Response response = this.performRestPut(request.getDestinationBucketName(), request.getDestinationObjectKey(),
                result.getHeaders(), result.getParams(), null, false);
        this.verifyResponseContentType(response);

        CopyPartResult ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
                XmlResponsesSaxParser.CopyPartResultHandler.class, true).getCopyPartResult(request.getPartNumber());
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    private String getObjectUrl(String bucketName, String objectKey) {
        boolean pathStyle = this.isPathStyle();
        boolean https = this.getHttpsOnly();
        boolean isCname = this.isCname();
        return new StringBuilder().append(https ? "https://" : "http://")
                .append(pathStyle || isCname ? "" : bucketName + ".").append(this.getEndpoint()).append(":")
                .append(https ? this.getHttpsPort() : this.getHttpPort()).append("/")
                .append(pathStyle ? bucketName + "/" : "").append(RestUtils.uriEncode(objectKey, false)).toString();
    }

    protected ThreadPoolExecutor initThreadPool(AbstractBulkRequest request) {
        int taskThreadNum = request.getTaskThreadNum();
        int workQueenLength = request.getTaskQueueNum();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(taskThreadNum, taskThreadNum, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(workQueenLength));
        executor.setRejectedExecutionHandler(new BlockRejectedExecutionHandler());
        return executor;
    }

    protected void recordBulkTaskStatus(DefaultTaskProgressStatus progressStatus,
            TaskCallback<DeleteObjectResult, String> callback, TaskProgressListener listener, int interval) {

        progressStatus.execTaskIncrement();
        if (listener != null) {
            if (progressStatus.getExecTaskNum() % interval == 0) {
                listener.progressChanged(progressStatus);
            }
            if (progressStatus.getExecTaskNum() == progressStatus.getTotalTaskNum()) {
                listener.progressChanged(progressStatus);
            }
        }
    }

    protected HeaderResponse setExtensionPolicyImpl(String bucketName, String policyDocument) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(RequestParamEnum.EXTENSION_POLICY.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_JSON);
        metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX
                : Constants.OBS_HEADER_PREFIX) + Constants.OEF_MARKER, Constants.YES);

        Response response = performRestPut(bucketName, null, metadata, requestParameters,
                this.createRequestBody(Mimetypes.MIMETYPE_JSON, policyDocument), false, true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected QueryExtensionPolicyResult queryExtensionPolicyImpl(String bucketName) throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(RequestParamEnum.EXTENSION_POLICY.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX
                : Constants.OBS_HEADER_PREFIX) + Constants.OEF_MARKER, Constants.YES);

        Response response = performRestGet(bucketName, null, requestParams, metadata, true);

        this.verifyResponseContentTypeForJson(response);

        String body;
        try {
            body = response.body().string();
        } catch (IOException e) {
            throw new ServiceException(e);
        }

        QueryExtensionPolicyResult ret = (QueryExtensionPolicyResult) JSONChange
                .jsonToObj(new QueryExtensionPolicyResult(), body);
        ret.setResponseHeaders(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse deleteExtensionPolicyImpl(String bucketName) throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(RequestParamEnum.EXTENSION_POLICY.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX
                : Constants.OBS_HEADER_PREFIX) + Constants.OEF_MARKER, Constants.YES);

        Response response = performRestDelete(bucketName, null, requestParams, metadata, true, true);
        return this.build(response);
    }

    protected CreateAsynchFetchJobsResult createFetchJobImpl(String bucketName, String policyDocument)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(RequestParamEnum.ASYNC_FETCH_JOBS.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_JSON);
        metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX
                : Constants.OBS_HEADER_PREFIX) + Constants.OEF_MARKER, Constants.YES);

        Response response = performRestPost(bucketName, null, metadata, requestParameters,
                this.createRequestBody(Mimetypes.MIMETYPE_JSON, policyDocument), false, true);

        this.verifyResponseContentTypeForJson(response);

        String body;
        try {
            body = response.body().string();
        } catch (IOException e) {
            throw new ServiceException(e);
        }

        CreateAsynchFetchJobsResult ret = (CreateAsynchFetchJobsResult) JSONChange
                .jsonToObj(new CreateAsynchFetchJobsResult(), body);
        ret.setResponseHeaders(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected QueryAsynchFetchJobsResult queryFetchJobImpl(String bucketName, String jobId) throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(RequestParamEnum.ASYNC_FETCH_JOBS.getOriginalStringCode() + "/" + jobId, "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_JSON);
        metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX
                : Constants.OBS_HEADER_PREFIX) + Constants.OEF_MARKER, Constants.YES);

        Response response = performRestGet(bucketName, null, requestParams, metadata, true);

        this.verifyResponseContentTypeForJson(response);

        String body;
        try {
            body = response.body().string();
        } catch (IOException e) {
            throw new ServiceException(e);
        }

        QueryAsynchFetchJobsResult ret = (QueryAsynchFetchJobsResult) JSONChange
                .jsonToObj(new QueryAsynchFetchJobsResult(), body);
        ret.setResponseHeaders(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());

        return ret;
    }

    protected HeaderResponse putDisPolicyImpl(String bucketName, String policyDocument) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(RequestParamEnum.DIS_POLICIES.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_JSON);
        metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX
                : Constants.OBS_HEADER_PREFIX) + Constants.OEF_MARKER, Constants.YES);

        Response response = performRestPut(bucketName, null, metadata, requestParameters,
                this.createRequestBody(Mimetypes.MIMETYPE_JSON, policyDocument), false, true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected GetDisPolicyResult getDisPolicyImpl(String bucketName) throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(RequestParamEnum.DIS_POLICIES.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX
                : Constants.OBS_HEADER_PREFIX) + Constants.OEF_MARKER, Constants.YES);

        Response response = performRestGet(bucketName, null, requestParams, metadata, true);

        this.verifyResponseContentTypeForJson(response);

        String body;
        try {
            body = response.body().string();
        } catch (IOException e) {
            throw new ServiceException(e);
        }

        DisPolicy policy = (DisPolicy) JSONChange.jsonToObj(new DisPolicy(), body);
        GetDisPolicyResult ret = new GetDisPolicyResult(policy);
        ret.setResponseHeaders(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse deleteDisPolicyImpl(String bucketName) throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(RequestParamEnum.DIS_POLICIES.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX
                : Constants.OBS_HEADER_PREFIX) + Constants.OEF_MARKER, Constants.YES);

        Response response = performRestDelete(bucketName, null, requestParams, metadata, true, true);
        return this.build(response);
    }

    protected void verifyResponseContentTypeForJson(Response response) throws ServiceException {
        if (this.obsProperties.getBoolProperty(ObsConstraint.VERIFY_RESPONSE_CONTENT_TYPE, true)) {
            String contentType = response.header(Constants.CommonHeaders.CONTENT_TYPE);
            if (null == contentType) {
                throw new ServiceException("Expected JSON document response  but received content type is null");
            } else if (-1 == contentType.indexOf(Mimetypes.MIMETYPE_JSON)) {
                throw new ServiceException(
                        "Expected JSON document response  but received content type is " + contentType);
            }
        }
    }

}

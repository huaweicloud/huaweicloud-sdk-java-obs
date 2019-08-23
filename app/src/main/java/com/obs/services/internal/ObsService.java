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

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.Constants.CommonHeaders;
import com.obs.services.internal.Constants.ObsRequestParams;
import com.obs.services.internal.handler.XmlResponsesSaxParser.*;
import com.obs.services.internal.io.HttpMethodReleaseInputStream;
import com.obs.services.internal.io.ProgressInputStream;
import com.obs.services.internal.security.BasicSecurityKey;
import com.obs.services.internal.task.BlockRejectedExecutionHandler;
import com.obs.services.internal.task.DefaultTaskProgressStatus;
import com.obs.services.internal.utils.*;
import com.obs.services.model.*;
import com.obs.services.model.RestoreObjectRequest.RestoreObjectStatus;
import com.obs.services.model.fs.*;
import com.oef.services.model.*;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.*;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ObsService extends RestStorageService {

	private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.ObsClient");
	
	protected ObsService() {

	}

	private static class TransResult {
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
	}

	protected HeaderResponse setBucketVersioningImpl(String bucketName, VersioningStatusEnum status)
			throws ServiceException {
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put(SpecialParamEnum.VERSIONING.getOriginalStringCode(), "");
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

		String xml = this.getIConvertor().transVersioningConfiguration(bucketName,
				status != null ? status.getCode() : null);

		Response response = performRestPut(bucketName, null, metadata, requestParams,
				createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
		return this.build(response);
	}

	protected RequestBody createRequestBody(String mimeType, String content) throws ServiceException {
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

	protected BucketVersioningConfiguration getBucketVersioningImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put(SpecialParamEnum.VERSIONING.getOriginalStringCode(), "");
		Response response = performRestGet(bucketName, null, requestParams, null);

		this.verifyResponseContentType(response);

		BucketVersioningConfiguration ret = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(response), BucketVersioningHandler.class, false)
				.getVersioningStatus();
		setResponseHeaders(ret, this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
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
		return new TransResult(headers, params, null);
	}

	protected ListVersionsResult listVersionsImpl(ListVersionsRequest request) throws ServiceException {

		TransResult result = this.transListVersionsRequest(request);

		Response response = performRestGet(request.getBucketName(), null, result.getParams(), null);

		this.verifyResponseContentType(response);

		ListVersionsHandler handler = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
				ListVersionsHandler.class, true);
		List<VersionOrDeleteMarker> partialItems = handler.getItems();

		ListVersionsResult listVersionsResult = new ListVersionsResult(handler.getBucketName() == null ? request.getBucketName() : handler.getBucketName(), 
				handler.getRequestPrefix() == null ? request.getPrefix() : handler.getRequestPrefix(), handler.getKeyMarker() == null ? request.getKeyMarker() : handler.getKeyMarker(), handler.getNextKeyMarker(), 
						handler.getVersionIdMarker() == null ? request.getVersionIdMarker() : handler.getVersionIdMarker(), handler.getNextVersionIdMarker(), String.valueOf(handler.getRequestMaxKeys()), handler.isListingTruncated(), 
						partialItems.toArray(new VersionOrDeleteMarker[partialItems.size()]), handler.getCommonPrefixes(), response.header(this.getIHeaders().bucketRegionHeader()), handler.getDelimiter() == null ? request.getDelimiter() : handler.getDelimiter());
		setResponseHeaders(listVersionsResult, this.cleanResponseHeaders(response));
		setStatusCode(listVersionsResult, response.code());
		return listVersionsResult;

	}

	protected BucketPolicyResponse getBucketPolicyImpl(String bucketName) throws ServiceException {
		try {
			Map<String, String> requestParameters = new HashMap<String, String>();
			requestParameters.put(SpecialParamEnum.POLICY.getOriginalStringCode(), "");

			Response response = performRestGet(bucketName, null, requestParameters, null);
			BucketPolicyResponse ret = new BucketPolicyResponse(response.body().string());
			setResponseHeaders(ret, this.cleanResponseHeaders(response));
			setStatusCode(ret, response.code());
			return ret;
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	protected BucketNotificationConfiguration getBucketNotificationConfigurationImpl(String bucketName)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.NOTIFICATION.getOriginalStringCode(), "");
		Response httpResponse = performRestGet(bucketName, null, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		BucketNotificationConfiguration result = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), BucketNotificationConfigurationHandler.class,
						false)
				.getBucketNotificationConfiguration();
		setResponseHeaders(result, this.cleanResponseHeaders(httpResponse));
		setStatusCode(result, httpResponse.code());
		return result;
	}

	protected HeaderResponse setBucketNotificationImpl(String bucketName,
			BucketNotificationConfiguration bucketNotificationConfiguration) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.NOTIFICATION.getOriginalStringCode(), "");
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
		String xml = this.getIConvertor().transBucketNotificationConfiguration(bucketNotificationConfiguration);

		Response response = performRestPut(bucketName, null, metadata, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
		
		HeaderResponse ret =  build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected HeaderResponse setBucketPolicyImpl(String bucketName, String policyDocument)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.POLICY.getOriginalStringCode(), "");

		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_TEXT_PLAIN);
		Response response = performRestPut(bucketName, null, metadata, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_TEXT_PLAIN, policyDocument), true);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected HeaderResponse deleteBucketPolicyImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.POLICY.getOriginalStringCode(), "");
		Response response = performRestDelete(bucketName, null, requestParameters);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
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
			if (Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key.toLowerCase())) {
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

		transExtensionPermissions(request, headers);

		transSseHeaders(request, headers, iheaders);

		Object contentType = objectMetadata.getContentType() == null
				? objectMetadata.getValue(CommonHeaders.CONTENT_TYPE)
				: objectMetadata.getContentType();
		if (contentType == null) {
			contentType = Mimetypes.getInstance().getMimetype(request.getObjectKey());
		}

		String _contentType = contentType.toString().trim();
		headers.put(CommonHeaders.CONTENT_TYPE, _contentType);

		Map<String, String> params = new HashMap<String, String>();
		params.put(SpecialParamEnum.UPLOADS.getOriginalStringCode(), "");

		return new TransResult(headers, params, null);
	}

	protected InitiateMultipartUploadResult initiateMultipartUploadImpl(InitiateMultipartUploadRequest request)
			throws ServiceException {

		TransResult result = this.transInitiateMultipartUploadRequest(request);

		this.prepareRESTHeaderAcl(result.getHeaders(), request.getAcl());

		Response httpResponse = performRestPost(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
				result.getParams(), null, false);

		this.verifyResponseContentType(httpResponse);

		InitiateMultipartUploadResult multipartUpload = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), InitiateMultipartUploadHandler.class, true)
				.getInitiateMultipartUploadResult();
		setResponseHeaders(multipartUpload, this.cleanResponseHeaders(httpResponse));
		setStatusCode(multipartUpload, httpResponse.code());
		return multipartUpload;
	}

	protected HeaderResponse abortMultipartUploadImpl(String uploadId, String bucketName, String objectKey)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(ObsRequestParams.UPLOAD_ID, uploadId);
		Response response = performRestDelete(bucketName, objectKey, requestParameters);
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

		Response response = performRestPost(request.getBucketName(), request.getObjectKey(), metadata,
				requestParameters, createRequestBody(Mimetypes.MIMETYPE_XML,
						this.getIConvertor().transCompleteMultipartUpload(request.getPartEtag())),
				false);

		this.verifyResponseContentType(response);

		CompleteMultipartUploadHandler handler = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(response), CompleteMultipartUploadHandler.class, true);

		String versionId = response.header(this.getIHeaders().versionIdHeader());
		
		CompleteMultipartUploadResult ret = new CompleteMultipartUploadResult(handler.getBucketName(), 
				handler.getObjectKey(), handler.getEtag(), handler.getLocation(), versionId, this.getObjectUrl(handler.getBucketName(), handler.getObjectKey()));
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

		Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		ListMultipartUploadsHandler handler = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), ListMultipartUploadsHandler.class, true);

		MultipartUploadListing listResult = new MultipartUploadListing(handler.getBucketName() == null ? request.getBucketName() : handler.getBucketName(), 
				handler.getKeyMarker() == null ? request.getKeyMarker() : handler.getKeyMarker(), handler.getUploadIdMarker() == null ? request.getUploadIdMarker() : handler.getUploadIdMarker(),
						handler.getNextKeyMarker(), handler.getNextUploadIdMarker(), handler.getPrefix() == null ? request.getPrefix() : handler.getPrefix(), handler.getMaxUploads(), handler.isTruncated(), handler.getMultipartUploadList(), 
								handler.getDelimiter() == null ? request.getDelimiter() : handler.getDelimiter(), handler.getCommonPrefixes().toArray(new String[handler.getCommonPrefixes().size()]));
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
		Response httpResponse = performRestGet(request.getBucketName(), request.getKey(), requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		ListPartsHandler handler = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
				ListPartsHandler.class, true);
		
		ListPartsResult result = new ListPartsResult(handler.getBucketName() == null ? request.getBucketName() : handler.getBucketName(), handler.getObjectKey() == null ? request.getKey() : handler.getObjectKey(), 
				handler.getUploadId() == null ? request.getUploadId() : handler.getUploadId(), handler.getInitiator(), handler.getOwner(), 
				StorageClassEnum.getValueFromCode(handler.getStorageClass()), handler.getMultiPartList(), handler.getMaxParts(), handler.isTruncated(), handler.getPartNumberMarker() == null
						? (request.getPartNumberMarker() == null ? null : request.getPartNumberMarker().toString())
								: handler.getPartNumberMarker(), handler.getNextPartNumberMarker());
		setResponseHeaders(result, this.cleanResponseHeaders(httpResponse));
		setStatusCode(result, httpResponse.code());
		return result;
	}

	protected WebsiteConfiguration getBucketWebsiteConfigurationImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.WEBSITE.getOriginalStringCode(), "");

		Response httpResponse = performRestGet(bucketName, null, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		WebsiteConfiguration ret = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), BucketWebsiteConfigurationHandler.class, false)
				.getWebsiteConfig();
		setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
		setStatusCode(ret, httpResponse.code());
		return ret;
	}

	protected HeaderResponse setBucketWebsiteConfigurationImpl(String bucketName, WebsiteConfiguration config)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.WEBSITE.getOriginalStringCode(), "");

		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

		String xml = this.getIConvertor().transWebsiteConfiguration(config);

		Response response = performRestPut(bucketName, null, metadata, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected HeaderResponse deleteBucketWebsiteConfigurationImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.WEBSITE.getOriginalStringCode(), "");
		Response response = performRestDelete(bucketName, null, requestParameters);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected LifecycleConfiguration getBucketLifecycleConfigurationImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.LIFECYCLE.getOriginalStringCode(), "");

		Response response = performRestGet(bucketName, null, requestParameters, null);

		this.verifyResponseContentType(response);

		LifecycleConfiguration ret = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(response), BucketLifecycleConfigurationHandler.class, false)
				.getLifecycleConfig();
		setResponseHeaders(ret, this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected HeaderResponse setBucketLifecycleConfigurationImpl(String bucketName, LifecycleConfiguration config)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.LIFECYCLE.getOriginalStringCode(), "");

		Map<String, String> metadata = new HashMap<String, String>();
		String xml = this.getIConvertor().transLifecycleConfiguration(config);
		metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(xml));
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

		Response response = performRestPut(bucketName, null, metadata, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
		
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected HeaderResponse deleteBucketLifecycleConfigurationImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.LIFECYCLE.getOriginalStringCode(), "");
		Response response = performRestDelete(bucketName, null, requestParameters);
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

		Response httpResponse = performRestPost(deleteObjectsRequest.getBucketName(), null, metadata, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_XML, xml), false);
		this.verifyResponseContentType(httpResponse);

		DeleteObjectsResult ret = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), DeleteObjectsHandler.class, true)
				.getMultipleDeleteResult();
		setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
		setStatusCode(ret, httpResponse.code());
		return ret;
	}

	protected boolean headBucketImpl(String bucketName) throws ServiceException {
		try {
			performRestHead(bucketName, null, null, null);
			return true;
		} catch (ServiceException e) {
			if (e.getResponseCode() == 404) {
				return false;
			}
			throw e;
		}
	}
	
	protected HeaderResponse setBucketFSStatusImpl(SetBucketFSStatusRequest request) throws ServiceException{
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.FILEINTERFACE.getOriginalStringCode(), "");
		String xml = this.getIConvertor().transBucketFileInterface(request.getStatus());
		Response response = performRestPut(request.getBucketName(), null, null, requestParameters, 
				createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}
	
	
	protected TruncateFileResult truncateFileImpl(TruncateFileRequest request) throws ServiceException{
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.TRUNCATE.getOriginalStringCode(), "");
		requestParameters.put(Constants.ObsRequestParams.LENGTH, String.valueOf(request.getNewLength()));
		
		Response response = performRestPut(request.getBucketName(), request.getObjectKey(), null, requestParameters, null, true);
		TruncateFileResult result = new TruncateFileResult();
		setResponseHeaders(result, this.cleanResponseHeaders(response));
		setStatusCode(result, response.code());
		return result;
	}
	
	
	protected RenameResult renameObjectImpl(RenameRequest request) throws ServiceException{
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.RENAME.getOriginalStringCode(), "");
		requestParameters.put(Constants.ObsRequestParams.NAME, request.getNewObjectKey());
		
		Response response = performRestPost(request.getBucketName(), request.getObjectKey(), null, requestParameters, null, true);
		RenameResult result = new RenameResult();
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
			Response response = performRestHead(bucketMetadataInfoRequest.getBucketName(), null, null, headers);
			output = this.getOptionInfoResult(response);
			response.close();
		}

		return output;
	}

	protected RestoreObjectStatus restoreObjectImpl(RestoreObjectRequest restoreObjectRequest) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.RESTORE.getOriginalStringCode(), "");
		if (restoreObjectRequest.getVersionId() != null) {
			requestParameters.put(ObsRequestParams.VERSION_ID, restoreObjectRequest.getVersionId());
		}
		Map<String, String> metadata = new HashMap<String, String>();
		String requestXmlElement = this.getIConvertor().transRestoreObjectRequest(restoreObjectRequest);
		metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
		Response response = this.performRestPost(restoreObjectRequest.getBucketName(),
				restoreObjectRequest.getObjectKey(), metadata, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
		RestoreObjectStatus ret = RestoreObjectStatus.valueOf(response.code());
		setResponseHeaders(ret, this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}
	
	protected RestoreObjectResult restoreObjectV2Impl(RestoreObjectRequest restoreObjectRequest) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.RESTORE.getOriginalStringCode(), "");
        if (restoreObjectRequest.getVersionId() != null) {
            requestParameters.put(ObsRequestParams.VERSION_ID, restoreObjectRequest.getVersionId());
        }
        Map<String, String> metadata = new HashMap<String, String>();
        String requestXmlElement = this.getIConvertor().transRestoreObjectRequest(restoreObjectRequest);
        metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        Response response = this.performRestPost(restoreObjectRequest.getBucketName(),
                restoreObjectRequest.getObjectKey(), metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
        RestoreObjectResult ret = new RestoreObjectResult(restoreObjectRequest.getBucketName(), 
                restoreObjectRequest.getObjectKey(), restoreObjectRequest.getVersionId());
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

	protected BucketTagInfo getBucketTaggingImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.TAGGING.getOriginalStringCode(), "");
		Response httpResponse = this.performRestGet(bucketName, null, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		BucketTagInfo result = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), BucketTagInfoHandler.class, false)
				.getBucketTagInfo();
		setResponseHeaders(result, this.cleanResponseHeaders(httpResponse));
		setStatusCode(result, httpResponse.code());
		return result;
	}

	protected HeaderResponse setBucketTaggingImpl(String bucketName, BucketTagInfo bucketTagInfo)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.TAGGING.getOriginalStringCode(), "");
		Map<String, String> headers = new HashMap<String, String>();

		String requestXmlElement = this.getIConvertor().transBucketTagInfo(bucketTagInfo);

		headers.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
		headers.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

		Response response = this.performRestPut(bucketName, null, headers, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected HeaderResponse deleteBucketTaggingImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.TAGGING.getOriginalStringCode(), "");
		Response response = performRestDelete(bucketName, null, requestParameters);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}
	
	protected BucketEncryption getBucketEncryptionImpl(String bucketName) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ENCRYPTION.getOriginalStringCode(), "");
        
        Response httpResponse = performRestGet(bucketName, null, requestParameters, null);
        
        this.verifyResponseContentType(httpResponse);
        
        BucketEncryption ret = getXmlResponseSaxParser()
                .parse(new HttpMethodReleaseInputStream(httpResponse), BucketEncryptionHandler.class, false).getEncryption();
        
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }

	protected HeaderResponse setBucketEncryptionImpl(String bucketName, BucketEncryption encryption) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ENCRYPTION.getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        
        String encryptAsXml = encryption == null ? "" : this.getIConvertor().transBucketEcryption(encryption);
        metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(encryptAsXml.length()));
        Response response = performRestPut(bucketName, null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, encryptAsXml), true);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }
	
	protected HeaderResponse deleteBucketEncryptionImpl(String bucketName) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ENCRYPTION.getOriginalStringCode(), "");
        Response response = performRestDelete(bucketName, null, requestParameters);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

	protected ReplicationConfiguration getBucketReplicationConfigurationImpl(String bucketName)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.REPLICATION.getOriginalStringCode(), "");
		Response httpResponse = this.performRestGet(bucketName, null, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		ReplicationConfiguration result = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), BucketReplicationConfigurationHandler.class,
						false)
				.getReplicationConfiguration();
		setResponseHeaders(result, this.cleanResponseHeaders(httpResponse));
		setStatusCode(result, httpResponse.code());
		return result;
	}

	protected HeaderResponse setBucketReplicationConfigurationImpl(String bucketName,
			ReplicationConfiguration replicationConfiguration) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.REPLICATION.getOriginalStringCode(), "");
		Map<String, String> headers = new HashMap<String, String>();

		String requestXmlElement = this.getIConvertor().transReplicationConfiguration(replicationConfiguration);

		headers.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
		headers.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

		Response response = this.performRestPut(bucketName, null, headers, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected HeaderResponse deleteBucketReplicationConfigurationImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.REPLICATION.getOriginalStringCode(), "");
		Response response = performRestDelete(bucketName, null, requestParameters);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}
	
	protected TemporarySignatureResponse _createTemporarySignature(AbstractTemporarySignatureRequest request) throws Exception {
	    String requestMethod = request.getMethod() != null ? request.getMethod().getOperationType() : "GET";
	    
		Map<String, Object> queryParams = new TreeMap<String, Object>();
		queryParams.putAll(request.getQueryParams());
		BasicSecurityKey securityKey =  this.getProviderCredentials().getSecurityKey();
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
        
        if(this.isCname()) {
            hostname = endpoint;
            uriPath = objectKeyPath;
            virtualBucketPath = endpoint + "/";
        }
		
        uriPath += "?";
		if (request.getSpecialParam() != null) {
			if(request.getSpecialParam() == SpecialParamEnum.STORAGECLASS || request.getSpecialParam() == SpecialParamEnum.STORAGEPOLICY) {
				request.setSpecialParam(this.getSpecialParamForStorageClass());
			}
			uriPath +=  request.getSpecialParam().getOriginalStringCode() + "&";
		}
	 	
		String accessKeyIdPrefix = this.getProviderCredentials().getAuthType() == AuthTypeEnum.OBS ? "AccessKeyId=" : "AWSAccessKeyId=";
		uriPath += accessKeyIdPrefix + accessKey;
		
		String expiresOrPolicy = "";
		String uriExpiresOrPolicy = "";
		if (request instanceof TemporarySignatureRequest) {
            TemporarySignatureRequest tempRequest = (TemporarySignatureRequest)request;
            long secondsSinceEpoch = tempRequest.getExpires() <= 0 ? ObsConstraint.DEFAULT_EXPIRE_SECONEDS : tempRequest.getExpires();
            secondsSinceEpoch += System.currentTimeMillis() / 1000;
            expiresOrPolicy = String.valueOf(secondsSinceEpoch);
            uriExpiresOrPolicy = "&Expires=" + expiresOrPolicy;
        } else if(request instanceof PolicyTempSignatureRequest) {
            PolicyTempSignatureRequest policyRequest = (PolicyTempSignatureRequest)request;
            String policy = policyRequest.generatePolicy();
            expiresOrPolicy = ServiceUtils.toBase64(policy.getBytes(Constants.DEFAULT_ENCODING));
            uriExpiresOrPolicy = "&Policy=" +  expiresOrPolicy;
        }
		uriPath += uriExpiresOrPolicy;
		
		for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                String key = RestUtils.uriEncode(entry.getKey(), false);
                uriPath += "&";
                uriPath += key;
                uriPath += "=";
                String value = RestUtils.uriEncode(entry.getValue().toString(), false);
                uriPath += value;
            }
        }
		
		Map<String, String> headers = new HashMap<String, String>();
        headers.putAll(request.getHeaders());
        headers.put(CommonHeaders.HOST, hostname +  ":" + (this.getHttpsOnly() ? this.getHttpsPort() : this.getHttpPort()));
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
		if (log.isDebugEnabled()) {
			log.debug("CanonicalString is :" + canonicalString);
		}
		
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
	
	protected PostSignatureResponse _createPostSignature(PostSignatureRequest request, boolean isV4) throws Exception {
		BasicSecurityKey securityKey =  this.getProviderCredentials().getSecurityKey();
		String accessKey = securityKey.getAccessKey();
		String secretKey = securityKey.getSecretKey();
		String securityToken = securityKey.getSecurityToken();
		Date requestDate = request.getRequestDate() != null ? request.getRequestDate() : new Date();
		SimpleDateFormat expirationDateFormat = ServiceUtils.getExpirationDateFormat();
		Date expiryDate = request.getExpiryDate() == null
				? new Date(requestDate.getTime() + (request.getExpires() <=0 ? ObsConstraint.DEFAULT_EXPIRE_SECONEDS : request.getExpires()) * 1000)
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
			if(isV4) {
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
							&& !key.startsWith(this.getRestHeaderPrefix()) && !key.startsWith(Constants.OBS_HEADER_PREFIX) && !key.equals("acl")
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
		
		if(isV4) {
			String signature = V4Authentication.caculateSignature(policy, shortDate, secretKey);
			return new V4PostSignatureResponse(policy, originPolicy.toString(),
					Constants.V4_ALGORITHM, credential, longDate, signature, expiration);
		}else {
			String signature = AbstractAuthentication.caculateSignature(policy, secretKey);
			return new PostSignatureResponse(policy, originPolicy.toString(), signature, expiration, accessKey);
		}
		
	}

	protected TemporarySignatureResponse createV4TemporarySignature(TemporarySignatureRequest request) throws Exception {
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
		
		
		if(this.isCname()) {
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
		if((this.getHttpsOnly() && this.getHttpsPort() == 443) || (!this.getHttpsOnly() && this.getHttpPort() == 80)) {
			headers.put(CommonHeaders.HOST, endpoint);
		}else {
			headers.put(CommonHeaders.HOST, endpoint +  ":" + (this.getHttpsOnly() ? this.getHttpsPort() : this.getHttpPort()));
		}

		BasicSecurityKey securityKey =  this.getProviderCredentials().getSecurityKey();
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
		queryParams.put(Constants.V2_HEADER_PREFIX_CAMEL + "Expires", request.getExpires() <= 0 ? ObsConstraint.DEFAULT_EXPIRE_SECONEDS : request.getExpires());
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
			if(request.getSpecialParam() == SpecialParamEnum.STORAGECLASS || request.getSpecialParam() == SpecialParamEnum.STORAGEPOLICY) {
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

	protected String getCredential(String shortDate, String accessKey) {
		return new StringBuilder(accessKey).append("/").append(shortDate).append("/")
				.append(ObsConstraint.DEFAULT_BUCKET_LOCATION_VALUE).append("/").append(Constants.SERVICE).append("/")
				.append(Constants.REQUEST_TAG).toString();
	}

	GetBucketFSStatusResult getOptionInfoResult(Response response) {

		Headers headers = response.headers();

		Map<String, List<String>> map = headers.toMultimap();
		String maxAge = headers.get(Constants.CommonHeaders.ACCESS_CONTROL_MAX_AGE);
		
		IHeaders iheaders = this.getIHeaders();
		FSStatusEnum status = FSStatusEnum.getValueFromCode(headers.get(iheaders.fsFileInterfaceHeader()));
		
		GetBucketFSStatusResult output = new GetBucketFSStatusResult(headers.get(Constants.CommonHeaders.ACCESS_CONTROL_ALLOW_ORIGIN), map.get(Constants.CommonHeaders.ACCESS_CONTROL_ALLOW_HEADERS), maxAge == null ? 0 :Integer.parseInt(maxAge) , map.get(Constants.CommonHeaders.ACCESS_CONTROL_ALLOW_METHODS), map.get(Constants.CommonHeaders.ACCESS_CONTROL_EXPOSE_HEADERS), 
				StorageClassEnum.getValueFromCode(headers.get(iheaders.defaultStorageClassHeader())), headers.get(iheaders.bucketRegionHeader()), headers.get(iheaders.serverVersionHeader()), status, 
				AvailableZoneEnum.getValueFromCode(headers.get(iheaders.azRedundancyHeader())), headers.get(iheaders.epidHeader()));
		setResponseHeaders(output, this.cleanResponseHeaders(response));
		setStatusCode(output, response.code());
		return output;
	}

	protected BucketMetadataInfoResult optionsImpl(String bucketName, String ObjectName, OptionsInfoRequest option)
			throws ServiceException {
		Map<String, String> metadata = new IdentityHashMap<String, String>();

		if (ServiceUtils.isValid(option.getOrigin())) {
			metadata.put(CommonHeaders.ORIGIN, option.getOrigin().trim());
		}

		for (int i = 0; option.getRequestMethod() != null && i < option.getRequestMethod().size(); i++) {
			metadata.put(new String(CommonHeaders.ACCESS_CONTROL_REQUEST_METHOD), option.getRequestMethod().get(i));
		}
		for (int i = 0; option.getRequestHeaders() != null && i < option.getRequestHeaders().size(); i++) {
			metadata.put(new String(CommonHeaders.ACCESS_CONTROL_REQUEST_HEADERS), option.getRequestHeaders().get(i));
		}

		Response rsult = performRestOptions(bucketName, ObjectName, metadata, null, true);
		return getOptionInfoResult(rsult);

	}

	protected HeaderResponse deleteBucketCorsImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.CORS.getOriginalStringCode(), "");

		Response response = performRestDelete(bucketName, null, requestParameters);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected BucketCors getBucketCorsImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.CORS.getOriginalStringCode(), "");
		Response httpResponse = performRestGet(bucketName, null, requestParameters, null);
		this.verifyResponseContentType(httpResponse);
		BucketCors ret = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), BucketCorsHandler.class, false)
				.getConfiguration();
		setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
		setStatusCode(ret, httpResponse.code());
		return ret;

	}

	protected HeaderResponse setBucketCorsImpl(String bucketName, BucketCors bucketCors) throws ServiceException {
		String corsXML = bucketCors == null ? "" : this.getIConvertor().transBucketCors(bucketCors);

		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.CORS.getOriginalStringCode(), "");

		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
		metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(corsXML));

		metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(corsXML.length()));
		Response response = performRestPut(bucketName, null, metadata, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_XML, corsXML), true);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected HeaderResponse setBucketQuotaImpl(String bucketName, BucketQuota quota) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.QUOTA.getOriginalStringCode(), "");
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

		String quotaAsXml = quota == null ? "" : this.getIConvertor().transBucketQuota(quota);
		metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(quotaAsXml.length()));
		Response response = performRestPut(bucketName, null, metadata, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_XML, quotaAsXml), true);
		HeaderResponse ret =  build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected HeaderResponse setBucketStorageImpl(String bucketName,
			BucketStoragePolicyConfiguration storagePolicy) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(this.getSpecialParamForStorageClass().getOriginalStringCode(), "");
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
		String xml = storagePolicy == null ? "" : this.getIConvertor().transStoragePolicy(storagePolicy);
		metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(xml.length()));
		Response response = performRestPut(bucketName, null, metadata, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	void putAclImpl(String bucketName, String objectKey, AccessControlList acl, String versionId)
			throws ServiceException {
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
			performRestPut(bucketName, objectKey, metadata, requestParameters,
					createRequestBody(Mimetypes.MIMETYPE_XML, aclAsXml), true);
		}
	}

	protected HeaderResponse setObjectAclImpl(String bucketName, String objectKey, String cannedACL,
			AccessControlList acl, String versionId) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");
		if (versionId != null) {
			requestParameters.put(ObsRequestParams.VERSION_ID, versionId);
		}
		RequestBody entity = null;
		if (ServiceUtils.isValid(cannedACL)) {
			acl = this.getIConvertor().transCannedAcl(cannedACL.trim());
		}
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
		boolean isExtraAclPutRequired = !prepareRESTHeaderAclObject(metadata, acl);
		if (isExtraAclPutRequired) {
			String aclAsXml = acl == null ? "" : this.getIConvertor().transAccessControlList(acl, false);
			metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(aclAsXml.length()));
			entity = createRequestBody(Mimetypes.MIMETYPE_XML, aclAsXml);
		}
		Response response = performRestPut(bucketName, objectKey, metadata, requestParameters, entity, true);
		HeaderResponse ret =  build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected HeaderResponse setBucketAclImpl(String bucketName, String cannedACL, AccessControlList acl)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");
		RequestBody entity = null;
		if (ServiceUtils.isValid(cannedACL)) {
			acl = this.getIConvertor().transCannedAcl(cannedACL.trim());
		}
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
		boolean isExtraAclPutRequired = !prepareRESTHeaderAcl(metadata, acl);
		if (isExtraAclPutRequired) {
			String aclAsXml = acl == null ? "" : this.getIConvertor().transAccessControlList(acl, true);
			metadata.put(CommonHeaders.CONTENT_LENGTH, String.valueOf(aclAsXml.length()));
			entity = createRequestBody(Mimetypes.MIMETYPE_XML, aclAsXml);
		}
		Response response = performRestPut(bucketName, null, metadata, requestParameters, entity, true);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	AbstractAuthentication getAuthentication() {
		return Constants.AUTHTICATION_MAP.get(this.getProviderCredentials().getAuthType());
	}

	SpecialParamEnum getSpecialParamForStorageClass() {
		return this.getProviderCredentials().getAuthType() == AuthTypeEnum.OBS ? SpecialParamEnum.STORAGECLASS
				: SpecialParamEnum.STORAGEPOLICY;
	}

	void putHeader(Map<String, String> headers, String key, String value) {
		if (ServiceUtils.isValid(key)) {
			headers.put(key, value);
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
		
		if(request.getAvailableZone() != null) {
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
			result.body = requestEntity;
		}
		return result;
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

	protected ObsBucket createBucketImpl(CreateBucketRequest request) throws ServiceException {
		TransResult result = this.transCreateBucketRequest(request);
		String bucketName = request.getBucketName();
		AccessControlList acl = request.getAcl();

		boolean isExtraAclPutRequired = !prepareRESTHeaderAcl(result.getHeaders(), acl);

		Response response = performRestPut(bucketName, null, result.getHeaders(), null, result.body, true);

		if (isExtraAclPutRequired && acl != null) {
			if (log.isDebugEnabled()) {
				log.debug("Creating bucket with a non-canned ACL using REST, so an extra ACL Put is required");
			}
			try {
				putAclImpl(bucketName, null, acl, null);
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
				? this.prepareRESTHeaderAclForOBSObject(metadata, acl)
				: this.prepareRESTHeaderAclForV2(metadata, acl);
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
				? this.prepareRESTHeaderAclForOBS(metadata, acl)
				: this.prepareRESTHeaderAclForV2(metadata, acl);
	}

	protected BucketLocationResponse getBucketLocationImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.LOCATION.getOriginalStringCode(), "");

		Response httpResponse = performRestGet(bucketName, null, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		BucketLocationResponse ret = new BucketLocationResponse(getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), BucketLocationHandler.class, false)
				.getLocation());
		setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
		setStatusCode(ret, httpResponse.code());
		return ret;
	}

	protected BucketLoggingConfiguration getBucketLoggingConfigurationImpl(String bucketName) throws ServiceException {

		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.LOGGING.getOriginalStringCode(), "");

		Response httpResponse = performRestGet(bucketName, null, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		BucketLoggingConfiguration ret = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), BucketLoggingHandler.class, false)
				.getBucketLoggingStatus();

		setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
		setStatusCode(ret, httpResponse.code());
		return ret;
	}

	HeaderResponse setBucketLoggingConfigurationImpl(String bucketName, BucketLoggingConfiguration status)
			throws ServiceException {

		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.LOGGING.getOriginalStringCode(), "");

		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

		String statusAsXml = status == null ? "" : this.getIConvertor().transBucketLoggingConfiguration(status);

		Response response = performRestPut(bucketName, null, metadata, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_XML, statusAsXml), true);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
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
			if (Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key.toLowerCase())) {
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

		transExtensionPermissions(request, headers);

		transSseHeaders(request, headers, iheaders);

		Object contentType = objectMetadata.getContentType() == null
				? objectMetadata.getValue(CommonHeaders.CONTENT_TYPE)
				: objectMetadata.getContentType();
		if (contentType == null) {
			contentType = Mimetypes.getInstance().getMimetype(request.getObjectKey());
		}
		Object contentLength = objectMetadata.getContentLength();

		if (contentLength == null) {
			contentLength = objectMetadata.getValue(CommonHeaders.CONTENT_LENGTH);
		}

		long _contentLength = contentLength == null ? -1l : Long.parseLong(contentLength.toString());

		InputStream input = null;
		if (request.getFile() != null) {
			
			if (Mimetypes.MIMETYPE_OCTET_STREAM.equals(contentType)) {
				contentType = Mimetypes.getInstance().getMimetype(request.getFile());
			}

			try {
				input = new FileInputStream(request.getFile());
			} catch (FileNotFoundException e) {
				 throw new IllegalArgumentException("File doesnot exist");
			}
			
			long fileSize = request.getFile().length();
			if(request.getOffset() > 0 && request.getOffset() < fileSize) {
				_contentLength = (_contentLength > 0 && _contentLength <= fileSize - request.getOffset()) ?  _contentLength : fileSize - request.getOffset();
				try {
					input.skip(request.getOffset());
				} catch (IOException e) {
					ServiceUtils.closeStream(input);
					throw new ServiceException(e);
				}
			}else if(_contentLength < 0 || _contentLength > fileSize) {
				_contentLength = fileSize;
			}
			
			if(request.getProgressListener() != null) {
				ProgressManager progressManager = new SimpleProgressManager(_contentLength, 0, request.getProgressListener(), 
						request.getProgressInterval() > 0 ? request.getProgressInterval() : ObsConstraint.DEFAULT_PROGRESS_INTERVAL);
				input = new ProgressInputStream(input, progressManager);
			}
			
		} else {
			input = request.getInput();
			if(input != null && request.getProgressListener() != null) {
				ProgressManager progressManager = new SimpleProgressManager(_contentLength, 0, request.getProgressListener(), 
						request.getProgressInterval() > 0 ? request.getProgressInterval() : ObsConstraint.DEFAULT_PROGRESS_INTERVAL);
				input = new ProgressInputStream(input, progressManager);
			}
		}

		String _contentType = contentType.toString().trim();
		headers.put(CommonHeaders.CONTENT_TYPE, _contentType);
		
		if(_contentLength > -1) {
			this.putHeader(headers, CommonHeaders.CONTENT_LENGTH, String.valueOf(_contentLength));
		}

		RequestBody body = input == null ? null
				: new RepeatableRequestEntity(input, _contentType, _contentLength, this.obsProperties);

		return new TransResult(headers, body);
	}
	
	TransResult transWriteFileRequest(WriteFileRequest request) throws ServiceException {
		TransResult result = this.transPutObjectRequest(request);
		if(request.getPosition() > 0) {
			Map<String, String> params = new HashMap<String, String>();
			params.put(SpecialParamEnum.MODIFY.getOriginalStringCode(), "");
			params.put(ObsRequestParams.POSITION, String.valueOf(request.getPosition()));
			result.params = params;
		}
		return result;
	}
	
	TransResult transAppendObjectRequest(AppendObjectRequest request) throws ServiceException {
		TransResult result = this.transPutObjectRequest(request);
		Map<String, String> params = new HashMap<String, String>();
		params.put(SpecialParamEnum.APPEND.getOriginalStringCode(), "");
		params.put(ObsRequestParams.POSITION, String.valueOf(request.getPosition()));
		result.params = params;
		return result;
	}

	void transSseKmsHeaders(SseKmsHeader kmsHeader, Map<String, String> headers, IHeaders iheaders) {
		if (kmsHeader == null) {
			return;
		}
		
		String sseKmsEncryption = this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? "aws:" + kmsHeader.getSSEAlgorithm().getCode() : kmsHeader.getSSEAlgorithm().getCode();
		putHeader(headers, iheaders.sseKmsHeader(), ServiceUtils.toValid(sseKmsEncryption));
		if (ServiceUtils.isValid(kmsHeader.getKmsKeyId())) {
			putHeader(headers, iheaders.sseKmsKeyHeader(), kmsHeader.getKmsKeyId());
		}
		
		if(ServiceUtils.isValid(kmsHeader.getProjectId())) {
			putHeader(headers, iheaders.sseKmsProjectIdHeader(), kmsHeader.getProjectId());
		}
	}

	void transSseCHeaders(SseCHeader cHeader, Map<String, String> headers, IHeaders iheaders) throws ServiceException {
		if (cHeader == null) {
			return;
		}
		
		String sseCAlgorithm = cHeader.getSSEAlgorithm().getCode();
		
		putHeader(headers, iheaders.sseCHeader(), ServiceUtils.toValid(sseCAlgorithm));
		if(cHeader.getSseCKeyBase64() != null) {
			try {
				putHeader(headers, iheaders.sseCKeyHeader(), cHeader.getSseCKeyBase64());
				putHeader(headers, iheaders.sseCKeyMd5Header(), ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(ServiceUtils.fromBase64(cHeader.getSseCKeyBase64()))));
			} catch (IOException e) {
				throw new IllegalStateException("fail to read sseCkey", e);
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException("fail to read sseCkey", e);
			}	
		}else if (null != cHeader.getSseCKey()) {
			try {
				byte[] data = cHeader.getSseCKey();
				putHeader(headers, iheaders.sseCKeyHeader(), ServiceUtils.toBase64(data));
				putHeader(headers, iheaders.sseCKeyMd5Header(), ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(data)));
			} catch (IOException e) {
				throw new IllegalStateException("fail to read sseCkey", e);
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException("fail to read sseCkey", e);
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

	protected AppendObjectResult appendObjectImpl(AppendObjectRequest request) throws ServiceException {
		TransResult result = null;
		Response response;
		boolean isExtraAclPutRequired;
		AccessControlList acl = request.getAcl();
		try {
			result = this.transAppendObjectRequest(request);

			isExtraAclPutRequired = !prepareRESTHeaderAcl(result.getHeaders(), acl);

			response = performRestPost(request.getBucketName(), request.getObjectKey(), result.getHeaders(), result.getParams(),
					result.body, true);
		} finally {
			if (result != null && result.body != null && request.isAutoClose()) {
				RepeatableRequestEntity entity = (RepeatableRequestEntity) result.body;
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
				putAclImpl(request.getBucketName(), request.getObjectKey(), acl, null);
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

			response = performRestPut(request.getBucketName(), request.getObjectKey(), result.getHeaders(), result.getParams(),
					result.body, true);
		} finally {
			if (result != null && result.body != null && request.isAutoClose()) {
				if(result.body instanceof Closeable) {
					ServiceUtils.closeStream((Closeable)result.body);
				}
			}
		}

		ObsFSFile ret = new ObsFSFile(request.getBucketName(), request.getObjectKey(), 
				response.header(CommonHeaders.ETAG), response.header(this.getIHeaders().versionIdHeader()), 
				StorageClassEnum.getValueFromCode(response.header(this.getIHeaders().storageClassHeader())), this.getObjectUrl(request.getBucketName(), request.getObjectKey()));
		Map<String, Object> map = this.cleanResponseHeaders(response);
		setResponseHeaders(ret, map);
		setStatusCode(ret, response.code());
		if (isExtraAclPutRequired && acl != null) {
			try {
				putAclImpl(request.getBucketName(), request.getObjectKey(), acl, null);
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
					result.body, true);
		} finally {
			if (result != null && result.body != null && request.isAutoClose()) {
				if(result.body instanceof Closeable) {
					ServiceUtils.closeStream((Closeable)result.body);
				}
			}
		}

		ObsFSFile ret = new ObsFSFile(request.getBucketName(), request.getObjectKey(), 
				response.header(CommonHeaders.ETAG), response.header(this.getIHeaders().versionIdHeader()), 
				StorageClassEnum.getValueFromCode(response.header(this.getIHeaders().storageClassHeader())), this.getObjectUrl(request.getBucketName(), request.getObjectKey()));
		Map<String, Object> map = this.cleanResponseHeaders(response);
		setResponseHeaders(ret, map);
		setStatusCode(ret, response.code());
		if (isExtraAclPutRequired && acl != null) {
			try {
				putAclImpl(request.getBucketName(), request.getObjectKey(), acl, null);
			} catch (Exception e) {
				if (log.isWarnEnabled()) {
					log.warn("Try to set object acl error", e);
				}
			}
		}
		return ret;
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

		this.transExtensionPermissions(request, headers);
		this.transSseHeaders(request, headers, iheaders);

		transSseCSourceHeaders(request.getSseCHeaderSource(), headers, iheaders);

		transConditionCopyHeaders(request, headers, iheaders);

		String sourceKey = RestUtils
				.encodeUrlString(request.getSourceBucketName()) + "/" + RestUtils
				.encodeUrlString(request.getSourceObjectKey());
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
			if(sseCHeader.getSseCKeyBase64() != null) {
				try {
					putHeader(headers, iheaders.copySourceSseCKeyHeader(), sseCHeader.getSseCKeyBase64());
					putHeader(headers, iheaders.copySourceSseCKeyMd5Header(), ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(ServiceUtils.fromBase64(sseCHeader.getSseCKeyBase64()))));
				} catch (IOException e) {
					throw new IllegalStateException("fail to read sseCkey", e);
				} catch (NoSuchAlgorithmException e) {
					throw new IllegalStateException("fail to read sseCkey", e);
				}
			}else if (null != sseCHeader.getSseCKey()) {
				try {
					byte[] data = sseCHeader.getSseCKey();
					putHeader(headers, iheaders.copySourceSseCKeyHeader(), ServiceUtils.toBase64(data));
					putHeader(headers, iheaders.copySourceSseCKeyMd5Header(), ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(data)));
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

	protected CopyObjectResult copyObjectImpl(CopyObjectRequest request) throws ServiceException {

		TransResult result = this.transCopyObjectRequest(request);

		AccessControlList acl = request.getAcl();
		boolean isExtraAclPutRequired = !prepareRESTHeaderAcl(result.getHeaders(), acl);

		Response response = performRestPut(request.getDestinationBucketName(), request.getDestinationObjectKey(),
				result.getHeaders(), null, null, false);

		this.verifyResponseContentType(response);

		CopyObjectResultHandler handler = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
				CopyObjectResultHandler.class, false);
		CopyObjectResult copyRet = new CopyObjectResult(handler.getETag(), handler.getLastModified(), response.header(this.getIHeaders().versionIdHeader()), response.header(this.getIHeaders().copySourceVersionIdHeader()), StorageClassEnum.getValueFromCode(response.header(this.getIHeaders().storageClassHeader())));
		Map<String, Object> map = this.cleanResponseHeaders(response);
		setResponseHeaders(copyRet, map);
		setStatusCode(copyRet, response.code());
		if (isExtraAclPutRequired && acl != null) {
			if (log.isDebugEnabled()) {
				log.debug("Creating object with a non-canned ACL using REST, so an extra ACL Put is required");
			}
			try {
				putAclImpl(request.getDestinationBucketName(), request.getDestinationObjectKey(), acl, null);
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
		Map<String, String> params = new HashMap<String, String>();
		if (request.getVersionId() != null) {
			params.put(ObsRequestParams.VERSION_ID, request.getVersionId());
		}
		boolean doesObjectExist = false;
		try{
			Response response = performRestHead(request.getBucketName(), request.getObjectKey(), params, headers);
			if(200 == response.code()) {
				doesObjectExist = true;
			}
		}catch (ServiceException ex){
			if(404 == ex.getResponseCode()){
				doesObjectExist = false;
			}else {
				throw ex;
			}
		}
		return  doesObjectExist;
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
			headers.put(key, entry.getValue() == null ? "" : entry.getValue().toString());
		}

		if (request.getObjectStorageClass() != null) {
			putHeader(headers, iheaders.storageClassHeader(),
					iconvertor.transStorageClass(request.getObjectStorageClass()));
		}

		if (request.getWebSiteRedirectLocation() != null) {
			putHeader(headers, iheaders.websiteRedirectLocationHeader(), request.getWebSiteRedirectLocation());
		}
		
		if(request.getContentDisposition() != null) {
			putHeader(headers, Constants.CommonHeaders.CONTENT_DISPOSITION, request.getContentDisposition());
		}
		
		if(request.getContentEncoding() != null) {
			putHeader(headers, Constants.CommonHeaders.CONTENT_ENCODING, request.getContentEncoding());
		}
		
		if(request.getContentLanguage() != null) {
			putHeader(headers, Constants.CommonHeaders.CONTENT_LANGUAGE, request.getContentLanguage());
		}
		
		if(request.getContentType() != null) {
			putHeader(headers, Constants.CommonHeaders.CONTENT_TYPE, request.getContentType());
		}
		
		if(request.getCacheControl() != null) {
			putHeader(headers, Constants.CommonHeaders.CACHE_CONTROL, request.getCacheControl());
		}
		
		if(request.getExpires() != null) {
			putHeader(headers, Constants.CommonHeaders.EXPIRES, request.getExpires());
		}
		
		putHeader(headers, iheaders.metadataDirectiveHeader(), request.isRemoveUnset() ? Constants.DERECTIVE_REPLACE  : Constants.DERECTIVE_REPLACE_NEW);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(SpecialParamEnum.METADATA.getOriginalStringCode(), "");
		if (request.getVersionId() != null) {
			params.put(ObsRequestParams.VERSION_ID, request.getVersionId());
		}

		return new TransResult(headers, params, null);
	}
	
	protected ObjectMetadata setObjectMetadataImpl(SetObjectMetadataRequest request) {
		TransResult result = this.transSetObjectMetadataRequest(request);
		Response response = performRestPut(request.getBucketName(), request.getObjectKey(), result.headers, result.params,
				result.body, true);
		return this.getObsFSAttributeFromResponse(response);
	}
	

	TransResult transGetObjectRequest(GetObjectRequest request) throws ServiceException {
		Map<String, String> headers = new HashMap<String, String>();
		this.transSseCHeaders(request.getSseCHeader(), headers, this.getIHeaders());
		this.transConditionGetObjectHeaders(request, headers);

		if (request.getRangeStart() != null) {
		    String rangeEnd = request.getRangeEnd() != null ? String.valueOf(request.getRangeEnd()) : "";
            String range = String.format("bytes=%s-%s", request.getRangeStart(), rangeEnd);
            headers.put(CommonHeaders.RANGE, range);
        }

		Map<String, String> params = new HashMap<String, String>();
		this.transGetObjectParams(request, params);

		return new TransResult(headers, params, null);
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
		if(request.getCacheOption() != null) {
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
		Date _lastModified = null;
		String lastModified = response.header(CommonHeaders.LAST_MODIFIED);
		if (lastModified != null) {
			try {
				_lastModified = ServiceUtils.parseRfc822Date(lastModified);
			} catch (ParseException e) {
				if (log.isWarnEnabled()) {
					log.warn("Response last-modified is not well-format", e);
				}
			}
		}
		ObsFSAttribute objMetadata = new ObsFSAttribute();
		objMetadata.setLastModified(_lastModified);
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
		if((fsMode=response.header(this.getIHeaders().fsModeHeader())) != null) {
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
		if(objMetadata.getNextPosition() == -1L) {
			objMetadata.setNextPosition(Long.parseLong(response.header(Constants.CommonHeaders.CONTENT_LENGTH, "-1")));
		}

		objMetadata.getMetadata().putAll(this.cleanResponseHeaders(response));
		setStatusCode(objMetadata, response.code());
		return objMetadata;
	}
	
	protected Object getObjectImpl(boolean headOnly, String bucketName, String objectKey, Map<String, String> headers,
			Map<String, String> params, ProgressListener progressListener, long progressInterval) throws ServiceException {
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
		InputStream input = response.body().byteStream();
		if(progressListener != null) {
			ProgressManager progressManager = new SimpleProgressManager(objMetadata.getContentLength(), 0, 
					progressListener, progressInterval > 0 ? progressInterval : ObsConstraint.DEFAULT_PROGRESS_INTERVAL);
			input = new ProgressInputStream(input, progressManager);
		}
		
		int readBufferSize = obsProperties.getIntProperty(ObsConstraint.READ_BUFFER_SIZE,
				ObsConstraint.DEFAULT_READ_BUFFER_STREAM);
		if(readBufferSize > 0) {
			input = new BufferedInputStream(input, readBufferSize);
		}
		
		obsObject.setObjectContent(input);
		return obsObject;
	}

	protected BucketQuota getBucketQuotaImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.QUOTA.getOriginalStringCode(), "");

		Response httpResponse = performRestGet(bucketName, null, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		BucketQuota ret = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), BucketQuotaHandler.class, false).getQuota();
		setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
		setStatusCode(ret, httpResponse.code());
		return ret;
	}

	protected BucketStoragePolicyConfiguration getBucketStoragePolicyImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(this.getSpecialParamForStorageClass().getOriginalStringCode(), "");

		Response httpResponse = performRestGet(bucketName, null, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		BucketStoragePolicyConfiguration ret = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), BucketStoragePolicyHandler.class, false)
				.getStoragePolicy();
		setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
		setStatusCode(ret, httpResponse.code());
		return ret;
	}

	protected BucketStorageInfo getBucketStorageInfoImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.STORAGEINFO.getOriginalStringCode(), "");

		Response httpResponse = performRestGet(bucketName, null, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		BucketStorageInfo ret = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), BucketStorageInfoHandler.class, false)
				.getStorageInfo();
		setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
		setStatusCode(ret, httpResponse.code());
		return ret;
	}

	protected AccessControlList getBucketAclImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");

		Response httpResponse = performRestGet(bucketName, null, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		AccessControlList ret = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), AccessControlListHandler.class, false)
				.getAccessControlList();
		setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
		setStatusCode(ret, httpResponse.code());
		return ret;
	}

	protected AccessControlList getObjectAclImpl(String bucketName, String objectKey, String versionId)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");
		if (ServiceUtils.isValid(versionId)) {
			requestParameters.put(ObsRequestParams.VERSION_ID, versionId.trim());
		}
		Response httpResponse = performRestGet(bucketName, objectKey, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		AccessControlList ret = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), AccessControlListHandler.class, false)
				.getAccessControlList();
		setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
		setStatusCode(ret, httpResponse.code());
		return ret;
	}

	protected DeleteObjectResult deleteObjectImpl(String bucketName, String objectKey, String versionId)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		if (versionId != null) {
			requestParameters.put(ObsRequestParams.VERSION_ID, versionId);
		}

		Response response = performRestDelete(bucketName, objectKey, requestParameters);

		DropFileResult result = new DropFileResult(Boolean.valueOf(response.header(this.getIHeaders().deleteMarkerHeader())), 
		        objectKey, response.header(this.getIHeaders().versionIdHeader()));
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
	
	protected ReadAheadResult DeleteReadAheadObjectsImpl(String bucketName, String prefix) throws ServiceException {
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
	
	protected ReadAheadQueryResult queryReadAheadObjectsTaskImpl(String bucketName, String taskId) throws ServiceException {
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

	protected BucketDirectColdAccess getBucketDirectColdAccessImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.DIRECTCOLDACCESS.getOriginalStringCode(), "");
		Response httpResponse = this.performRestGet(bucketName, null, requestParameters, null);

		this.verifyResponseContentType(httpResponse);

		BucketDirectColdAccess result = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), BucketDirectColdAccessHandler.class, false)
				.getBucketDirectColdAccess();
		setResponseHeaders(result, this.cleanResponseHeaders(httpResponse));
		setStatusCode(result, httpResponse.code());
		return result;
	}

	protected HeaderResponse setBucketDirectColdAccessImpl(String bucketName, BucketDirectColdAccess access)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.DIRECTCOLDACCESS.getOriginalStringCode(), "");
		Map<String, String> headers = new HashMap<String, String>();

		String requestXmlElement = this.getIConvertor().transBucketDirectColdAccess(access);

		headers.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
		headers.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

		Response response = this.performRestPut(bucketName, null, headers, requestParameters,
				createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected HeaderResponse deleteBucketDirectColdAccessImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(SpecialParamEnum.DIRECTCOLDACCESS.getOriginalStringCode(), "");
		Response response = performRestDelete(bucketName, null, requestParameters);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
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
		
		if (listObjectsRequest.getListTimeout() > 0) {
			putHeader(headers, this.getIHeaders().listTimeoutHeader(), String.valueOf(listObjectsRequest.getListTimeout()));
		}
		
		return new TransResult(headers, params, null);
	}

	protected ObjectListing listObjectsImpl(ListObjectsRequest listObjectsRequest) throws ServiceException {

		TransResult result = this.transListObjectsRequest(listObjectsRequest);

		Response httpResponse = performRestGet(listObjectsRequest.getBucketName(), null, result.getParams(), null);

		this.verifyResponseContentType(httpResponse);

		ListObjectsHandler listObjectsHandler = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), ListObjectsHandler.class, true);

		ObjectListing objList = new ObjectListing(listObjectsHandler.getObjects(), listObjectsHandler.getCommonPrefixes(), listObjectsHandler.getBucketName() == null ? listObjectsRequest.getBucketName()
				: listObjectsHandler.getBucketName(), listObjectsHandler.isListingTruncated(), listObjectsHandler.getRequestPrefix() == null ? listObjectsRequest.getPrefix()
						: listObjectsHandler.getRequestPrefix(), listObjectsHandler.getRequestMarker() == null ? listObjectsRequest.getMarker()
								: listObjectsHandler.getRequestMarker(), listObjectsHandler.getRequestMaxKeys(), listObjectsHandler.getRequestDelimiter() == null ? listObjectsRequest.getDelimiter()
								: listObjectsHandler.getRequestDelimiter(), listObjectsHandler.getMarkerForNextListing(), httpResponse.header(this.getIHeaders().bucketRegionHeader()));
		setResponseHeaders(objList, this.cleanResponseHeaders(httpResponse));
		setStatusCode(objList, httpResponse.code());
		return objList;
	}

	protected ListBucketsResult listAllBucketsImpl(ListBucketsRequest request) throws ServiceException {
		Map<String, String> headers = new HashMap<String, String>();
		if (request != null && request.isQueryLocation()) {
			this.putHeader(headers, this.getIHeaders().locationHeader(), Constants.TRUE);
		}
		Response httpResponse = performRestGetForListBuckets("", null, null, headers);

		this.verifyResponseContentType(httpResponse);

		ListBucketsHandler handler = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(httpResponse), ListBucketsHandler.class, true);
		
		Map<String, Object> responseHeaders = this.cleanResponseHeaders(httpResponse);
		
		ListBucketsResult result = new ListBucketsResult(handler.getBuckets(), handler.getOwner());
		setResponseHeaders(result, responseHeaders);
		setStatusCode(result, httpResponse.code());
		
		return result;
	}

	protected HeaderResponse setBucketLoggingConfigurationImpl(String bucketName,
			BucketLoggingConfiguration status, boolean updateTargetACLifRequired) throws ServiceException {
		if (status.isLoggingEnabled() && updateTargetACLifRequired
				&& this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS) {
			boolean isSetLoggingGroupWrite = false;
			boolean isSetLoggingGroupReadACP = false;
			String groupIdentifier = Constants.LOG_DELIVERY_URI;

			AccessControlList logBucketACL = getBucketAclImpl(status.getTargetBucketName());
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
					log.warn("Target logging bucket '" + status.getTargetBucketName()
							+ "' does not have the necessary ACL settings, updating ACL now");
				}
				if (logBucketACL.getOwner() != null) {
					logBucketACL.getOwner().setDisplayName(null);
				}
				logBucketACL.grantPermission(GroupGrantee.LOG_DELIVERY, Permission.PERMISSION_WRITE);
				logBucketACL.grantPermission(GroupGrantee.LOG_DELIVERY, Permission.PERMISSION_READ_ACP);
				setBucketAclImpl(status.getTargetBucketName(), null, logBucketACL);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Target logging bucket '" + status.getTargetBucketName()
							+ "' has the necessary ACL settings");
				}
			}
		}

		return setBucketLoggingConfigurationImpl(bucketName, status);
	}

	protected HeaderResponse deleteBucketImpl(String bucketName) throws ServiceException {
		Response response = performRestDelete(bucketName, null, null);
		return this.build(response);
	}

	Map<String, Object> cleanResponseHeaders(Response response) {
		Map<String, List<String>> map = response.headers().toMultimap();
		return ServiceUtils.cleanRestMetadataMap(map, this.getIHeaders().headerPrefix(),
				this.getIHeaders().headerMetaPrefix());
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
		this.transSseCHeaders(request.getSseCHeader(), headers, iheaders);

		InputStream input = null;
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
				input = new FileInputStream(request.getFile());
				input.skip(offset);
			} catch (Exception e) {
				ServiceUtils.closeStream(input);
				throw new ServiceException(e);
			}
			
			if(request.getProgressListener() != null) {
				ProgressManager progressManager = new SimpleProgressManager(contentLength, 0, request.getProgressListener(), 
						request.getProgressInterval() > 0 ? request.getProgressInterval() : ObsConstraint.DEFAULT_PROGRESS_INTERVAL);
				input = new ProgressInputStream(input, progressManager);
			}
			
		} else if (null != request.getInput()) {
			if(request.getPartSize() != null && request.getPartSize() > 0){
				contentLength = request.getPartSize();
			}
			input = request.getInput();
			
			if(input != null && request.getProgressListener() != null) {
				ProgressManager progressManager = new SimpleProgressManager(contentLength, 0, request.getProgressListener(), 
						request.getProgressInterval() > 0 ? request.getProgressInterval() : ObsConstraint.DEFAULT_PROGRESS_INTERVAL);
				input = new ProgressInputStream(input, progressManager);
			}
			
		}
		String contentType = Mimetypes.getInstance().getMimetype(request.getObjectKey());
		headers.put(CommonHeaders.CONTENT_TYPE, contentType);
		
		if(contentLength > -1) {
			this.putHeader(headers, CommonHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
		}
		RequestBody body = input == null ? null
				: new RepeatableRequestEntity(input, contentType, contentLength, this.obsProperties);
		return new TransResult(headers, params, body);
	}

	protected UploadPartResult uploadPartImpl(UploadPartRequest request) throws ServiceException {
		TransResult result = null;
		Response response;
		try {
			result = this.transUploadPartRequest(request);
			response = performRestPut(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
					result.getParams(), result.body, true);
		} finally {
			if (result != null && result.body != null && request.isAutoClose()) {
				RepeatableRequestEntity entity = (RepeatableRequestEntity) result.body;
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

	TransResult transCopyPartRequest(CopyPartRequest request) throws ServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ObsRequestParams.PART_NUMBER, String.valueOf(request.getPartNumber()));
		params.put(ObsRequestParams.UPLOAD_ID, request.getUploadId());

		Map<String, String> headers = new HashMap<String, String>();
		IHeaders iheaders = this.getIHeaders();

		String sourceKey = RestUtils
				.encodeUrlString(request.getSourceBucketName()) + "/" + RestUtils
				.encodeUrlString(request.getSourceObjectKey());
		if (ServiceUtils.isValid(request.getVersionId())) {
			sourceKey += "?versionId=" + request.getVersionId().trim();
		}
		putHeader(headers, iheaders.copySourceHeader(), sourceKey);

		if (request.getByteRangeStart() != null) {
		    String rangeEnd = request.getByteRangeEnd() != null ? String.valueOf(request.getByteRangeEnd()) : "";
			String range = String.format("bytes=%s-%s", request.getByteRangeStart(), rangeEnd);
			putHeader(headers, iheaders.copySourceRangeHeader(), range);
		}

		this.transSseCHeaders(request.getSseCHeaderDestination(), headers, iheaders);
		this.transSseCSourceHeaders(request.getSseCHeaderSource(), headers, iheaders);

		return new TransResult(headers, params, null);
	}

	protected CopyPartResult copyPartImpl(CopyPartRequest request) throws ServiceException {

		TransResult result = this.transCopyPartRequest(request);
		Response response = this.performRestPut(request.getDestinationBucketName(), request.getDestinationObjectKey(),
				result.getHeaders(), result.getParams(), null, false);
		this.verifyResponseContentType(response);

		CopyPartResult ret = getXmlResponseSaxParser()
				.parse(new HttpMethodReleaseInputStream(response), CopyPartResultHandler.class, true)
				.getCopyPartResult(request.getPartNumber());
		setResponseHeaders(ret, this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}
	
	HeaderResponse build(Response res){
    	HeaderResponse response = new HeaderResponse();
        setResponseHeaders(response, this.cleanResponseHeaders(res));
        setStatusCode(response, res.code());
        return response;
    }
	
	
    static HeaderResponse build(Map<String,Object> responseHeaders){
    	HeaderResponse response = new HeaderResponse();
        setResponseHeaders(response, responseHeaders);
        return response;
    }
    
    static void setStatusCode(HeaderResponse response, int statusCode)
    {
    	response.setStatusCode(statusCode);
    }

    static void setResponseHeaders(HeaderResponse response, Map<String,Object> responseHeaders)
    {
    	response.setResponseHeaders(responseHeaders);
    }
    
    private String getObjectUrl(String bucketName, String objectKey) {
    	boolean pathStyle = this.isPathStyle();
    	boolean https = this.getHttpsOnly();
    	boolean isCname = this.isCname();
    	return new StringBuilder().append(https? "https://" : "http://")
    	.append(pathStyle || isCname? "" : bucketName + ".")
    	.append(this.getEndpoint())
    	.append(":").append(https? this.getHttpsPort(): this.getHttpPort())
    	.append("/")
    	.append(pathStyle? bucketName + "/" : "")
    	.append(RestUtils.uriEncode(objectKey, false)).toString();
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

	private AuthTypeEnum parseAuthTypeInResponse(String bucketName) throws ServiceException {
		Response response;
		try {
			response = getAuthTypeNegotiationResponseImpl(bucketName);
		} catch (ServiceException e) {
			if (e.getResponseCode() == 404 || e.getResponseCode() <= 0 || e.getResponseCode() == 408 || e.getResponseCode() >= 500) {
				throw e;
			} else {
				return AuthTypeEnum.V2;
			}
		}
		String apiVersion;
		return (response.code() == 200 && (apiVersion=response.headers().get("x-obs-api")) != null && apiVersion.compareTo("3.0") >= 0) ?
				AuthTypeEnum.OBS : AuthTypeEnum.V2;
	}

	private Response getAuthTypeNegotiationResponseImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put("apiversion", "");
		return performRestForApiVersion(bucketName, null, requestParameters, null);
	}
	
	protected ThreadPoolExecutor initThreadPool(AbstractBulkRequest request) {
        int taskThreadNum = request.getTaskThreadNum();
        int workQueenLength = request.getTaskQueueNum();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(taskThreadNum, taskThreadNum, 0, TimeUnit.SECONDS, 
                new LinkedBlockingQueue<Runnable>(workQueenLength));
        executor.setRejectedExecutionHandler(new BlockRejectedExecutionHandler());
        return executor;
    }
	
	protected void recordBulkTaskStatus(DefaultTaskProgressStatus progressStatus, TaskCallback<DeleteObjectResult, String> callback, 
	        TaskProgressListener listener, int interval) {
	    
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
	
	protected HeaderResponse setExtensionPolicyImpl(String bucketName, String policyDocument)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(RequestParamEnum.EXTENSION_POLICY.getOriginalStringCode(), "");

		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_JSON);
		metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX : Constants.OBS_HEADER_PREFIX)
				+ Constants.OEF_MARKER, Constants.YES);
		
		Response response = performRestPut(bucketName, null, metadata, requestParameters,
				this.createRequestBody(Mimetypes.MIMETYPE_JSON, policyDocument), false, true);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}

	protected QueryExtensionPolicyResult queryExtensionPolicyImpl(String bucketName) throws ServiceException{
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put(RequestParamEnum.EXTENSION_POLICY.getOriginalStringCode(), "");
		
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX : Constants.OBS_HEADER_PREFIX)
				+ Constants.OEF_MARKER, Constants.YES);
		
		Response response = performRestGet(bucketName, null, requestParams, metadata, true);
		
		this.verifyResponseContentTypeForJson(response);
		
		String body;
		try {
			body = response.body().string();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
		
		QueryExtensionPolicyResult ret = (QueryExtensionPolicyResult) JSONChange.jsonToObj(new QueryExtensionPolicyResult(), body);
		ret.setResponseHeaders(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}
	
	protected HeaderResponse deleteExtensionPolicyImpl(String bucketName) throws ServiceException {
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put(RequestParamEnum.EXTENSION_POLICY.getOriginalStringCode(), "");
		
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX : Constants.OBS_HEADER_PREFIX)
				+ Constants.OEF_MARKER, Constants.YES);
		
		Response response = performRestDelete(bucketName, null, requestParams, metadata, true);
		return this.build(response);
	}
	
	protected CreateAsynchFetchJobsResult createFetchJobImpl(String bucketName, String policyDocument) throws ServiceException{
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(RequestParamEnum.ASYNC_FETCH_JOBS.getOriginalStringCode(), "");

		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_JSON);
		metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX : Constants.OBS_HEADER_PREFIX)
				+ Constants.OEF_MARKER, Constants.YES);
		
		Response response = performRestPost(bucketName, null, metadata, requestParameters,
				this.createRequestBody(Mimetypes.MIMETYPE_JSON, policyDocument), false, true);
		
		this.verifyResponseContentTypeForJson(response);
		
		String body;
		try {
			body = response.body().string();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
		
		CreateAsynchFetchJobsResult ret = (CreateAsynchFetchJobsResult) JSONChange.jsonToObj(new CreateAsynchFetchJobsResult(), body);
		ret.setResponseHeaders(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}
	
	protected QueryAsynchFetchJobsResult queryFetchJobImpl(String bucketName, String jobId) throws ServiceException{
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put(RequestParamEnum.ASYNC_FETCH_JOBS.getOriginalStringCode() + "/" +jobId, "");
		
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_JSON);
		metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX : Constants.OBS_HEADER_PREFIX)
				+ Constants.OEF_MARKER, Constants.YES);
		
		Response response = performRestGet(bucketName, null, requestParams, metadata, true);
		
		this.verifyResponseContentTypeForJson(response);
		
		String body;
		try {
			body = response.body().string();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
		
		QueryAsynchFetchJobsResult ret = (QueryAsynchFetchJobsResult) JSONChange.jsonToObj(new QueryAsynchFetchJobsResult(), body);
		ret.setResponseHeaders(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		
		return ret;
	}
	
	protected HeaderResponse putDisPolicyImpl(String bucketName, String policyDocument)
			throws ServiceException {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(RequestParamEnum.DIS_POLICIES.getOriginalStringCode(), "");

		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_JSON);
		metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX : Constants.OBS_HEADER_PREFIX)
				+ Constants.OEF_MARKER, Constants.YES);
		
		Response response = performRestPut(bucketName, null, metadata, requestParameters,
				this.createRequestBody(Mimetypes.MIMETYPE_JSON, policyDocument), false, true);
		HeaderResponse ret = build(this.cleanResponseHeaders(response));
		setStatusCode(ret, response.code());
		return ret;
	}
	
	protected GetDisPolicyResult getDisPolicyImpl(String bucketName) throws ServiceException{
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put(RequestParamEnum.DIS_POLICIES.getOriginalStringCode(), "");
		
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX : Constants.OBS_HEADER_PREFIX)
				+ Constants.OEF_MARKER, Constants.YES);
		
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
		metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX : Constants.OBS_HEADER_PREFIX)
				+ Constants.OEF_MARKER, Constants.YES);
		
		Response response = performRestDelete(bucketName, null, requestParams, metadata, true);
		return this.build(response);
	}
	
	protected void verifyResponseContentTypeForJson(Response response) throws ServiceException {
		if (this.obsProperties.getBoolProperty(ObsConstraint.VERIFY_RESPONSE_CONTENT_TYPE, true)) {
			String contentType = response.header(Constants.CommonHeaders.CONTENT_TYPE);
			if(null == contentType) {
				throw new ServiceException(
						"Expected JSON document response  but received content type is null");
			} else if(-1 == contentType.toString().indexOf(Mimetypes.MIMETYPE_JSON)) {
				throw new ServiceException(
						"Expected JSON document response  but received content type is " + contentType);
			}
		}
	}

}

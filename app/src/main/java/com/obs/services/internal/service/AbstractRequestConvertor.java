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

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.Constants;
import com.obs.services.internal.IHeaders;
import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.RestStorageService;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.utils.Mimetypes;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.AvailableZoneEnum;
import com.obs.services.model.BucketTypeEnum;
import com.obs.services.model.GenericRequest;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.SpecialParamEnum;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.fs.FSStatusEnum;
import com.obs.services.model.fs.GetBucketFSStatusResult;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class AbstractRequestConvertor extends RestStorageService {
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

        public Map<String, String> getHeaders() {
            if (this.headers == null) {
                headers = new HashMap<String, String>();
            }
            return this.headers;
        }

        public Map<String, String> getParams() {
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
    
    /**
     * set requestHeader for requestPayment
     * 
     * @param isRequesterPays
     * @param headers
     * @param iheaders
     * @throws ServiceException
     */
    protected Map<String, String> transRequestPaymentHeaders(boolean isRequesterPays, Map<String, String> headers,
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
    protected Map<String, String> transRequestPaymentHeaders(GenericRequest request, Map<String, String> headers,
            IHeaders iheaders) throws ServiceException {
        if (null != request) {
            return transRequestPaymentHeaders(request.isRequesterPays(), headers, iheaders);
        }

        return null;
    }
    
    protected String getHeaderByMethodName(String code) {
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
    
    protected void putHeader(Map<String, String> headers, String key, String value) {
        if (ServiceUtils.isValid(key)) {
            headers.put(key, value);
        }
    }
    
    protected HeaderResponse build(Response res) {
        HeaderResponse response = new HeaderResponse();
        setResponseHeaders(response, this.cleanResponseHeaders(res));
        setStatusCode(response, res.code());
        return response;
    }
    
    protected static void setStatusCode(HeaderResponse response, int statusCode) {
        response.setStatusCode(statusCode);
    }
    
    protected Map<String, Object> cleanResponseHeaders(Response response) {
        Map<String, List<String>> map = response.headers().toMultimap();
        return ServiceUtils.cleanRestMetadataMap(map, this.getIHeaders().headerPrefix(),
                this.getIHeaders().headerMetaPrefix());
    }
    
    protected void setResponseHeaders(HeaderResponse response, Map<String, Object> responseHeaders) {
        response.setResponseHeaders(responseHeaders);
    }
    
    protected SpecialParamEnum getSpecialParamForStorageClass() {
        return this.getProviderCredentials().getAuthType() == AuthTypeEnum.OBS ? SpecialParamEnum.STORAGECLASS
                : SpecialParamEnum.STORAGEPOLICY;
    }
    
    protected HeaderResponse build(Map<String, Object> responseHeaders) {
        HeaderResponse response = new HeaderResponse();
        setResponseHeaders(response, responseHeaders);
        return response;
    }
    
    protected RequestBody createRequestBody(String mimeType, String content) throws ServiceException {
        if (log.isTraceEnabled()) {
            log.trace("Entity Content:" + content);
        }
        return RequestBody.create(MediaType.parse(mimeType), content.getBytes(StandardCharsets.UTF_8));
    }

    protected GetBucketFSStatusResult getOptionInfoResult(Response response) {

        Headers headers = response.headers();

        Map<String, List<String>> map = headers.toMultimap();
        String maxAge = headers.get(Constants.CommonHeaders.ACCESS_CONTROL_MAX_AGE);

        IHeaders iheaders = this.getIHeaders();
        FSStatusEnum status = FSStatusEnum.getValueFromCode(headers.get(iheaders.fsFileInterfaceHeader()));

        BucketTypeEnum bucketType = BucketTypeEnum.OBJECT;
        if (FSStatusEnum.ENABLED == status) {
            bucketType = BucketTypeEnum.PFS;
        }

        GetBucketFSStatusResult output = new GetBucketFSStatusResult.Builder()
                .allowOrigin(headers.get(Constants.CommonHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
                .allowHeaders(map.get(Constants.CommonHeaders.ACCESS_CONTROL_ALLOW_HEADERS))
                .maxAge(maxAge == null ? 0 : Integer.parseInt(maxAge))
                .allowMethods(map.get(Constants.CommonHeaders.ACCESS_CONTROL_ALLOW_METHODS))
                .exposeHeaders(map.get(Constants.CommonHeaders.ACCESS_CONTROL_EXPOSE_HEADERS))
                .storageClass(StorageClassEnum.getValueFromCode(headers.get(iheaders.defaultStorageClassHeader())))
                .location(headers.get(iheaders.bucketRegionHeader()))
                .obsVersion(headers.get(iheaders.serverVersionHeader()))
                .status(status)
                .availableZone(AvailableZoneEnum.getValueFromCode(headers.get(iheaders.azRedundancyHeader())))
                .epid(headers.get(iheaders.epidHeader()))
                .bucketType(bucketType).build();
        
        setResponseHeaders(output, this.cleanResponseHeaders(response));
        setStatusCode(output, response.code());
        return output;
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

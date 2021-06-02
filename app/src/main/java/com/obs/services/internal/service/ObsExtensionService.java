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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.obs.services.internal.Constants;
import com.obs.services.internal.Constants.CommonHeaders;
import com.obs.services.internal.Constants.ObsRequestParams;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.utils.JSONChange;
import com.obs.services.internal.utils.Mimetypes;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.ReadAheadQueryResult;
import com.obs.services.model.ReadAheadRequest;
import com.obs.services.model.ReadAheadResult;
import com.oef.services.model.CreateAsynchFetchJobsResult;
import com.oef.services.model.DisPolicy;
import com.oef.services.model.GetDisPolicyResult;
import com.oef.services.model.QueryAsynchFetchJobsResult;
import com.oef.services.model.QueryExtensionPolicyResult;
import com.oef.services.model.RequestParamEnum;

import okhttp3.Response;

public abstract class ObsExtensionService extends ObsFileService {
    protected HeaderResponse setExtensionPolicyImpl(String bucketName, String policyDocument) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(RequestParamEnum.EXTENSION_POLICY.getOriginalStringCode(), "");

        return performRestPut(bucketName, policyDocument, requestParameters);
    }

    protected QueryExtensionPolicyResult queryExtensionPolicyImpl(String bucketName) throws ServiceException {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(RequestParamEnum.EXTENSION_POLICY.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        Response response = performRestGet(bucketName, requestParams, metadata);

        String body = readBodyFromResponse(response);

        QueryExtensionPolicyResult ret = (QueryExtensionPolicyResult) JSONChange
                .jsonToObj(new QueryExtensionPolicyResult(), body);
        ret.setResponseHeaders(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    private String readBodyFromResponse(Response response) {
        String body;
        try {
            body = response.body().string();
        } catch (IOException e) {
            throw new ServiceException(e);
        }
        return body;
    }

    private Response performRestGet(String bucketName, Map<String, String> requestParams,
            Map<String, String> metadata) {
        metadata.put((this.getProviderCredentials().getAuthType() != AuthTypeEnum.OBS ? Constants.V2_HEADER_PREFIX
                : Constants.OBS_HEADER_PREFIX) + Constants.OEF_MARKER, Constants.YES);

        Response response = performRestGet(bucketName, null, requestParams, metadata, true);

        this.verifyResponseContentTypeForJson(response);
        return response;
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

        String body = readBodyFromResponse(response);

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
        Response response = performRestGet(bucketName, requestParams, metadata);

        String body = readBodyFromResponse(response);

        QueryAsynchFetchJobsResult ret = (QueryAsynchFetchJobsResult) JSONChange
                .jsonToObj(new QueryAsynchFetchJobsResult(), body);
        ret.setResponseHeaders(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());

        return ret;
    }

    protected HeaderResponse putDisPolicyImpl(String bucketName, String policyDocument) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(RequestParamEnum.DIS_POLICIES.getOriginalStringCode(), "");

        return performRestPut(bucketName, policyDocument, requestParameters);
    }

    private HeaderResponse performRestPut(String bucketName, String policyDocument,
            Map<String, String> requestParameters) {
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
        Response response = performRestGet(bucketName, requestParams, metadata);

        String body = readBodyFromResponse(response);

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
    
    protected ReadAheadResult readAheadObjectsImpl(ReadAheadRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(Constants.ObsRequestParams.READAHEAD, "");
        requestParameters.put(Constants.ObsRequestParams.PREFIX, request.getPrefix());

        Map<String, String> metadata = new HashMap<String, String>();
        String cacheControl = request.getCacheOption().getCode() + ", ttl=" + request.getTtl();
        metadata.put(ObsRequestParams.X_CACHE_CONTROL, cacheControl);

        Response response = performRestPost(request.getBucketName(), null, metadata, requestParameters, null, false);

        this.verifyResponseContentTypeForJson(response);

        String body = readBodyFromResponse(response);

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

        String body = readBodyFromResponse(response);

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

        String body = readBodyFromResponse(response);

        ReadAheadQueryResult result = (ReadAheadQueryResult) JSONChange.jsonToObj(new ReadAheadQueryResult(), body);
        result.setResponseHeaders(this.cleanResponseHeaders(response));
        setStatusCode(result, response.code());

        return result;
    }
}

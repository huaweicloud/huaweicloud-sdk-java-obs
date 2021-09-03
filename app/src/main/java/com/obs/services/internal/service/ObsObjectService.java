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

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.Constants;
import com.obs.services.internal.Constants.CommonHeaders;
import com.obs.services.internal.Constants.ObsRequestParams;
import com.obs.services.internal.RepeatableRequestEntity;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.utils.Mimetypes;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.AppendObjectRequest;
import com.obs.services.model.AppendObjectResult;
import com.obs.services.model.ModifyObjectRequest;
import com.obs.services.model.ModifyObjectResult;
import com.obs.services.model.RenameObjectRequest;
import com.obs.services.model.RenameObjectResult;
import com.obs.services.model.RestoreObjectRequest;
import com.obs.services.model.RestoreObjectRequest.RestoreObjectStatus;
import com.obs.services.model.RestoreObjectResult;
import com.obs.services.model.SpecialParamEnum;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.TruncateObjectRequest;
import com.obs.services.model.TruncateObjectResult;

import okhttp3.Response;

public abstract class ObsObjectService extends ObsMultipartObjectService {
    private static final ILogger log = LoggerBuilder.getLogger(ObsObjectService.class);
    
    protected TruncateObjectResult truncateObjectImpl(TruncateObjectRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.TRUNCATE.getOriginalStringCode(), "");
        requestParameters.put(Constants.ObsRequestParams.LENGTH, String.valueOf(request.getNewLength()));

        Response response = performRestPut(request.getBucketName(), request.getObjectKey(),
                transRequestPaymentHeaders(request, null, this.getIHeaders()), requestParameters, null, true);
        TruncateObjectResult result = new TruncateObjectResult();
        setHeadersAndStatus(result, response);
        return result;
    }
    
    protected RenameObjectResult renameObjectImpl(RenameObjectRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.RENAME.getOriginalStringCode(), "");
        requestParameters.put(Constants.ObsRequestParams.NAME, request.getNewObjectKey());

        Response response = performRestPost(request.getBucketName(), request.getObjectKey(),
                transRequestPaymentHeaders(request, null, this.getIHeaders()), requestParameters, null, true);
        RenameObjectResult result = new RenameObjectResult();
        setHeadersAndStatus(result, response);
        return result;
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

        setHeadersAndStatus(ret, response);
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
                    result.getParams(), result.getBody(), true, false, request.isEncodeHeaders());
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

        setHeadersAndStatus(ret, response);
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
                    result.getParams(), result.getBody(), true, false, request.isEncodeHeaders());
        } finally {
            if (result != null && result.getBody() != null && request.isAutoClose()) {
                if (result.getBody() instanceof Closeable) {
                    ServiceUtils.closeStream((Closeable) result.getBody());
                }
            }
        }
        ModifyObjectResult ret = new ModifyObjectResult();

        setHeadersAndStatus(ret, response, request.isEncodeHeaders());
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
}

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

package com.oef.services;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.utils.JSONChange;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.HeaderResponse;
import com.oef.services.model.CreateAsyncFetchJobsRequest;
import com.oef.services.model.CreateAsynchFetchJobsResult;
import com.oef.services.model.PutExtensionPolicyRequest;
import com.oef.services.model.QueryAsynchFetchJobsResult;
import com.oef.services.model.QueryExtensionPolicyResult;

/**
 * OEF客户端
 */
public class OefClient extends ObsClient implements IOefClient {

    private static final ILogger ILOG = LoggerBuilder.getLogger(OefClient.class);

    /**
     * 构造函数
     * 
     * @param endPoint
     *            OEF服务地址
     * 
     */
    public OefClient(String endPoint) {
        super(endPoint);
    }

    /**
     * 构造函数
     * 
     * @param config
     *            OEF客户端配置参数
     * 
     */
    public OefClient(ObsConfiguration config) {
        super(config);
    }

    /**
     * 构造函数
     * 
     * @param accessKey
     *            访问密钥中的AK
     * @param secretKey
     *            访问密钥中的SK
     * @param endPoint
     *            OEF服务地址
     * 
     */
    public OefClient(String accessKey, String secretKey, String endPoint) {
        super(accessKey, secretKey, endPoint);
    }

    /**
     * 构造函数
     * 
     * @param accessKey
     *            访问密钥中的AK
     * @param secretKey
     *            访问密钥中的SK
     * @param config
     *            OEF客户端配置参数
     * 
     */
    public OefClient(String accessKey, String secretKey, ObsConfiguration config) {
        super(accessKey, secretKey, config);
    }

    /**
     * 构造函数
     * 
     * @param accessKey
     *            临时访问密钥中的AK
     * @param secretKey
     *            临时访问密钥中的SK
     * @param securityToken
     *            安全令牌
     * @param endPoint
     *            OEF的服务地址
     * 
     */
    public OefClient(String accessKey, String secretKey, String securityToken, String endPoint) {
        super(accessKey, secretKey, securityToken, endPoint);
    }

    /**
     * 构造函数
     * 
     * @param accessKey
     *            临时访问密钥中的AK
     * @param secretKey
     *            临时访问密钥中的SK
     * @param securityToken
     *            安全令牌
     * @param config
     *            OEF客户端配置参数
     * 
     */
    public OefClient(String accessKey, String secretKey, String securityToken, ObsConfiguration config) {
        super(accessKey, secretKey, securityToken, config);
    }

    @Override
    public HeaderResponse putExtensionPolicy(final String bucketName, final PutExtensionPolicyRequest request)
            throws ObsException {
        ServiceUtils.asserParameterNotNull(bucketName, "bucket is null");
        ServiceUtils.asserParameterNotNull(request, "policy is null");
        if (null == request.getCompress() && null == request.getFetch() && null == request.getTranscode()) {
            throw new IllegalArgumentException(
                    "putExtensionPolicy failed: compress, fetch and transcode cannot be empty at the same time");
        }
        return this.doActionWithResult("putExtensionPolicy", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        String policy = JSONChange.objToJson(request);
                        return OefClient.this.setExtensionPolicyImpl(bucketName, policy);
                    }
                });
    }

    @Override
    public QueryExtensionPolicyResult queryExtensionPolicy(final String bucketName) throws ObsException {
        ServiceUtils.asserParameterNotNull(bucketName, "bucket is null");
        return this.doActionWithResult("queryExtensionPolicy", bucketName,
                new ActionCallbackWithResult<QueryExtensionPolicyResult>() {
                    @Override
                    public QueryExtensionPolicyResult action() throws ServiceException {
                        return OefClient.this.queryExtensionPolicyImpl(bucketName);

                    }
                });
    }

    @Override
    public HeaderResponse deleteExtensionPolicy(final String bucketName) throws ObsException {
        ServiceUtils.asserParameterNotNull(bucketName, "bucket is null");
        return this.doActionWithResult("deleteExtensionPolicy", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return OefClient.this.deleteExtensionPolicyImpl(bucketName);
                    }
                });
    }

    @Override
    public CreateAsynchFetchJobsResult createFetchJob(final CreateAsyncFetchJobsRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request.getBucketName(), "bucket is null");
        ServiceUtils.asserParameterNotNull(request, "policy is null");
        ServiceUtils.asserParameterNotNull(request.getUrl(), "url is null");
        if (request.getCallBackUrl() != null) {
            ServiceUtils.asserParameterNotNull(request.getCallBackBody(),
                    "callbackbody is null when callbackurl is not null");
        }
        return this.doActionWithResult("CreateFetchJob", request.getBucketName(),
                new ActionCallbackWithResult<CreateAsynchFetchJobsResult>() {
                    @Override
                    public CreateAsynchFetchJobsResult action() throws ServiceException {
                        String policy = JSONChange.objToJson(request);
                        return OefClient.this.createFetchJobImpl(request.getBucketName(), policy);

                    }
                });
    }

    @Override
    public QueryAsynchFetchJobsResult queryFetchJob(final String bucketName, final String jobId) throws ObsException {
        ServiceUtils.asserParameterNotNull(bucketName, "bucket is null");
        ServiceUtils.asserParameterNotNull(jobId, "jobId is null");
        return this.doActionWithResult("queryFetchJob", bucketName,
                new ActionCallbackWithResult<QueryAsynchFetchJobsResult>() {
                    @Override
                    public QueryAsynchFetchJobsResult action() throws ServiceException {
                        return OefClient.this.queryFetchJobImpl(bucketName, jobId);

                    }
                });
    }
}

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
**/
package com.oef.services;

import java.util.Date;
import com.obs.log.ILogger;
import com.obs.log.InterfaceLogBean;
import com.obs.log.LoggerBuilder;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.Constants;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.utils.AccessLoggerUtils;
import com.obs.services.internal.utils.JSONChange;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.HeaderResponse;
import com.oef.services.model.CreateAsyncFetchJobsRequest;
import com.oef.services.model.CreateAsynchFetchJobsResult;
import com.oef.services.model.PutExtensionPolicyRequest;
import com.oef.services.model.QueryExtensionPolicyResult;
import com.oef.services.model.QueryAsynchFetchJobsResult;

/**
 * OEF client
 */
public class OefClient extends ObsClient implements IOefClient {

	private static final ILogger ILOG = LoggerBuilder.getLogger(OefClient.class);


	/**
	 * Constructor
	 * 
	 * @param endPoint
	 *            OEF service address
	 * 
	 */
	public OefClient(String endPoint) {
		super(endPoint);
	}

	/**
	 * Constructor
	 * 
	 * @param config
	 *            Configuration parameters of OEF client
	 * 
	 */
	public OefClient(ObsConfiguration config) {
		super(config);
	}

	/**
	 * Constructor
	 * 
	 * @param accessKey
	 *            Access key ID
	 * @param secretKey
	 *            Secret access key
	 * @param endPoint
	 *            OEF service address
	 * 
	 */
	public OefClient(String accessKey, String secretKey, String endPoint) {
		super(accessKey, secretKey, endPoint);
	}

	/**
	 * Constructor
	 * 
	 * @param accessKey
	 *            Access key ID
	 * @param secretKey
	 *            Secret access key
	 * @param config
	 *            Configuration parameters of OEF client
	 * 
	 */
	public OefClient(String accessKey, String secretKey, ObsConfiguration config) {
		super(accessKey, secretKey, config);
	}

	/**
	 * Constructor
	 * 
	 * @param accessKey
	 *            AK in the temporary access keys
	 * @param secretKey
	 *            SK in the temporary access keys
	 * @param securityToken
	 *            Security token
	 * @param endPoint
	 *            OEF service address
	 * 
	 */
	public OefClient(String accessKey, String secretKey, String securityToken, String endPoint) {
		super(accessKey, secretKey, securityToken, endPoint);
	}

	/**
	 * Constructor
	 * 
	 * @param accessKey
	 *            AK in the temporary access keys
	 * @param secretKey
	 *            SK in the temporary access keys
	 * @param securityToken
	 *            Security Token
	 * @param config
	 *            Configuration parameters of OEF client
	 * 
	 */
	public OefClient(String accessKey, String secretKey, String securityToken, ObsConfiguration config) {
		super(accessKey, secretKey, securityToken, config);
	}


	@Override
	public HeaderResponse putExtensionPolicy(final String bucketName, final PutExtensionPolicyRequest request) throws ObsException {
		ServiceUtils.asserParameterNotNull(bucketName, "bucket is null");
		ServiceUtils.asserParameterNotNull(request, "policy is null");
		if(null == request.getCompress() && null == request.getFetch() && null == request.getTranscode()) {
			throw new IllegalArgumentException("putExtensionPolicy failed: compress, fetch and transcode cannot be empty at the same time");
		}
		return this.doActionWithResult("putExtensionPolicy", bucketName, new ActionCallbackWithResult<HeaderResponse>() {
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
		return this.doActionWithResult("queryExtensionPolicy", bucketName, new ActionCallbackWithResult<QueryExtensionPolicyResult>() {
			@Override
			public QueryExtensionPolicyResult action() throws ServiceException {
				return OefClient.this.queryExtensionPolicyImpl(bucketName);

			}
		});
	}
	
	@Override
	public HeaderResponse deleteExtensionPolicy(final String bucketName) throws ObsException {
		ServiceUtils.asserParameterNotNull(bucketName, "bucket is null");
		return this.doActionWithResult("deleteExtensionPolicy", bucketName, new ActionCallbackWithResult<HeaderResponse>() {
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
		if(request.getCallBackUrl() != null) {
			ServiceUtils.asserParameterNotNull(request.getCallBackBody(), "callbackbody is null when callbackurl is not null");
		}
		return this.doActionWithResult("CreateFetchJob", request.getBucketName(), new ActionCallbackWithResult<CreateAsynchFetchJobsResult>() {
			@Override
			public CreateAsynchFetchJobsResult action() throws ServiceException {
				String policy = JSONChange.objToJson(request);
				return OefClient.this.createFetchJobImpl(request.getBucketName(), policy);

			}
		});
	}
	
	@Override
	public QueryAsynchFetchJobsResult queryFetchJob(final String bucketName, final String jobId) throws ObsException{
		ServiceUtils.asserParameterNotNull(bucketName, "bucket is null");
		ServiceUtils.asserParameterNotNull(jobId, "jobId is null");
		return this.doActionWithResult("queryFetchJob", bucketName, new ActionCallbackWithResult<QueryAsynchFetchJobsResult>() {
			@Override
			public QueryAsynchFetchJobsResult action() throws ServiceException {
				return OefClient.this.queryFetchJobImpl(bucketName, jobId);
				 
			}
		});
	}
	
	private abstract class ActionCallbackWithResult<T> {

		abstract T action() throws ServiceException;

		void authTypeNegotiate(String bucketName) throws ServiceException {
			AuthTypeEnum authTypeEnum = OefClient.this.getApiVersion(bucketName);
			OefClient.this.getProviderCredentials().setThreadLocalAuthType(authTypeEnum);
		}
	}
	
	private <T> T doActionWithResult(String action, String bucketName, ActionCallbackWithResult<T> callback)
			throws ObsException {
	    if (! this.isCname()) {
	        ServiceUtils.asserParameterNotNull(bucketName, "bucketName is null");
        }
		InterfaceLogBean reqBean = new InterfaceLogBean(action, this.getEndpoint(), "");
		try {
			long start = System.currentTimeMillis();
			if (this.isAuthTypeNegotiation()) {
				callback.authTypeNegotiate(bucketName);
			}
			T ret = callback.action();
			reqBean.setRespTime(new Date());
			reqBean.setResultCode(Constants.RESULTCODE_SUCCESS);
			if (ILOG.isInfoEnabled()) {
				ILOG.info(reqBean);
			}
			if (ILOG.isInfoEnabled()) {
				ILOG.info("ObsClient [" + action + "] cost " + (System.currentTimeMillis() - start) + " ms");
			}
			return ret;
		} catch (ServiceException e) {
			
			ObsException ex = ServiceUtils.changeFromServiceException(e);
			if(ex.getResponseCode() >= 400 && ex.getResponseCode() < 500) {
				if(ILOG.isWarnEnabled()) {
					reqBean.setRespTime(new Date());
					reqBean.setResultCode(String.valueOf(e.getResponseCode()));
					ILOG.warn(reqBean);
				}
			}else if (ILOG.isErrorEnabled()) {
				reqBean.setRespTime(new Date());
				reqBean.setResultCode(String.valueOf(ex.getResponseCode()));
				ILOG.error(reqBean);
			}
			throw ex;
		} finally {
			if(this.isAuthTypeNegotiation()) {
				this.getProviderCredentials().removeThreadLocalAuthType();
			}
			AccessLoggerUtils.printLog();
		}
	}

}

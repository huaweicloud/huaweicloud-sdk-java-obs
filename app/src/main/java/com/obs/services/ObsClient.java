/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 **/
package com.obs.services;

import com.obs.log.ILogger;
import com.obs.log.InterfaceLogBean;
import com.obs.log.LoggerBuilder;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.*;
import com.obs.services.internal.Constants.CommonHeaders;
import com.obs.services.internal.consensus.CacheManager;
import com.obs.services.internal.consensus.SegmentLock;
import com.obs.services.internal.security.ProviderCredentials;
import com.obs.services.internal.task.*;
import com.obs.services.internal.utils.AccessLoggerUtils;
import com.obs.services.internal.utils.ReflectUtils;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.*;
import com.obs.services.model.PolicyConditionItem.ConditionOperator;
import com.obs.services.model.RestoreObjectRequest.RestoreObjectStatus;
import com.obs.services.model.fs.*;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

/**
 * ObsClient
 */
public class ObsClient extends ObsService implements Closeable, IObsClient, IFSClient {

    private static final ILogger ILOG = LoggerBuilder.getLogger(ObsClient.class);

    private void init(String accessKey, String secretKey, String securityToken, ObsConfiguration config) {
        InterfaceLogBean reqBean = new InterfaceLogBean("ObsClient", config.getEndPoint(), "");
        ProviderCredentials credentials = new ProviderCredentials(accessKey, secretKey, securityToken);
        ObsProperties obsProperties = ServiceUtils.changeFromObsConfiguration(config);
        credentials.setAuthType(config.getAuthType());
        this.obsProperties = obsProperties;
        this.credentials = credentials;
        this.obsProperties = obsProperties;
        this.keyManagerFactory = config.getKeyManagerFactory();
        this.trustManagerFactory = config.getTrustManagerFactory();
        if (this.isAuthTypeNegotiation()) {
            this.apiVersionCache = new CacheManager();
            this.getProviderCredentials().initThreadLocalAuthType();
            this.segmentLock = new SegmentLock();
        }
        this.initHttpClient(config.getHttpDispatcher());
        reqBean.setRespTime(new Date());
        reqBean.setResultCode(Constants.RESULTCODE_SUCCESS);
        if (ILOG.isInfoEnabled()) {
            ILOG.info(reqBean);
        }

        if (ILOG.isWarnEnabled()) {
            StringBuilder sb = new StringBuilder("[OBS SDK Version=");
            sb.append(Constants.OBS_SDK_VERSION);
            sb.append("];");
            sb.append("[Endpoint=");
            String ep = "";
            if (this.getHttpsOnly()) {
                ep = "https://" + this.getEndpoint() + ":" + this.getHttpsPort() + "/";
            } else {
                ep = "http://" + this.getEndpoint() + ":" + this.getHttpPort() + "/";
            }
            sb.append(ep);
            sb.append("];");
            sb.append("[Access Mode=");
            sb.append(this.isPathStyle() ? "Path" : "Virtul Hosting");
            sb.append("]");
            ILOG.warn(sb);
        }
    }

    /**
     * Constructor
     *
     * @param endPoint
     *            OBS endpoint
     *
     */
    public ObsClient(String endPoint) {
        ObsConfiguration config = new ObsConfiguration();
        config.setEndPoint(endPoint);
        this.init("", "", null, config);
    }

    /**
     * Constructor
     *
     * @param config
     *            Configuration parameters of ObsClient
     *
     */
    public ObsClient(ObsConfiguration config) {
        if (config == null) {
            config = new ObsConfiguration();
        }
        this.init("", "", null, config);
    }

    /**
     * Constructor
     *
     * @param accessKey
     *            AK in the access key
     * @param secretKey
     *            SK in the access key
     * @param endPoint
     *            OBS endpoint
     *
     */
    public ObsClient(String accessKey, String secretKey, String endPoint) {
        ObsConfiguration config = new ObsConfiguration();
        config.setEndPoint(endPoint);
        this.init(accessKey, secretKey, null, config);
    }

    /**
     * Constructor
     *
     * @param accessKey
     *            AK in the access key
     * @param secretKey
     *            SK in the access key
     * @param config
     *            Configuration parameters of ObsClient
     *
     */
    public ObsClient(String accessKey, String secretKey, ObsConfiguration config) {
        if (config == null) {
            config = new ObsConfiguration();
        }
        this.init(accessKey, secretKey, null, config);
    }

    /**
     * Constructor
     *
     * @param accessKey
     *            AK in the temporary access key
     * @param secretKey
     *            SK in the temporary access key
     * @param securityToken
     *            Security token
     * @param endPoint
     *            OBS endpoint
     *
     */
    public ObsClient(String accessKey, String secretKey, String securityToken, String endPoint) {
        ObsConfiguration config = new ObsConfiguration();
        config.setEndPoint(endPoint);
        this.init(accessKey, secretKey, securityToken, config);
    }

    /**
     * Constructor
     *
     * @param accessKey
     *            AK in the temporary access key
     * @param secretKey
     *            SK in the temporary access key
     * @param securityToken
     *            Security token
     * @param config
     *            Configuration parameters of ObsClient
     *
     */
    public ObsClient(String accessKey, String secretKey, String securityToken, ObsConfiguration config) {
        if (config == null) {
            config = new ObsConfiguration();
        }
        this.init(accessKey, secretKey, securityToken, config);
    }

	public ObsClient(IObsCredentialsProvider provider, String endPoint){
		ServiceUtils.asserParameterNotNull(provider, "ObsCredentialsProvider is null");
		ObsConfiguration config = new ObsConfiguration();
		config.setEndPoint(endPoint);
		this.init(provider.getSecurityKey().getAccessKey(), provider.getSecurityKey().getSecretKey(), provider.getSecurityKey().getSecurityToken(), config);
		this.credentials.setObsCredentialsProvider(provider);
	}

	public ObsClient(IObsCredentialsProvider provider, ObsConfiguration config){
		ServiceUtils.asserParameterNotNull(provider, "ObsCredentialsProvider is null");
		if (config == null) {
			config = new ObsConfiguration();
		}
		this.init(provider.getSecurityKey().getAccessKey(), provider.getSecurityKey().getSecretKey(), provider.getSecurityKey().getSecurityToken(), config);
		this.credentials.setObsCredentialsProvider(provider);
	}

	/* (non-Javadoc)
	 * @see com.obs.services.IObsClient#refresh(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void refresh(String accessKey, String secretKey, String securityToken) {
		ProviderCredentials credentials = new ProviderCredentials(accessKey, secretKey, securityToken);
		credentials.setAuthType(this.credentials.getAuthType());
		this.setProviderCredentials(credentials);
	}

    /**
     * Create a temporarily authorized URL.
     *
     * @param method
     *            HTTP request method
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param specialParam
     *            Special operator
     * @param expiryTime
     *            Time when the temporary authentication expires
     * @param headers
     *            Header information
     * @param queryParams
     *            Query parameter information
     * @return Temporarily authorized URL
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface fails to be called or access to OBS fails
     */
    @Deprecated
    public String createSignedUrl(HttpMethodEnum method, String bucketName, String objectKey,
                                  SpecialParamEnum specialParam, Date expiryTime, Map<String, String> headers,
                                  Map<String, Object> queryParams) throws ObsException {
        return this.createSignedUrl(method, bucketName, objectKey, specialParam,
                expiryTime == null ? ObsConstraint.DEFAULT_EXPIRE_SECONEDS
                        : (expiryTime.getTime() - System.currentTimeMillis()) / 1000,
                headers, queryParams);
    }

    /**
     * Create a temporarily authorized URL.
     *
     * @param method
     *            HTTP request method
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param specialParam
     *            Special operator
     * @param expires
     *            Time when the temporary authentication expires. The unit is second and the default value is 300.
     * @param headers
     *            Header information
     * @param queryParams
     *            Query parameter information
     * @return Temporarily authorized URL
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface fails to be called or access to OBS fails
     */
    @Deprecated
    public String createSignedUrl(HttpMethodEnum method, String bucketName, String objectKey,
                                  SpecialParamEnum specialParam, long expires, Map<String, String> headers, Map<String, Object> queryParams) {
        TemporarySignatureRequest request = new TemporarySignatureRequest();
        request.setMethod(method);
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setSpecialParam(specialParam);
        request.setHeaders(headers);
        request.setQueryParams(queryParams);
        if (expires > 0) {
            request.setExpires(expires);
        }
        return createTemporarySignature(request).getSignedUrl();
    }

    @Deprecated
    public V4TemporarySignatureResponse createV4TemporarySignature(V4TemporarySignatureRequest request) {
        ServiceUtils.asserParameterNotNull(request, "V4TemporarySignatureRequest is null");
        InterfaceLogBean reqBean = new InterfaceLogBean("createV4TemporarySignature", this.getEndpoint(), "");
        try {
            TemporarySignatureResponse response = this.createV4TemporarySignature((TemporarySignatureRequest) request);
            V4TemporarySignatureResponse res = new V4TemporarySignatureResponse(response.getSignedUrl());
            res.getActualSignedRequestHeaders().putAll(response.getActualSignedRequestHeaders());
            return res;
        } catch (Exception e) {
            reqBean.setRespTime(new Date());
            if (ILOG.isErrorEnabled()) {
                ILOG.error(reqBean);
            }
            throw new ObsException(e.getMessage(), e);
        }
    }

    @Deprecated
    public V4PostSignatureResponse createV4PostSignature(String acl, String contentType, long expires,
                                                         String bucketName, String objectKey) throws ObsException {
        V4PostSignatureRequest request = new V4PostSignatureRequest(expires, new Date(), bucketName, objectKey);
        request.getFormParams().put("acl", acl);
        request.getFormParams().put("content-type", contentType);
        return this.createV4PostSignature(request);
    }

    @Deprecated
    public V4PostSignatureResponse createV4PostSignature(long expires, String bucketName, String objectKey)
            throws ObsException {
        V4PostSignatureRequest request = new V4PostSignatureRequest(expires, new Date(), bucketName, objectKey);
        return this.createV4PostSignature(request);
    }

    @Deprecated
    public V4PostSignatureResponse createV4PostSignature(V4PostSignatureRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "V4PostSignatureRequest is null");
        InterfaceLogBean reqBean = new InterfaceLogBean("createV4PostSignature", this.getEndpoint(), "");
        try {
            V4PostSignatureResponse response = (V4PostSignatureResponse) this._createPostSignature(request, true);
            reqBean.setRespTime(new Date());
            reqBean.setResultCode(Constants.RESULTCODE_SUCCESS);
            if (ILOG.isInfoEnabled()) {
                ILOG.info(reqBean);
            }
            return response;
        } catch (Exception e) {
            reqBean.setRespTime(new Date());
            if (ILOG.isErrorEnabled()) {
                ILOG.error(reqBean);
            }
            throw new ObsException(e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#createTemporarySignature(com.obs.services.model.TemporarySignatureRequest)
     */
    @Override
    public TemporarySignatureResponse createTemporarySignature(TemporarySignatureRequest request) {
        ServiceUtils.asserParameterNotNull(request, "TemporarySignatureRequest is null");
        InterfaceLogBean reqBean = new InterfaceLogBean("createTemporarySignature", this.getEndpoint(), "");
        try {
            TemporarySignatureResponse response = this.getProviderCredentials().getAuthType() == AuthTypeEnum.V4
                    ? this.createV4TemporarySignature(request)
                    : this._createTemporarySignature(request);
            return response;
        } catch (Exception e) {
            reqBean.setRespTime(new Date());
            if (ILOG.isErrorEnabled()) {
                ILOG.error(reqBean);
            }
            throw new ObsException(e.getMessage(), e);
        }
    }

	/**
	 * Generate temporary authorization parameters for GET requests based on the object name prefix and validity period.
	 * @param bucketName Bucket name
	 * @param objectKey Object name
	 * @param prefix Object name prefix
	 * @param expiryDate Expiration date (ISO 8601 UTC)
	 * @param headers Header information
	 * @param queryParams Query parameter information
	 * @return Response to the request for temporary access authorization
     * @throws ObsException OBS SDK self-defined exception, thrown when the interface fails to be called or access to OBS fails
	 */
    public TemporarySignatureResponse createGetTemporarySignature(String bucketName, String objectKey, String prefix,
                                                                  Date expiryDate, Map<String, String> headers, Map<String, Object> queryParams) {
        try {
            PolicyTempSignatureRequest request = createPolicyGetRequest(bucketName, objectKey, prefix, headers, queryParams);
            request.setExpiryDate(expiryDate);
            TemporarySignatureResponse response = this._createTemporarySignature(request);
            return response;
        } catch (Exception e) {
            throw new ObsException(e.getMessage(), e);
        }
    }

	/**
     * Generate temporary authorization parameters for GET requests based on the object name prefix and validity period.
     * @param bucketName Bucket name
     * @param objectKey Object name
     * @param prefix Object name prefix
     * @param expires Validity period (seconds)
     * @param headers Header information
     * @param queryParams Query parameter information
     * @return Response to the request for temporary access authorization
     * @throws ObsException OBS SDK self-defined exception, thrown when the interface fails to be called or access to OBS fails
     */
    public TemporarySignatureResponse createGetTemporarySignature(String bucketName, String objectKey, String prefix,
                                                                  long expires, Map<String, String> headers, Map<String, Object> queryParams) {
        try {
            PolicyTempSignatureRequest request = createPolicyGetRequest(bucketName, objectKey, prefix, headers, queryParams);
            request.setExpires(expires);
            TemporarySignatureResponse response = this._createTemporarySignature(request);
            return response;
        } catch (Exception e) {
            throw new ObsException(e.getMessage(), e);
        }
    }

    private PolicyTempSignatureRequest createPolicyGetRequest(String bucketName, String objectKey, String prefix,
                                                              Map<String, String> headers, Map<String, Object> queryParams) {
        PolicyTempSignatureRequest request = new PolicyTempSignatureRequest(HttpMethodEnum.GET, bucketName, objectKey);
        List<PolicyConditionItem> conditions = new ArrayList<PolicyConditionItem>();
        PolicyConditionItem keyCondition = new PolicyConditionItem(ConditionOperator.STARTS_WITH, "key", prefix);
        String _bucket = this.isCname() ? this.getEndpoint() : bucketName;
        PolicyConditionItem bucketCondition = new PolicyConditionItem(ConditionOperator.EQUAL, "bucket", _bucket);
        conditions.add(keyCondition);
        conditions.add(bucketCondition);
        request.setConditions(conditions);
        request.setHeaders(headers);
        request.setQueryParams(queryParams);
        return request;
    }

    /**
     * Generate parameters for browser-based authorized access.
     *
     * @param acl
     *            Object ACL
     * @param contentType
     *            MIME type of the object
     * @param expires
     *            Validity period (in seconds)
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @return Response to the V4 browser-based authorized access
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface fails to be called or access to OBS fails
     */
    public PostSignatureResponse createPostSignature(String acl, String contentType, long expires, String bucketName,
                                                     String objectKey) throws ObsException {
        PostSignatureRequest request = new PostSignatureRequest(expires, new Date(), bucketName, objectKey);
        request.getFormParams().put(
                this.getProviderCredentials().getAuthType() == AuthTypeEnum.V4 ? "acl" : this.getIHeaders().aclHeader(),
                acl);
        request.getFormParams().put(CommonHeaders.CONTENT_TYPE, contentType);
        return this.createPostSignature(request);
    }

    /**
     * Generate parameters for browser-based authorized access.
     *
     * @param expires
     *            Validity period (in seconds)
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @return Response to the V4 browser-based authorized access
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface fails to be called or access to OBS fails
     */
    public PostSignatureResponse createPostSignature(long expires, String bucketName, String objectKey)
            throws ObsException {
        PostSignatureRequest request = new PostSignatureRequest(expires, new Date(), bucketName, objectKey);
        return this.createPostSignature(request);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#createPostSignature(com.obs.services.model.PostSignatureRequest)
     */
    @Override
    public PostSignatureResponse createPostSignature(PostSignatureRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "PostSignatureRequest is null");
        InterfaceLogBean reqBean = new InterfaceLogBean("createPostSignature", this.getEndpoint(), "");
        try {
            PostSignatureResponse response = this._createPostSignature(request, this.getProviderCredentials().getAuthType() == AuthTypeEnum.V4);
            reqBean.setRespTime(new Date());
            reqBean.setResultCode(Constants.RESULTCODE_SUCCESS);
            if (ILOG.isInfoEnabled()) {
                ILOG.info(reqBean);
            }
            return response;
        } catch (Exception e) {
            reqBean.setRespTime(new Date());
            if (ILOG.isErrorEnabled()) {
                ILOG.error(reqBean);
            }
            throw new ObsException(e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#createBucket(java.lang.String)
     */
    @Override
    public ObsBucket createBucket(String bucketName) throws ObsException {
        ObsBucket obsBucket = new ObsBucket();
        obsBucket.setBucketName(bucketName);
        return this.createBucket(obsBucket);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#createBucket(java.lang.String, java.lang.String)
     */
    @Override
    public ObsBucket createBucket(String bucketName, String location) throws ObsException {
        ObsBucket obsBucket = new ObsBucket();
        obsBucket.setBucketName(bucketName);
        obsBucket.setLocation(location);
        return this.createBucket(obsBucket);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#createBucket(com.obs.services.model.ObsBucket)
     */
    @Override
    public ObsBucket createBucket(final ObsBucket bucket) throws ObsException {
        CreateBucketRequest request = new CreateBucketRequest();
        request.setBucketName(bucket.getBucketName());
        request.setAcl(bucket.getAcl());
        request.setBucketStorageClass(bucket.getBucketStorageClass());
        request.setLocation(bucket.getLocation());
        return this.createBucket(request);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#createBucket(com.obs.services.model.CreateBucketRequest)
     */
    @Override
    public ObsBucket createBucket(final CreateBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "CreateBucketRequest is null");
        return this.doActionWithResult("createBucket", request.getBucketName(),
                new ActionCallbackWithResult<ObsBucket>() {
                    @Override
                    public ObsBucket action() throws ServiceException {
                        if (isCname()) {
                            throw new ServiceException("createBucket is not allowed in customdomain mode");
                        }
                        try {
                            return ObsClient.this.createBucketImpl(request);
                        } catch (ServiceException e) {
                            if (ObsClient.this.isAuthTypeNegotiation() && e.getResponseCode() == 400 &&
                                    "Unsupported Authorization Type".equals(e.getErrorMessage()) &&
                                    ObsClient.this.getProviderCredentials().getAuthType() == AuthTypeEnum.OBS) {
                                ObsClient.this.getProviderCredentials().setThreadLocalAuthType(AuthTypeEnum.V2);
                                return ObsClient.this.createBucketImpl(request);
                            } else {
                                throw e;
                            }
                        }
                    }

                    @Override
                    void authTypeNegotiate(String bucketName) throws ServiceException {
                        AuthTypeEnum authTypeEnum = ObsClient.this.getApiVersionCache().getApiVersionInCache(bucketName);
                        if (authTypeEnum == null) {
                            authTypeEnum = ObsClient.this.getApiVersion("");
                        }
                        ObsClient.this.getProviderCredentials().setThreadLocalAuthType(authTypeEnum);
                    }
                });
    }

    @Deprecated
    public ObsBucket createBucket(final S3Bucket bucket) throws ObsException {
        ServiceUtils.asserParameterNotNull(bucket, "bucket is null");
        ObsBucket obsBucket = new ObsBucket();
        obsBucket.setBucketName(bucket.getBucketName());
        obsBucket.setLocation(bucket.getLocation());
        obsBucket.setAcl(bucket.getAcl());
        obsBucket.setMetadata(bucket.getMetadata());
        obsBucket.setBucketStorageClass(bucket.getBucketStorageClass());
        return this.createBucket(obsBucket);
    }

    @Deprecated
    public List<S3Bucket> listBuckets() throws ObsException {
        List<ObsBucket> ret = this.listBuckets(null);
        List<S3Bucket> buckets = new ArrayList<S3Bucket>(ret.size());
        buckets.addAll(ret);
        return buckets;
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#listBuckets(com.obs.services.model.ListBucketsRequest)
     */
    @Override
    public List<ObsBucket> listBuckets(final ListBucketsRequest request) throws ObsException {
        return this.listBucketsV2(request).getBuckets();
    }


    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#listBucketsV2(com.obs.services.model.ListBucketsRequest)
     */
    @Override
    public ListBucketsResult listBucketsV2(final ListBucketsRequest request) throws ObsException {
        return this.doActionWithResult("listBuckets", "All Buckets", new ActionCallbackWithResult<ListBucketsResult>() {
            @Override
            public ListBucketsResult action() throws ServiceException {
                if (isCname()) {
                    throw new ServiceException("listBuckets is not allowed in customdomain mode");
                }
                return ObsClient.this.listAllBucketsImpl(request);
            }

            @Override
            void authTypeNegotiate(String bucketName) throws ServiceException {
                AuthTypeEnum authTypeEnum = ObsClient.this.getApiVersion("");
                ObsClient.this.getProviderCredentials().setThreadLocalAuthType(authTypeEnum);
            }
        });
    }


    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#deleteBucket(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucket(final String bucketName) throws ObsException {
        return this.doActionWithResult("deleteBucket", bucketName, new ActionCallbackWithResult<HeaderResponse>() {

            @Override
            public HeaderResponse action() throws ServiceException {
                return ObsClient.this.deleteBucketImpl(bucketName);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#listObjects(com.obs.services.model.ListObjectsRequest)
     */
    @Override
    public ObjectListing listObjects(final ListObjectsRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "ListObjectsRequest is null");
        return this.doActionWithResult("listObjects", request.getBucketName(),
                new ActionCallbackWithResult<ObjectListing>() {
                    @Override
                    public ObjectListing action() throws ServiceException {
                        return ObsClient.this.listObjectsImpl(request);
                    }

                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#listObjects(java.lang.String)
     */
    @Override
    public ObjectListing listObjects(String bucketName) throws ObsException {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        return this.listObjects(listObjectsRequest);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#headBucket(java.lang.String)
     */
    @Override
    public boolean headBucket(final String bucketName) throws ObsException {
        return this.doActionWithResult("headBucket", bucketName, new ActionCallbackWithResult<Boolean>() {

            @Override
            public Boolean action() throws ServiceException {
                return ObsClient.this.headBucketImpl(bucketName);
            }

            @Override
            void authTypeNegotiate(String bucketName) throws ServiceException {
                try {
                    AuthTypeEnum authTypeEnum = ObsClient.this.getApiVersion(bucketName);
                    ObsClient.this.getProviderCredentials().setThreadLocalAuthType(authTypeEnum);
                } catch (ServiceException e) {
                    if (e.getResponseCode() != 404) {
                        throw e;
                    }
                }
            }
        });
    }

    /**
     * List versioning objects in a bucket.
     *
     * @param bucketName
     *            Bucket name
     * @param prefix
     *            Object name prefix used for listing versioning objects
     * @param delimiter
     *            Character for grouping object names
     * @param keyMarker
     *            Start position for listing versioning objects (sorted by object name)
     * @param versionIdMarker
     *            Start position for listing versioning objects (sorted by version ID)
     * @param maxKeys
     *            Maximum number of versioning objects to be listed
     * @param nextVersionIdMarker
     *            Deprecated field
     * @return Response to the request for listing versioning objects in the bucket
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface fails to be called or access to OBS fails
     */
    @Deprecated
    public ListVersionsResult listVersions(final String bucketName, final String prefix, final String delimiter,
                                           final String keyMarker, final String versionIdMarker, final long maxKeys, final String nextVersionIdMarker)
            throws ObsException {
        ListVersionsRequest request = new ListVersionsRequest();
        request.setBucketName(bucketName);
        request.setPrefix(prefix);
        request.setKeyMarker(keyMarker);
        request.setMaxKeys((int) maxKeys);
        request.setVersionIdMarker(versionIdMarker);
        request.setDelimiter(delimiter);
        return this.listVersions(request);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#listVersions(com.obs.services.model.ListVersionsRequest)
     */
    @Override
    public ListVersionsResult listVersions(final ListVersionsRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "ListVersionsRequest is null");
        return this.doActionWithResult("listVersions", request.getBucketName(),
                new ActionCallbackWithResult<ListVersionsResult>() {
                    @Override
                    public ListVersionsResult action() throws ServiceException {
                        return ObsClient.this.listVersionsImpl(request);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#listVersions(java.lang.String)
     */
    @Override
    public ListVersionsResult listVersions(final String bucketName) throws ObsException {
        ListVersionsRequest request = new ListVersionsRequest();
        request.setBucketName(bucketName);
        return this.listVersions(request);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#listVersions(java.lang.String, long)
     */
    @Override
    public ListVersionsResult listVersions(final String bucketName, final long maxKeys) throws ObsException {
        ListVersionsRequest request = new ListVersionsRequest();
        request.setBucketName(bucketName);
        request.setMaxKeys((int) maxKeys);
        return this.listVersions(request);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#listVersions(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, long)
     */
    @Override
    public ListVersionsResult listVersions(final String bucketName, final String prefix, final String delimiter,
                                           final String keyMarker, final String versionIdMarker, final long maxKeys) throws ObsException {
        return this.listVersions(bucketName, prefix, delimiter, keyMarker, versionIdMarker, maxKeys, null);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketMetadata(com.obs.services.model.BucketMetadataInfoRequest)
     */
    @Override
    public BucketMetadataInfoResult getBucketMetadata(final BucketMetadataInfoRequest request)
            throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BucketMetadataInfoRequest is null");
        return this.doActionWithResult("getBucketMetadata", request.getBucketName(),
                new ActionCallbackWithResult<BucketMetadataInfoResult>() {
                    @Override
                    public BucketMetadataInfoResult action() throws ServiceException {
                        return ObsClient.this.getBucketMetadataImpl(request);
                    }
                });
    }


    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketAcl(java.lang.String)
     */
    @Override
    public AccessControlList getBucketAcl(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketAcl", bucketName, new ActionCallbackWithResult<AccessControlList>() {

            @Override
            public AccessControlList action() throws ServiceException {
                return ObsClient.this.getBucketAclImpl(bucketName);
            }

        });
    }

    /**
     * Set a bucket ACL. <br>
     *
     * @param bucketName
     *            Bucket name
     * @param cannedACL
     *            Pre-defined access control policy
     * @param acl
     *            ACL ("acl" and "cannedACL" cannot be used together.)
     * @return Common response headers
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface fails to be called or access to OBS fails
     */
    @Deprecated
    public HeaderResponse setBucketAcl(final String bucketName, final String cannedACL, final AccessControlList acl)
            throws ObsException {
        return this.doActionWithResult("setBucketAcl", bucketName, new ActionCallbackWithResult<HeaderResponse>() {

            @Override
            public HeaderResponse action() throws ServiceException {
                if (acl == null && null == cannedACL) {
                    throw new IllegalArgumentException("Both CannedACL and AccessControlList is null");
                }
                return ObsClient.this.setBucketAclImpl(bucketName, cannedACL, acl);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketAcl(java.lang.String, com.obs.services.model.AccessControlList)
     */
    @Override
    public HeaderResponse setBucketAcl(final String bucketName, final AccessControlList acl) throws ObsException {
        return this.setBucketAcl(bucketName, null, acl);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketLocation(java.lang.String)
     */
    @Override
    public String getBucketLocation(final String bucketName) throws ObsException {
        return this.getBucketLocationV2(bucketName).getLocation();
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketLocationV2(java.lang.String)
     */
    @Override
    public BucketLocationResponse getBucketLocationV2(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketLocation", bucketName,
                new ActionCallbackWithResult<BucketLocationResponse>() {
                    @Override
                    public BucketLocationResponse action() throws ServiceException {
                        return ObsClient.this.getBucketLocationImpl(bucketName);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketStorageInfo(java.lang.String)
     */
    @Override
    public BucketStorageInfo getBucketStorageInfo(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketStorageInfo", bucketName,
                new ActionCallbackWithResult<BucketStorageInfo>() {

                    @Override
                    public BucketStorageInfo action() throws ServiceException {
                        return ObsClient.this.getBucketStorageInfoImpl(bucketName);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketQuota(java.lang.String)
     */
    @Override
    public BucketQuota getBucketQuota(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketQuota", bucketName, new ActionCallbackWithResult<BucketQuota>() {

            @Override
            public BucketQuota action() throws ServiceException {
                return ObsClient.this.getBucketQuotaImpl(bucketName);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketQuota(java.lang.String, com.obs.services.model.BucketQuota)
     */
    @Override
    public HeaderResponse setBucketQuota(final String bucketName, final BucketQuota bucketQuota) throws ObsException {
        return this.doActionWithResult("setBucketQuota", bucketName, new ActionCallbackWithResult<HeaderResponse>() {

            @Override
            public HeaderResponse action() throws ServiceException {
                ServiceUtils.asserParameterNotNull(bucketQuota,
                        "The bucket '" + bucketName + "' does not include Quota information");
                return ObsClient.this.setBucketQuotaImpl(bucketName, bucketQuota);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketStoragePolicy(java.lang.String)
     */
    @Override
    public BucketStoragePolicyConfiguration getBucketStoragePolicy(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketStoragePolicy", bucketName,
                new ActionCallbackWithResult<BucketStoragePolicyConfiguration>() {

                    @Override
                    public BucketStoragePolicyConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketStoragePolicyImpl(bucketName);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketStoragePolicy(java.lang.String, com.obs.services.model.BucketStoragePolicyConfiguration)
     */
    @Override
    public HeaderResponse setBucketStoragePolicy(final String bucketName,
                                                 final BucketStoragePolicyConfiguration bucketStorage) throws ObsException {
        return this.doActionWithResult("setBucketStoragePolicy", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull(bucketStorage,
                                "The bucket '" + bucketName + "' does not include storagePolicy information");
                        return ObsClient.this.setBucketStorageImpl(bucketName, bucketStorage);
                    }
                });
    }


    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketCors(java.lang.String, com.obs.services.model.BucketCors)
     */
    @Override
    public HeaderResponse setBucketCors(final String bucketName, final BucketCors bucketCors) throws ObsException {
        return this.doActionWithResult("setBucketCors", bucketName, new ActionCallbackWithResult<HeaderResponse>() {

            @Override
            public HeaderResponse action() throws ServiceException {
                ServiceUtils.asserParameterNotNull(bucketCors, "BucketCors is null");
                return ObsClient.this.setBucketCorsImpl(bucketName, bucketCors);
            }
        });
    }

    @Deprecated
    public HeaderResponse setBucketCors(final String bucketName, final S3BucketCors s3BucketCors) throws ObsException {
        ServiceUtils.asserParameterNotNull(s3BucketCors,
                "The bucket '" + bucketName + "' does not include Cors information");
        BucketCors bucketCors = new BucketCors();
        bucketCors.setRules(s3BucketCors.getRules());
        return this.setBucketCors(bucketName, bucketCors);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketCors(java.lang.String)
     */
    @Override
    public BucketCors getBucketCors(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketCors", bucketName, new ActionCallbackWithResult<BucketCors>() {

            @Override
            public BucketCors action() throws ServiceException {
                return ObsClient.this.getBucketCorsImpl(bucketName);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#deleteBucketCors(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketCors(final String bucketName) throws ObsException {
        return this.doActionWithResult("deleteBucketCors", bucketName, new ActionCallbackWithResult<HeaderResponse>() {

            @Override
            public HeaderResponse action() throws ServiceException {
                return ObsClient.this.deleteBucketCorsImpl(bucketName);
            }
        });
    }

    /**
     * Pre-request a bucket.
     *
     * @param bucketName
     *            Bucket name
     * @param optionInfo
     *            Parameters in a bucket preflight request
     * @return Response to the bucket preflight request
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface fails to be called or access to OBS fails
     */
    @Deprecated
    public OptionsInfoResult optionsBucket(final String bucketName, final OptionsInfoRequest optionInfo)
            throws ObsException {
        return this.doActionWithResult("optionsBucket", bucketName, new ActionCallbackWithResult<OptionsInfoResult>() {

            @Override
            public OptionsInfoResult action() throws ServiceException {
                ServiceUtils.asserParameterNotNull(optionInfo, "OptionsInfoRequest is null");
                return ObsClient.this.optionsImpl(bucketName, null, optionInfo);
            }
        });
    }

    /**
     * Perform a preflight on a bucket.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param optionInfo
     *            Parameters in an object preflight request
     * @return Response to the object preflight request
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface fails to be called or access to OBS fails
     */
    @Deprecated
    public OptionsInfoResult optionsObject(final String bucketName, final String objectKey,
                                           final OptionsInfoRequest optionInfo) throws ObsException {
        return this.doActionWithResult("optionsObject", bucketName, new ActionCallbackWithResult<OptionsInfoResult>() {

            @Override
            public OptionsInfoResult action() throws ServiceException {
                ServiceUtils.asserParameterNotNull(optionInfo, "OptionsInfoRequest is null");
                return ObsClient.this.optionsImpl(bucketName, objectKey, optionInfo);
            }
        });
    }

    /**
     * Obtain the logging settings of a bucket.
     *
     * @param bucketName
     *            Bucket name
     * @return Logging settings of the bucket
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface fails to be called or access to OBS fails
     */
    @Deprecated
    public BucketLoggingConfiguration getBucketLoggingConfiguration(final String bucketName) throws ObsException {
        return this.getBucketLogging(bucketName);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketLogging(java.lang.String)
     */
    @Override
    public BucketLoggingConfiguration getBucketLogging(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketLoggingConfiguration", bucketName,
                new ActionCallbackWithResult<BucketLoggingConfiguration>() {
                    @Override
                    public BucketLoggingConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketLoggingConfigurationImpl(bucketName);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketLoggingConfiguration(java.lang.String, com.obs.services.model.BucketLoggingConfiguration, boolean)
     */
    @Override
    public HeaderResponse setBucketLoggingConfiguration(final String bucketName,
                                                        final BucketLoggingConfiguration loggingConfiguration, final boolean updateTargetACLifRequired)
            throws ObsException {
        return this.doActionWithResult("setBucketLoggingConfiguration", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketLoggingConfigurationImpl(bucketName,
                                loggingConfiguration == null ? new BucketLoggingConfiguration() : loggingConfiguration,
                                updateTargetACLifRequired);
                    }
                });
    }

    @Deprecated
    public HeaderResponse setBucketLoggingConfiguration(final String bucketName,
                                                        final BucketLoggingConfiguration loggingConfiguration) throws ObsException {
        return this.setBucketLogging(bucketName, loggingConfiguration);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketLogging(java.lang.String, com.obs.services.model.BucketLoggingConfiguration)
     */
    @Override
    public HeaderResponse setBucketLogging(final String bucketName,
                                           final BucketLoggingConfiguration loggingConfiguration) throws ObsException {
        return this.setBucketLoggingConfiguration(bucketName, loggingConfiguration, false);
    }

    @Deprecated
    public HeaderResponse setBucketVersioning(String bucketName, String status) throws ObsException {
        return this.setBucketVersioning(bucketName, new BucketVersioningConfiguration(status));
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketVersioning(java.lang.String, com.obs.services.model.BucketVersioningConfiguration)
     */
    @Override
    public HeaderResponse setBucketVersioning(final String bucketName,
                                              final BucketVersioningConfiguration versioningConfiguration) throws ObsException {
        return this.doActionWithResult("setBucketVersioning", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull(versioningConfiguration, "BucketVersioningConfiguration is null");
                        return ObsClient.this.setBucketVersioningImpl(bucketName,
                                versioningConfiguration.getVersioningStatus());
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketVersioning(java.lang.String)
     */
    @Override
    public BucketVersioningConfiguration getBucketVersioning(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketVersioning", bucketName,
                new ActionCallbackWithResult<BucketVersioningConfiguration>() {

                    @Override
                    public BucketVersioningConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketVersioningImpl(bucketName);
                    }
                });
    }

    @Deprecated
    public LifecycleConfiguration getBucketLifecycleConfiguration(final String bucketName) throws ObsException {
        return this.getBucketLifecycle(bucketName);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketLifecycle(java.lang.String)
     */
    @Override
    public LifecycleConfiguration getBucketLifecycle(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketLifecycleConfiguration", bucketName,
                new ActionCallbackWithResult<LifecycleConfiguration>() {

                    @Override
                    public LifecycleConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketLifecycleConfigurationImpl(bucketName);
                    }
                });
    }

    @Deprecated
    public HeaderResponse setBucketLifecycleConfiguration(final String bucketName,
                                                          final LifecycleConfiguration lifecycleConfig) throws ObsException {
        return this.setBucketLifecycle(bucketName, lifecycleConfig);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketLifecycle(java.lang.String, com.obs.services.model.LifecycleConfiguration)
     */
    @Override
    public HeaderResponse setBucketLifecycle(final String bucketName,
                                             final LifecycleConfiguration lifecycleConfig) throws ObsException {
        return this.doActionWithResult("setBucketLifecycleConfiguration", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull(lifecycleConfig, "LifecycleConfiguration is null");
                        return ObsClient.this.setBucketLifecycleConfigurationImpl(bucketName, lifecycleConfig);
                    }
                });
    }

    @Deprecated
    public HeaderResponse deleteBucketLifecycleConfiguration(final String bucketName) throws ObsException {
        return this.deleteBucketLifecycle(bucketName);

    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#deleteBucketLifecycle(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketLifecycle(final String bucketName) throws ObsException {
        return this.doActionWithResult("deleteBucketLifecycleConfiguration", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketLifecycleConfigurationImpl(bucketName);
                    }
                });

    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketPolicy(java.lang.String)
     */
    @Override
    public String getBucketPolicy(final String bucketName) throws ObsException {
        return this.getBucketPolicyV2(bucketName).getPolicy();
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketPolicyV2(java.lang.String)
     */
    @Override
    public BucketPolicyResponse getBucketPolicyV2(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketPolicy", bucketName,
                new ActionCallbackWithResult<BucketPolicyResponse>() {
                    @Override
                    public BucketPolicyResponse action() throws ServiceException {
                        return ObsClient.this.getBucketPolicyImpl(bucketName);
                    }

                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketPolicy(java.lang.String, java.lang.String)
     */
    @Override
    public HeaderResponse setBucketPolicy(final String bucketName, final String policy) throws ObsException {
        return this.doActionWithResult("setBucketPolicy", bucketName, new ActionCallbackWithResult<HeaderResponse>() {

            @Override
            public HeaderResponse action() throws ServiceException {
                ServiceUtils.asserParameterNotNull(policy, "policy is null");
                return ObsClient.this.setBucketPolicyImpl(bucketName, policy);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#deleteBucketPolicy(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketPolicy(final String bucketName) throws ObsException {
        return this.doActionWithResult("deleteBucketPolicy", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketPolicyImpl(bucketName);
                    }
                });
    }

    @Deprecated
    public WebsiteConfiguration getBucketWebsiteConfiguration(final String bucketName) throws ObsException {
        return this.getBucketWebsite(bucketName);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketWebsite(java.lang.String)
     */
    @Override
    public WebsiteConfiguration getBucketWebsite(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketWebsiteConfiguration", bucketName,
                new ActionCallbackWithResult<WebsiteConfiguration>() {

                    @Override
                    public WebsiteConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketWebsiteConfigurationImpl(bucketName);
                    }
                });
    }

    @Deprecated
    public HeaderResponse setBucketWebsiteConfiguration(final String bucketName,
                                                        final WebsiteConfiguration websiteConfig) throws ObsException {
        return this.setBucketWebsite(bucketName, websiteConfig);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketWebsite(java.lang.String, com.obs.services.model.WebsiteConfiguration)
     */
    @Override
    public HeaderResponse setBucketWebsite(final String bucketName,
                                           final WebsiteConfiguration websiteConfig) throws ObsException {
        return this.doActionWithResult("setBucketWebsiteConfiguration", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull(websiteConfig, "WebsiteConfiguration is null");
                        return ObsClient.this.setBucketWebsiteConfigurationImpl(bucketName, websiteConfig);
                    }
                });
    }

    @Deprecated
    public HeaderResponse deleteBucketWebsiteConfiguration(final String bucketName) throws ObsException {
        return this.deleteBucketWebsite(bucketName);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#deleteBucketWebsite(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketWebsite(final String bucketName) throws ObsException {
        return this.doActionWithResult("deleteBucketWebsiteConfiguration", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketWebsiteConfigurationImpl(bucketName);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketTagging(java.lang.String)
     */
    @Override
    public BucketTagInfo getBucketTagging(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketTagging", bucketName, new ActionCallbackWithResult<BucketTagInfo>() {

            @Override
            public BucketTagInfo action() throws ServiceException {
                return ObsClient.this.getBucketTaggingImpl(bucketName);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketTagging(java.lang.String, com.obs.services.model.BucketTagInfo)
     */
    @Override
    public HeaderResponse setBucketTagging(final String bucketName, final BucketTagInfo bucketTagInfo)
            throws ObsException {
        return this.doActionWithResult("setBucketTagging", bucketName, new ActionCallbackWithResult<HeaderResponse>() {

            @Override
            public HeaderResponse action() throws ServiceException {
                ServiceUtils.asserParameterNotNull(bucketTagInfo, "BucketTagInfo is null");
                return ObsClient.this.setBucketTaggingImpl(bucketName, bucketTagInfo);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#deleteBucketTagging(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketTagging(final String bucketName) throws ObsException {
        return this.doActionWithResult("deleteBucketTagging", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketTaggingImpl(bucketName);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketEncryption(java.lang.String)
     */
    @Override
    public BucketEncryption getBucketEncryption(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketEncryption", bucketName, new ActionCallbackWithResult<BucketEncryption>() {

            @Override
            BucketEncryption action() throws ServiceException {
                return ObsClient.this.getBucketEncryptionImpl(bucketName);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketEncryption(java.lang.String, com.obs.services.model.BucketEncryption)
     */
    @Override
    public HeaderResponse setBucketEncryption(final String bucketName, final BucketEncryption bucketEncryption)
            throws ObsException {
        return this.doActionWithResult("setBucketEncryption", bucketName, new ActionCallbackWithResult<HeaderResponse>() {

            @Override
            HeaderResponse action() throws ServiceException {
                ServiceUtils.asserParameterNotNull(bucketEncryption, "BucketEncryption is null");
                return ObsClient.this.setBucketEncryptionImpl(bucketName, bucketEncryption);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#deleteBucketEncryption(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketEncryption(final String bucketName) throws ObsException {
        return this.doActionWithResult("deleteBucketEncryption", bucketName, new ActionCallbackWithResult<HeaderResponse>() {
            @Override
            HeaderResponse action() throws ServiceException {
                return ObsClient.this.deleteBucketEncryptionImpl(bucketName);
            }
        });
    }

    @Deprecated
    public HeaderResponse setBucketReplicationConfiguration(final String bucketName,
                                                            final ReplicationConfiguration replicationConfiguration) throws ObsException {
        return this.setBucketReplication(bucketName, replicationConfiguration);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketReplication(java.lang.String, com.obs.services.model.ReplicationConfiguration)
     */
    @Override
    public HeaderResponse setBucketReplication(final String bucketName,
                                               final ReplicationConfiguration replicationConfiguration) throws ObsException {
        return this.doActionWithResult("setBucketReplication", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull(replicationConfiguration, "ReplicationConfiguration is null");
                        return ObsClient.this.setBucketReplicationConfigurationImpl(bucketName,
                                replicationConfiguration);
                    }
                });
    }

    @Deprecated
    public ReplicationConfiguration getBucketReplicationConfiguration(final String bucketName) throws ObsException {
        return this.getBucketReplication(bucketName);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketReplication(java.lang.String)
     */
    @Override
    public ReplicationConfiguration getBucketReplication(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketReplicationConfiguration", bucketName,
                new ActionCallbackWithResult<ReplicationConfiguration>() {

                    @Override
                    public ReplicationConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketReplicationConfigurationImpl(bucketName);
                    }
                });
    }

    @Deprecated
    public HeaderResponse deleteBucketReplicationConfiguration(final String bucketName) throws ObsException {
        return this.deleteBucketReplication(bucketName);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#deleteBucketReplication(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketReplication(final String bucketName) throws ObsException {
        return this.doActionWithResult("deleteBucketReplicationConfiguration", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketReplicationConfigurationImpl(bucketName);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getBucketNotification(java.lang.String)
     */
    @Override
    public BucketNotificationConfiguration getBucketNotification(final String bucketName) throws ObsException {
        return this.doActionWithResult("getBucketNotification", bucketName,
                new ActionCallbackWithResult<BucketNotificationConfiguration>() {

                    @Override
                    public BucketNotificationConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketNotificationConfigurationImpl(bucketName);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setBucketNotification(java.lang.String, com.obs.services.model.BucketNotificationConfiguration)
     */
    @Override
    public HeaderResponse setBucketNotification(final String bucketName,
                                                final BucketNotificationConfiguration bucketNotificationConfiguration) throws ObsException {
        return this.doActionWithResult("setBucketNotification", bucketName,
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketNotificationImpl(bucketName,
                                bucketNotificationConfiguration == null ? new BucketNotificationConfiguration()
                                        : bucketNotificationConfiguration);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#putObject(java.lang.String, java.lang.String, java.io.InputStream, com.obs.services.model.ObjectMetadata)
     */
    @Override
    public PutObjectResult putObject(String bucketName, String objectKey, InputStream input, ObjectMetadata metadata)
            throws ObsException {
        PutObjectRequest request = new PutObjectRequest();
        request.setBucketName(bucketName);
        request.setInput(input);
        request.setMetadata(metadata);
        request.setObjectKey(objectKey);
        return this.putObject(request);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#putObject(java.lang.String, java.lang.String, java.io.InputStream)
     */
    @Override
    public PutObjectResult putObject(String bucketName, String objectKey, InputStream input) throws ObsException {
        return this.putObject(bucketName, objectKey, input, null);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#putObject(com.obs.services.model.PutObjectRequest)
     */
    @Override
    public PutObjectResult putObject(final PutObjectRequest request) throws ObsException {

        ServiceUtils.asserParameterNotNull(request, "PutObjectRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");

        return this.doActionWithResult("putObject", request.getBucketName(),
                new ActionCallbackWithResult<PutObjectResult>() {
                    @Override
                    public PutObjectResult action() throws ServiceException {
                        if (null != request.getInput() && null != request.getFile()) {
                            throw new ServiceException("Both input and file are set, only one is allowed");
                        }
                        return ObsClient.this.putObjectImpl(request);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#putObject(java.lang.String, java.lang.String, java.io.File)
     */
    @Override
    public PutObjectResult putObject(String bucketName, String objectKey, File file) throws ObsException {
        return this.putObject(bucketName, objectKey, file, null);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#putObject(java.lang.String, java.lang.String, java.io.File, com.obs.services.model.ObjectMetadata)
     */
    @Override
    public PutObjectResult putObject(String bucketName, String objectKey, File file, ObjectMetadata metadata)
            throws ObsException {
        PutObjectRequest request = new PutObjectRequest();
        request.setBucketName(bucketName);
        request.setFile(file);
        request.setObjectKey(objectKey);
        request.setMetadata(metadata);
        return this.putObject(request);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#appendObject(com.obs.services.model.AppendObjectRequest)
     */
    @Override
    public AppendObjectResult appendObject(final AppendObjectRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "AppendObjectRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");

        return this.doActionWithResult("appendObject", request.getBucketName(),
                new ActionCallbackWithResult<AppendObjectResult>() {
                    @Override
                    public AppendObjectResult action() throws ServiceException {
                        if (null != request.getInput() && null != request.getFile()) {
                            throw new ServiceException("Both input and file are set, only one is allowed");
                        }
                        return ObsClient.this.appendObjectImpl(request);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#uploadFile(com.obs.services.model.UploadFileRequest)
     */
    @Override
    public CompleteMultipartUploadResult uploadFile(UploadFileRequest uploadFileRequest) throws ObsException {
        return new ResumableClient(this).uploadFileResume(uploadFileRequest);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#downloadFile(com.obs.services.model.DownloadFileRequest)
     */
    @Override
    public DownloadFileResult downloadFile(DownloadFileRequest downloadFileRequest) throws ObsException {
        return new ResumableClient(this).downloadFileResume(downloadFileRequest);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getObject(com.obs.services.model.GetObjectRequest)
     */
    @Override
    public ObsObject getObject(final GetObjectRequest request) throws ObsException {

        ServiceUtils.asserParameterNotNull(request, "GetObjectRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        return this.doActionWithResult("getObject", request.getBucketName(), new ActionCallbackWithResult<ObsObject>() {

            @Override
            public ObsObject action() throws ServiceException {
                return ObsClient.this.getObjectImpl(request);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getObject(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ObsObject getObject(final String bucketName, final String objectKey, final String versionId)
            throws ObsException {
        return this.getObject(new GetObjectRequest(bucketName, objectKey, versionId));
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getObject(java.lang.String, java.lang.String)
     */
    @Override
    public ObsObject getObject(final String bucketName, final String objectKey) throws ObsException {
        return this.getObject(bucketName, objectKey, null);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getObjectMetadata(com.obs.services.model.GetObjectMetadataRequest)
     */
    @Override
    public ObjectMetadata getObjectMetadata(final GetObjectMetadataRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "GetObjectMetadataRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        return this.doActionWithResult("getObjectMetadata", request.getBucketName(),
                new ActionCallbackWithResult<ObjectMetadata>() {

                    @Override
                    public ObjectMetadata action() throws ServiceException {
                        return ObsClient.this.getObjectMetadataImpl(request);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setObjectMetadata(com.obs.services.model.SetObjectMetadataRequest)
     */
    @Override
    public ObjectMetadata setObjectMetadata(final SetObjectMetadataRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetObjectMetadataRequest is null");
        return this.doActionWithResult("setObjectMetadata", request.getBucketName(),
                new ActionCallbackWithResult<ObjectMetadata>() {
                    @Override
                    public ObjectMetadata action() throws ServiceException {
                        return ObsClient.this.setObjectMetadataImpl(request);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getObjectMetadata(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String objectKey, String versionId) throws ObsException {
        GetObjectMetadataRequest request = new GetObjectMetadataRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setVersionId(versionId);
        return this.getObjectMetadata(request);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getObjectMetadata(java.lang.String, java.lang.String)
     */
    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String objectKey) throws ObsException {
        return this.getObjectMetadata(bucketName, objectKey, null);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#restoreObject(com.obs.services.model.RestoreObjectRequest)
     */
    @Deprecated
    public RestoreObjectStatus restoreObject(final RestoreObjectRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "RestoreObjectRequest is null");
        return this.doActionWithResult("restoreObject", request.getBucketName(),
                new ActionCallbackWithResult<RestoreObjectStatus>() {

                    @Override
                    public RestoreObjectStatus action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
                        return ObsClient.this.restoreObjectImpl(request);
                    }
                });

    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#restoreObjectV2(com.obs.services.model.RestoreObjectRequest)
     */
    @Override
    public RestoreObjectResult restoreObjectV2(final RestoreObjectRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "RestoreObjectRequest is null");
        return this.doActionWithResult("restoreObjectV2", request.getBucketName(),
                new ActionCallbackWithResult<RestoreObjectResult>() {

                    @Override
                    public RestoreObjectResult action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
                        return ObsClient.this.restoreObjectV2Impl(request);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#restoreObjects(com.obs.services.model.RestoreObjectsRequest)
     */
    @Override
    public TaskProgressStatus restoreObjects(RestoreObjectsRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "RestoreObjectsRequest is null");
        if (!this.isCname()) {
            ServiceUtils.asserParameterNotNull(request.getBucketName(), "bucketName is null");
        }

        if (request.getKeyAndVersions() != null && request.getPrefix() != null) {
            throw new IllegalArgumentException("Prefix and keyandVersions cannot coexist in the same request");
        }

        int days = request.getDays();
        if (!(days >= 1 && days <= 30)) {
            throw new IllegalArgumentException("Restoration days should be at least 1 and at most 30");
        }
        DefaultTaskProgressStatus progreStatus = new DefaultTaskProgressStatus();
        ThreadPoolExecutor executor = this.initThreadPool(request);

        try {
            String bucketName = request.getBucketName();
            String prefix = request.getPrefix();
            RestoreTierEnum tier = request.getRestoreTier();
            boolean versionRestored = request.isVersionRestored();
            TaskCallback<RestoreObjectResult, RestoreObjectRequest> callback;
            TaskProgressListener listener;
            callback = (request.getCallback() == null) ? new LazyTaksCallback<RestoreObjectResult, RestoreObjectRequest>() : request.getCallback();
            listener = request.getProgressListener();
            int progressInterval = request.getProgressInterval();
            int totalTasks = 0;
            if (request.getKeyAndVersions() != null) {
                totalTasks = request.getKeyAndVersions().size();
                for (KeyAndVersion kv : request.getKeyAndVersions()) {
                    RestoreObjectRequest taskRequest = new RestoreObjectRequest(bucketName, kv.getKey(), kv.getVersion(), days, tier);
                    RestoreObjectTask task = new RestoreObjectTask(this, bucketName, taskRequest, callback, listener, progreStatus, progressInterval);
                    executor.execute(task);
                }
            } else {
                if (versionRestored) {
                    ListVersionsResult versionResult;
                    ListVersionsRequest listRequest = new ListVersionsRequest(bucketName);
                    listRequest.setPrefix(prefix);
                    do {
                        versionResult = this.listVersions(listRequest);
                        for (VersionOrDeleteMarker v : versionResult.getVersions()) {
                            if (v.getObjectStorageClass() == StorageClassEnum.COLD) {
                                totalTasks++;
                                RestoreObjectRequest taskRequest = new RestoreObjectRequest(bucketName, v.getKey(), v.getVersionId(), days, tier);
                                RestoreObjectTask task = new RestoreObjectTask(this, bucketName, taskRequest, callback, listener, progreStatus, progressInterval);
                                executor.execute(task);
                                if (ILOG.isInfoEnabled()) {
                                    if (totalTasks % 1000 == 0) {
                                        ILOG.info("RestoreObjects: " + totalTasks + " tasks have submitted to restore objects");
                                    }
                                }
                            }
                        }
                        listRequest.setKeyMarker(versionResult.getNextKeyMarker());
                        listRequest.setVersionIdMarker(versionResult.getNextVersionIdMarker());
                    } while (versionResult.isTruncated());
                } else {
                    ObjectListing objectsResult;
                    ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
                    listObjectsRequest.setPrefix(prefix);
                    do {
                        objectsResult = this.listObjects(listObjectsRequest);
                        for (ObsObject o : objectsResult.getObjects()) {
                            if (o.getMetadata().getObjectStorageClass() == StorageClassEnum.COLD) {
                                totalTasks++;
                                RestoreObjectRequest taskRequest = new RestoreObjectRequest(bucketName, o.getObjectKey(), null, days, tier);
                                RestoreObjectTask task = new RestoreObjectTask(this, bucketName, taskRequest, callback, listener, progreStatus, progressInterval);
                                executor.execute(task);
                                if (ILOG.isInfoEnabled()) {
                                    if (totalTasks % 1000 == 0) {
                                        ILOG.info("RestoreObjects: " + totalTasks + " tasks have submitted to restore objects");
                                    }
                                }
                            }
                        }
                        listObjectsRequest.setMarker(objectsResult.getNextMarker());
                    } while (objectsResult.isTruncated());
                }
            }

            progreStatus.setTotalTaskNum(totalTasks);
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (ObsException e) {
            throw e;
        } catch (Exception e) {
            throw new ObsException(e.getMessage(), e);
        }
        return progreStatus;
    }

    @Override
    public UploadProgressStatus putObjects(final PutObjectsRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "PutObjectsRequest is null");

        ThreadPoolExecutor executor = this.initThreadPool(request);
        Date now = new Date();
        UploadTaskProgressStatus progressStatus = new UploadTaskProgressStatus(request.getTaskProgressInterval(), now);

        try {
            UploadObjectsProgressListener listener = request.getUploadObjectsProgressListener();
            TaskCallback<PutObjectResult, PutObjectBasicRequest> callback = (request.getCallback() == null) ?
                    new LazyTaksCallback<PutObjectResult, PutObjectBasicRequest>() : request.getCallback();
            String prefix = request.getPrefix() == null ? "" : request.getPrefix();

            int totalTasks = 0;
            if (request.getFolderPath() != null) {
                String folderPath = request.getFolderPath();
                File fileRoot = new File(folderPath);

                if (fileRoot.exists()) {
                    if (fileRoot.isDirectory()) {
                        String folderRoot = fileRoot.getName();
                        LinkedList<File> list = new LinkedList<File>();
                        list.add(fileRoot);
                        File[] files = fileRoot.listFiles();
                        File temp_file;
                        while (!list.isEmpty()) {
                            temp_file = list.removeFirst();
                            files = temp_file.listFiles();
                            for (File file : files) {
                                if (file.isDirectory()) {
                                    if (!file.exists()) {
                                        String filePath = file.getCanonicalPath();
                                        String erroInfo = "putObjects: the folder \"" + filePath + "\" dose not a folder";
                                        ILOG.warn(erroInfo);
                                    } else {
                                        list.add(file);
                                    }
                                } else {
				                	// File upload
                                    String filePath = file.getCanonicalPath();
                                    if (!file.exists()) {
                                        ILOG.warn("putObjects: the file \"" + filePath + "\" dose not exist");
                                        continue;
                                    }
                                    totalTasks++;
                                    String objectKey = prefix + folderRoot
                                            + filePath.substring(folderPath.length(), filePath.length()).replace("\\", "/");
                                    uploadObjectTask(request, filePath, objectKey, executor, progressStatus, callback, listener);
                                }
                            }
                        }
                    } else {
                        String erroInfo = "putObjects: the folder \"" + folderPath + "\" dose not a folder";
                        ILOG.warn(erroInfo);
                        throw new ObsException(erroInfo);
                    }

                } else {
                    String erroInfo = "putObjects: the folder \"" + folderPath + "\" dose not exist";
                    ILOG.warn(erroInfo);
                    throw new ObsException(erroInfo);
                }

            } else if (request.getFilePaths() != null) {
                for (String filePath : request.getFilePaths()) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        totalTasks++;
                        String objectKey = prefix + file.getName();
        				// File upload
                        uploadObjectTask(request, filePath, objectKey, executor, progressStatus, callback, listener);
                    } else {
                        ILOG.warn("putObjects: the file \"" + filePath + "\" is not exist");
                    }
                }
            }

            progressStatus.setTotalTaskNum(totalTasks);
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (ObsException e) {
            throw e;
        } catch (Exception e) {
            throw new ObsException(e.getMessage(), e);
        }

        return progressStatus;
    }

    private void uploadObjectTask(final PutObjectsRequest request, final String filePath, final String objectKey
            , final ThreadPoolExecutor executor, final UploadTaskProgressStatus progressStatus
            , final TaskCallback<PutObjectResult, PutObjectBasicRequest> callback, final UploadObjectsProgressListener listener) {
        File fileObject = new File(filePath);
        String bucketName = request.getBucketName();
        int progressInterval = request.getProgressInterval();
        int taskNum = request.getTaskNum();
        long detailProgressInterval = request.getDetailProgressInterval();
        long bigfileThreshold = request.getBigfileThreshold();
        long partSize = request.getPartSize();
        AccessControlList acl = request.getAcl();
        Map<ExtensionObjectPermissionEnum, Set<String>> extensionPermissionMap = request.getExtensionPermissionMap();
        SseCHeader sseCHeader = request.getSseCHeader();
        SseKmsHeader sseKmsHeader = request.getSseKmsHeader();
        String successRedirectLocation = request.getSuccessRedirectLocation();

        if (fileObject.length() > bigfileThreshold) {
            UploadFileRequest taskRequest = new UploadFileRequest(bucketName, objectKey);
            taskRequest.setUploadFile(filePath);
            taskRequest.setPartSize(partSize);
            taskRequest.setTaskNum(taskNum);
            taskRequest.setExtensionPermissionMap(extensionPermissionMap);
            taskRequest.setAcl(acl);
            taskRequest.setSuccessRedirectLocation(successRedirectLocation);
            taskRequest.setSseCHeader(sseCHeader);
            taskRequest.setSseKmsHeader(sseKmsHeader);
            taskRequest.setEnableCheckpoint(true);
            progressStatus.addTotalSize(fileObject.length());
            taskRequest.setProgressListener(new ProgressListener() {

                @Override
                public void progressChanged(ProgressStatus status) {
                    progressStatus.putTaskTable(objectKey, status);
                    if (progressStatus.isRefreshprogress()) {
                        Date dateNow = new Date();
                        long totalMilliseconds = dateNow.getTime() - progressStatus.getStartDate().getTime();
                        progressStatus.setTotalMilliseconds(totalMilliseconds);
                        listener.progressChanged(progressStatus);
                    }
                }
            });
            taskRequest.setProgressInterval(detailProgressInterval);

            ResumableUploadTask task = new ResumableUploadTask(this, bucketName, taskRequest
                    , callback, listener, progressStatus, progressInterval);
            executor.execute(task);
        } else {
            PutObjectRequest taskRequest = new PutObjectRequest(bucketName, objectKey, fileObject);
            taskRequest.setExtensionPermissionMap(extensionPermissionMap);
            taskRequest.setAcl(acl);
            taskRequest.setSuccessRedirectLocation(successRedirectLocation);
            taskRequest.setSseCHeader(sseCHeader);
            taskRequest.setSseKmsHeader(sseKmsHeader);
            progressStatus.addTotalSize(fileObject.length());
            taskRequest.setProgressListener(new ProgressListener() {

                @Override
                public void progressChanged(ProgressStatus status) {
                    progressStatus.putTaskTable(objectKey, status);
                    if (progressStatus.isRefreshprogress()) {
                        Date dateNow = new Date();
                        long totalMilliseconds = dateNow.getTime() - progressStatus.getStartDate().getTime();
                        progressStatus.setTotalMilliseconds(totalMilliseconds);
                        listener.progressChanged(progressStatus);
                    }
                }
            });
            taskRequest.setProgressInterval(detailProgressInterval);
            PutObjectTask task = new PutObjectTask(this, bucketName, taskRequest, callback, listener
                    , progressStatus, progressInterval);
            executor.execute(task);
        }
    }


    /* (non-Javadoc)
     * @see com.obs.services.IFSClient#deleteFolder(com.obs.services.model.fs.DeleteFSFolderRequest)
     */
    @Override
    public TaskProgressStatus dropFolder(DropFolderRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "DropFolderRequest is null");
        if (!this.isCname()) {
            ServiceUtils.asserParameterNotNull(request.getBucketName(), "bucketName is null");
        }
        ThreadPoolExecutor executor = this.initThreadPool(request);
        DefaultTaskProgressStatus progressStatus = new DefaultTaskProgressStatus();
        try {
            String bucketName = request.getBucketName();
            String folderName = request.getFolderName();
            String delimiter = this.getFileSystemDelimiter();
            if (!folderName.endsWith(delimiter)) {
                folderName = folderName + delimiter;
            }
            TaskCallback<DeleteObjectResult, String> callback;
            TaskProgressListener listener;
            callback = (request.getCallback() == null) ? new LazyTaksCallback<DeleteObjectResult, String>() : request.getCallback();
            listener = request.getProgressListener();
            int interval = request.getProgressInterval();
            int[] totalTasks = {0};
            boolean isSubDeleted = recurseFolders(folderName, bucketName, callback, interval, progressStatus, listener, executor, totalTasks);
            Map<String, Future<?>> futures = new HashMap<String, Future<?>>();
            totalTasks[0]++;
            progressStatus.setTotalTaskNum(totalTasks[0]);

            if (isSubDeleted) {
                submitDropTask(folderName, bucketName, callback, interval, progressStatus, listener, executor, futures);
                checkDropFutures(futures, progressStatus, callback, listener, interval);
            } else {
                progressStatus.failTaskIncrement();
                callback.onException(new ObsException("Failed to delete due to child file deletion failed"), folderName);
                recordBulkTaskStatus(progressStatus, callback, listener, interval);
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (ObsException e) {
            throw e;
        } catch (Exception e) {
            throw new ObsException(e.getMessage(), e);
        }
        return progressStatus;
    }

    private boolean recurseFolders(String folders, String bucketName, TaskCallback<DeleteObjectResult, String> callback, int interval,
                                   DefaultTaskProgressStatus progressStatus, TaskProgressListener listener, ThreadPoolExecutor executor, int[] count) {
        ListObjectsRequest request = new ListObjectsRequest(bucketName);
        request.setDelimiter("/");
        request.setPrefix(folders);
        ObjectListing result;
        boolean isDeleted = true;
        do {
            result = this.listObjects(request);
            Map<String, Future<?>> futures = new HashMap<String, Future<?>>();

            for (ObsObject o : result.getObjects()) {
                if (!o.getObjectKey().endsWith("/")) {
                    count[0]++;
                    isDeleted = submitDropTask(o.getObjectKey(), bucketName, callback, interval, progressStatus, listener, executor, futures) && isDeleted;
                    if (ILOG.isInfoEnabled()) {
                        if (count[0] % 1000 == 0) {
                            ILOG.info("DropFolder: " + count + " tasks have submitted to delete objects");
                        }
                    }
                }
            }

            for (String prefix : result.getCommonPrefixes()) {
                boolean isSubDeleted = recurseFolders(prefix, bucketName, callback, interval, progressStatus, listener, executor, count);
                count[0]++;
                if (isSubDeleted) {
                    isDeleted = submitDropTask(prefix, bucketName, callback, interval, progressStatus, listener, executor, futures) && isDeleted;
                } else {
                    progressStatus.failTaskIncrement();
                    callback.onException(new ObsException("Failed to delete due to child file deletion failed"), prefix);
                    recordBulkTaskStatus(progressStatus, callback, listener, interval);
                }
                if (ILOG.isInfoEnabled()) {
                    if (count[0] % 1000 == 0) {
                        ILOG.info("DropFolder: " + count + " tasks have submitted to delete objects");
                    }
                }
            }

            request.setMarker(result.getNextMarker());
            isDeleted = checkDropFutures(futures, progressStatus, callback, listener, interval) && isDeleted;
        } while (result.isTruncated());
        return isDeleted;
    }

    private boolean submitDropTask(String key, String bucketName, TaskCallback<DeleteObjectResult, String> callback, int interval,
                                   DefaultTaskProgressStatus progreStatus, TaskProgressListener listener, ThreadPoolExecutor executor, Map<String, Future<?>> futures) {
        DropFolderTask task = new DropFolderTask(this, bucketName, key, progreStatus, listener, interval, callback);
        try {
            futures.put(key, executor.submit(task));
        } catch (RejectedExecutionException e) {
            progreStatus.failTaskIncrement();
            callback.onException(new ObsException(e.getMessage(), e), key);
            return false;
        }
        return true;
    }

    private boolean checkDropFutures(Map<String, Future<?>> futures, DefaultTaskProgressStatus progressStatus,
                                     TaskCallback<DeleteObjectResult, String> callback, TaskProgressListener listener, int interval) {
        boolean isDeleted = true;
        for (Entry<String, Future<?>> entry : futures.entrySet()) {
            try {
                entry.getValue().get();
            } catch (ExecutionException e) {
                progressStatus.failTaskIncrement();
                if (e.getCause() instanceof ObsException) {
                    callback.onException((ObsException) e.getCause(), entry.getKey());
                } else {
                    callback.onException(new ObsException(e.getMessage(), e), entry.getKey());
                }
                isDeleted = false;
            } catch (InterruptedException e) {
                progressStatus.failTaskIncrement();
                callback.onException(new ObsException(e.getMessage(), e), entry.getKey());
                isDeleted = false;
            }
            recordBulkTaskStatus(progressStatus, callback, listener, interval);
        }
        return isDeleted;
    }


    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#deleteObject(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public DeleteObjectResult deleteObject(final String bucketName, final String objectKey, final String versionId)
            throws ObsException {
        return this.doActionWithResult("deleteObject", bucketName, new ActionCallbackWithResult<DeleteObjectResult>() {

            @Override
            public DeleteObjectResult action() throws ServiceException {
                ServiceUtils.asserParameterNotNull2(objectKey, "objectKey is null");
                return ObsClient.this.deleteObjectImpl(bucketName, objectKey, versionId);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#deleteObject(java.lang.String, java.lang.String)
     */
    @Override
    public DeleteObjectResult deleteObject(final String bucketName, final String objectKey) throws ObsException {
        return this.deleteObject(bucketName, objectKey, null);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#deleteObjects(com.obs.services.model.DeleteObjectsRequest)
     */
    @Override
    public DeleteObjectsResult deleteObjects(final DeleteObjectsRequest deleteObjectsRequest) throws ObsException {
        ServiceUtils.asserParameterNotNull(deleteObjectsRequest, "DeleteObjectsRequest is null");
        return this.doActionWithResult("deleteObjects", deleteObjectsRequest.getBucketName(),
                new ActionCallbackWithResult<DeleteObjectsResult>() {

                    @Override
                    public DeleteObjectsResult action() throws ServiceException {
                        return ObsClient.this.deleteObjectsImpl(deleteObjectsRequest);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getObjectAcl(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public AccessControlList getObjectAcl(final String bucketName, final String objectKey, final String versionId)
            throws ObsException {
        ServiceUtils.asserParameterNotNull2(objectKey, "objectKey is null");
        return this.doActionWithResult("getObjectAcl", bucketName, new ActionCallbackWithResult<AccessControlList>() {
            @Override
            public AccessControlList action() throws ServiceException {
                return ObsClient.this.getObjectAclImpl(bucketName, objectKey, versionId);
            }

        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#getObjectAcl(java.lang.String, java.lang.String)
     */
    @Override
    public AccessControlList getObjectAcl(final String bucketName, final String objectKey) throws ObsException {
        return this.getObjectAcl(bucketName, objectKey, null);
    }

    /**
     * Set an object ACL.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param cannedACL
     *            Pre-defined access control policy
     * @param acl
     *            ACL ("acl" and "cannedACL" cannot be used together.)
     * @param versionId
     *            Object version ID
     * @return Common response headers
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface fails to be called or access to OBS fails
     */
    @Deprecated
    public HeaderResponse setObjectAcl(final String bucketName, final String objectKey, final String cannedACL,
                                       final AccessControlList acl, final String versionId) throws ObsException {

        return this.doActionWithResult("setObjectAcl", bucketName, new ActionCallbackWithResult<HeaderResponse>() {
            @Override
            public HeaderResponse action() throws ServiceException {
                if (acl == null && null == cannedACL) {
                    throw new IllegalArgumentException("Both cannedACL and AccessControlList is null");
                }
                return ObsClient.this.setObjectAclImpl(bucketName, objectKey, cannedACL, acl, versionId);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setObjectAcl(java.lang.String, java.lang.String, com.obs.services.model.AccessControlList, java.lang.String)
     */
    @Override
    public HeaderResponse setObjectAcl(final String bucketName, final String objectKey, final AccessControlList acl,
                                       final String versionId) throws ObsException {
        return this.setObjectAcl(bucketName, objectKey, null, acl, versionId);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#setObjectAcl(java.lang.String, java.lang.String, com.obs.services.model.AccessControlList)
     */
    @Override
    public HeaderResponse setObjectAcl(final String bucketName, final String objectKey, final AccessControlList acl)
            throws ObsException {
        return this.setObjectAcl(bucketName, objectKey, null, acl, null);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#copyObject(com.obs.services.model.CopyObjectRequest)
     */
    @Override
    public CopyObjectResult copyObject(final CopyObjectRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "CopyObjectRequest is null");
        ServiceUtils.asserParameterNotNull(request.getDestinationBucketName(), "destinationBucketName is null");
        ServiceUtils.asserParameterNotNull2(request.getSourceObjectKey(), "sourceObjectKey is null");
        ServiceUtils.asserParameterNotNull2(request.getDestinationObjectKey(), "destinationObjectKey is null");
        return this.doActionWithResult("copyObject", request.getSourceBucketName(),
                new ActionCallbackWithResult<CopyObjectResult>() {
                    @Override
                    public CopyObjectResult action() throws ServiceException {
                        return ObsClient.this.copyObjectImpl(request);
                    }
                });

    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#copyObject(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public CopyObjectResult copyObject(String sourceBucketName, String sourceObjectKey, String destBucketName,
                                       String destObjectKey) throws ObsException {
        return this.copyObject(new CopyObjectRequest(sourceBucketName, sourceObjectKey, destBucketName, destObjectKey));
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#initiateMultipartUpload(com.obs.services.model.InitiateMultipartUploadRequest)
     */
    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(final InitiateMultipartUploadRequest request)
            throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "InitiateMultipartUploadRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        return this.doActionWithResult("initiateMultipartUpload", request.getBucketName(),
                new ActionCallbackWithResult<InitiateMultipartUploadResult>() {
                    @Override
                    public InitiateMultipartUploadResult action() throws ServiceException {
                        return ObsClient.this.initiateMultipartUploadImpl(request);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#abortMultipartUpload(com.obs.services.model.AbortMultipartUploadRequest)
     */
    @Override
    public HeaderResponse abortMultipartUpload(final AbortMultipartUploadRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "AbortMultipartUploadRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        ServiceUtils.asserParameterNotNull(request.getUploadId(), "uploadId is null");
        return this.doActionWithResult("abortMultipartUpload", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.abortMultipartUploadImpl(request.getUploadId(), request.getBucketName(),
                                request.getObjectKey());
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#uploadPart(java.lang.String, java.lang.String, java.lang.String, int, java.io.InputStream)
     */
    @Override
    public UploadPartResult uploadPart(String bucketName, String objectKey, String uploadId, int partNumber,
                                       InputStream input) throws ObsException {
        UploadPartRequest request = new UploadPartRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setUploadId(uploadId);
        request.setPartNumber(partNumber);
        request.setInput(input);
        return this.uploadPart(request);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#uploadPart(java.lang.String, java.lang.String, java.lang.String, int, java.io.File)
     */
    @Override
    public UploadPartResult uploadPart(String bucketName, String objectKey, String uploadId, int partNumber, File file)
            throws ObsException {
        UploadPartRequest request = new UploadPartRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setUploadId(uploadId);
        request.setPartNumber(partNumber);
        request.setFile(file);
        return this.uploadPart(request);
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#uploadPart(com.obs.services.model.UploadPartRequest)
     */
    @Override
    public UploadPartResult uploadPart(final UploadPartRequest request) throws ObsException {

        ServiceUtils.asserParameterNotNull(request, "UploadPartRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        ServiceUtils.asserParameterNotNull(request.getUploadId(), "uploadId is null");
        return this.doActionWithResult("uploadPart", request.getBucketName(),
                new ActionCallbackWithResult<UploadPartResult>() {

                    @Override
                    public UploadPartResult action() throws ServiceException {
                        if (null != request.getInput() && null != request.getFile()) {
                            throw new ServiceException("Both input and file are set, only one is allowed");
                        }
                        return ObsClient.this.uploadPartImpl(request);
                    }
                });

    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#copyPart(com.obs.services.model.CopyPartRequest)
     */
    @Override
    public CopyPartResult copyPart(final CopyPartRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "CopyPartRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getSourceObjectKey(), "sourceObjectKey is null");
        ServiceUtils.asserParameterNotNull(request.getDestinationBucketName(), "destinationBucketName is null");
        ServiceUtils.asserParameterNotNull2(request.getDestinationObjectKey(), "destinationObjectKey is null");
        ServiceUtils.asserParameterNotNull(request.getUploadId(), "uploadId is null");
        return this.doActionWithResult("copyPart", request.getSourceBucketName(),
                new ActionCallbackWithResult<CopyPartResult>() {

                    @Override
                    public CopyPartResult action() throws ServiceException {
                        return ObsClient.this.copyPartImpl(request);
                    }
                });

    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#completeMultipartUpload(com.obs.services.model.CompleteMultipartUploadRequest)
     */
    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(final CompleteMultipartUploadRequest request)
            throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "CompleteMultipartUploadRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        ServiceUtils.asserParameterNotNull(request.getUploadId(), "uploadId is null");
        return this.doActionWithResult("completeMultipartUpload", request.getBucketName(),
                new ActionCallbackWithResult<CompleteMultipartUploadResult>() {
                    @Override
                    public CompleteMultipartUploadResult action() throws ServiceException {
                        return ObsClient.this.completeMultipartUploadImpl(request);
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#listParts(com.obs.services.model.ListPartsRequest)
     */
    @Override
    public ListPartsResult listParts(final ListPartsRequest request) throws ObsException {

        ServiceUtils.asserParameterNotNull(request, "ListPartsRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getKey(), "objectKey is null");
        ServiceUtils.asserParameterNotNull(request.getUploadId(), "uploadId is null");
        return this.doActionWithResult("listParts", request.getBucketName(),
                new ActionCallbackWithResult<ListPartsResult>() {

                    @Override
                    public ListPartsResult action() throws ServiceException {
                        return ObsClient.this.listPartsImpl(request);
                    }
                });

    }

    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#listMultipartUploads(com.obs.services.model.ListMultipartUploadsRequest)
     */
    @Override
    public MultipartUploadListing listMultipartUploads(final ListMultipartUploadsRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "ListMultipartUploadsRequest is null");
        return this.doActionWithResult("listMultipartUploads", request.getBucketName(),
                new ActionCallbackWithResult<MultipartUploadListing>() {

                    @Override
                    public MultipartUploadListing action() throws ServiceException {
                        return ObsClient.this.listMultipartUploadsImpl(request);
                    }
                });

    }

    @Override
    public ObsFSBucket newBucket(NewBucketRequest request) throws ObsException {
        ObsBucket bucket = this.createBucket(request);
        ObsFSBucket fsBucket = new ObsFSBucket(bucket.getBucketName(), bucket.getLocation());
        ReflectUtils.setInnerClient(fsBucket, this);
        return fsBucket;
    }

    @Override
    public ObsFSFile newFile(NewFileRequest request) throws ObsException {
        ObsFSFile obsFile = (ObsFSFile) this.putObject(request);
        ReflectUtils.setInnerClient(obsFile, this);
        return obsFile;
    }

    @Override
    public ObsFSFolder newFolder(NewFolderRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "CreateFolderRequest is null");
        if (request.getObjectKey() != null) {
            String delimiter = this.getFileSystemDelimiter();
            if (!request.getObjectKey().endsWith(delimiter)) {
                request.setObjectKey(request.getObjectKey() + delimiter);
            }
        }
        ObsFSFolder obsFolder = (ObsFSFolder) this.putObject(new PutObjectRequest(request));
        ReflectUtils.setInnerClient(obsFolder, this);
        return obsFolder;
    }

    @Override
    public ObsFSAttribute getAttribute(GetAttributeRequest request) throws ObsException {
        return (ObsFSAttribute) this.getObjectMetadata(request);
    }

    @Override
    public ReadFileResult readFile(ReadFileRequest request) throws ObsException {
        return (ReadFileResult) this.getObject(request);
    }

    @Override
    public ObsFSFile writeFile(final WriteFileRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "WriteFileRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        ObsFSFile obsFile = this.doActionWithResult("writeFile", request.getBucketName(),
                new ActionCallbackWithResult<ObsFSFile>() {
                    @Override
                    public ObsFSFile action() throws ServiceException {
                        if (null != request.getInput() && null != request.getFile()) {
                            throw new ServiceException("Both input and file are set, only one is allowed");
                        }
                        return ObsClient.this.writeFileImpl(request);
                    }
                });
        ReflectUtils.setInnerClient(obsFile, this);
        return obsFile;
    }

    @Override
    public ObsFSFile appendFile(WriteFileRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "WriteFileRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        ObjectMetadata metadata = this.getObjectMetadata(new GetObjectMetadataRequest(request.getBucketName(), request.getObjectKey()));
        if (request.getPosition() >= 0L && request.getPosition() != metadata.getNextPosition()) {
            throw new IllegalArgumentException("Where you proposed append to is not equal to length");
        }
        request.setPosition(metadata.getNextPosition());
        return this.writeFile(request);
    }

    @Override
    public RenameResult renameFile(final RenameRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "RenameRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "ObjectKey is null");
        ServiceUtils.asserParameterNotNull2(request.getNewObjectKey(), "NewObjectKey is null");

        return this.doActionWithResult("rename", request.getBucketName(), new ActionCallbackWithResult<RenameResult>() {
            @Override
            public RenameResult action() throws ServiceException {
                return ObsClient.this.renameObjectImpl(request);
            }
        });
    }

    @Override
    public RenameResult renameFolder(RenameRequest request) throws ObsException {
        if (request != null && request.getObjectKey() != null && request.getNewObjectKey() != null) {
            String delimiter = this.getFileSystemDelimiter();
            if (!request.getObjectKey().endsWith(delimiter)) {
                request.setObjectKey(request.getObjectKey() + delimiter);
            }

            if (!request.getNewObjectKey().endsWith(delimiter)) {
                request.setNewObjectKey(request.getNewObjectKey() + delimiter);
            }
        }
        return this.renameFile(request);
    }

    @Override
    public TruncateFileResult truncateFile(final TruncateFileRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "TruncateFileRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "ObjectKey is null");
        return this.doActionWithResult("truncateFile", request.getBucketName(), new ActionCallbackWithResult<TruncateFileResult>() {

            @Override
            public TruncateFileResult action() throws ServiceException {
                return ObsClient.this.truncateFileImpl(request);
            }
        });
    }

    @Override
    public HeaderResponse setBucketFSStatus(final SetBucketFSStatusRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketFileInterfaceRequest is null");
        return this.doActionWithResult("setBucketFSStatus", request.getBucketName(), new ActionCallbackWithResult<HeaderResponse>() {

            @Override
            public HeaderResponse action() throws ServiceException {
                return ObsClient.this.setBucketFSStatusImpl(request);
            }
        });
    }


    @Override
    public GetBucketFSStatusResult getBucketFSStatus(final GetBucketFSStatusRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "GetBucketFSStatusRequest is null");
        return this.doActionWithResult("getBucketFSStatus", request.getBucketName(), new ActionCallbackWithResult<GetBucketFSStatusResult>() {

            @Override
            public GetBucketFSStatusResult action() throws ServiceException {
                return ObsClient.this.getBucketMetadataImpl(request);
            }
        });
    }

    @Override
    public DropFileResult dropFile(final DropFileRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "DropFileRequest is null");
        return this.doActionWithResult("dropFile", request.getBucketName(), new ActionCallbackWithResult<DropFileResult>() {

            @Override
            public DropFileResult action() throws ServiceException {
                ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
                return (DropFileResult) ObsClient.this.deleteObjectImpl(request.getBucketName(), request.getObjectKey(), request.getVersionId());
            }
        });
    }

    @Override
    public ReadAheadResult readAheadObjects(final ReadAheadRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "request is null");
        return this.doActionWithResult("readAheadObjects", request.getBucketName(), new ActionCallbackWithResult<ReadAheadResult>() {
            @Override
            public ReadAheadResult action() throws ServiceException {
                return ObsClient.this.readAheadObjectsImpl(request);

            }
        });
    }

    @Override
    public ReadAheadResult deleteReadAheadObjects(final String bucketName, final String prefix) throws ObsException {
        ServiceUtils.asserParameterNotNull(bucketName, "bucketName is null");
        ServiceUtils.asserParameterNotNull(prefix, "prefix is null");
        return this.doActionWithResult("deleteReadAheadObjects", bucketName, new ActionCallbackWithResult<ReadAheadResult>() {
            @Override
            public ReadAheadResult action() throws ServiceException {
                return ObsClient.this.DeleteReadAheadObjectsImpl(bucketName, prefix);

            }
        });
    }

    @Override
    public ReadAheadQueryResult queryReadAheadObjectsTask(final String bucketName, final String taskId) throws ObsException {
        ServiceUtils.asserParameterNotNull(bucketName, "bucketName is null");
        ServiceUtils.asserParameterNotNull(taskId, "taskId is null");
        return this.doActionWithResult("queryReadAheadObjectsTask", bucketName, new ActionCallbackWithResult<ReadAheadQueryResult>() {
            @Override
            public ReadAheadQueryResult action() throws ServiceException {
                return ObsClient.this.queryReadAheadObjectsTaskImpl(bucketName, taskId);

            }
        });
    }

    @Override
    public HeaderResponse setBucketDirectColdAccess(final String bucketName, final BucketDirectColdAccess access)
            throws ObsException {
        ServiceUtils.asserParameterNotNull(bucketName, "bucketName is null");
        ServiceUtils.asserParameterNotNull(access, "bucketDirectColdAccess is null");
        return this.doActionWithResult("setBucketDirectColdAccess", bucketName, new ActionCallbackWithResult<HeaderResponse>() {

            @Override
            public HeaderResponse action() throws ServiceException {
                return ObsClient.this.setBucketDirectColdAccessImpl(bucketName, access);
            }
        });
    }

    @Override
    public BucketDirectColdAccess getBucketDirectColdAccess(final String bucketName) throws ObsException {
        ServiceUtils.asserParameterNotNull(bucketName, "bucketName is null");
        return this.doActionWithResult("getBucketDirectColdAccess", bucketName, new ActionCallbackWithResult<BucketDirectColdAccess>() {

            @Override
            public BucketDirectColdAccess action() throws ServiceException {
                return ObsClient.this.getBucketDirectColdAccessImpl(bucketName);
            }
        });
    }

    @Override
    public HeaderResponse deleteBucketDirectColdAccess(final String bucketName) throws ObsException {
        ServiceUtils.asserParameterNotNull(bucketName, "bucketName is null");
        return this.doActionWithResult("deleteBucketDirectColdAccess", bucketName, new ActionCallbackWithResult<HeaderResponse>() {

            @Override
            public HeaderResponse action() throws ServiceException {
                return ObsClient.this.deleteBucketDirectColdAccessImpl(bucketName);
            }
        });
    }

    @Override
    public boolean doesObjectExist(final String buckeName, final String objectKey) throws ObsException {
        GetObjectMetadataRequest request = new GetObjectMetadataRequest(buckeName, objectKey);
        return this.doesObjectExist(request);
    }

    @Override
    public boolean doesObjectExist(final GetObjectMetadataRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request.getBucketName(), "bucke is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        try {
            return ObsClient.this.doesObjectExistImpl(request);
        } catch (ServiceException e) {
            if (ObsClient.this.isAuthTypeNegotiation() && e.getResponseCode() == 400 &&
                    "Unsupported Authorization Type".equals(e.getErrorMessage()) &&
                    ObsClient.this.getProviderCredentials().getAuthType() == AuthTypeEnum.OBS) {
                ObsClient.this.getProviderCredentials().setThreadLocalAuthType(AuthTypeEnum.V2);
                return ObsClient.this.doesObjectExistImpl(request);
            } else {
                throw e;
            }
        }
    }

    private abstract class ActionCallbackWithResult<T> {

        abstract T action() throws ServiceException;

        void authTypeNegotiate(String bucketName) throws ServiceException {
            AuthTypeEnum authTypeEnum = ObsClient.this.getApiVersion(bucketName);
            ObsClient.this.getProviderCredentials().setThreadLocalAuthType(authTypeEnum);
        }
    }

    private <T> T doActionWithResult(String action, String bucketName, ActionCallbackWithResult<T> callback)
            throws ObsException {
        if (!this.isCname()) {
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
            if (ex.getResponseCode() >= 400 && ex.getResponseCode() < 500) {
                if (ILOG.isWarnEnabled()) {
                    reqBean.setRespTime(new Date());
                    reqBean.setResultCode(String.valueOf(e.getResponseCode()));
                    ILOG.warn(reqBean);
                }
            } else if (ILOG.isErrorEnabled()) {
                reqBean.setRespTime(new Date());
                reqBean.setResultCode(String.valueOf(ex.getResponseCode()));
                ILOG.error(reqBean);
            }
            throw ex;
        } finally {
            if (this.isAuthTypeNegotiation()) {
                this.getProviderCredentials().removeThreadLocalAuthType();
            }
            AccessLoggerUtils.printLog();
        }
    }


    /* (non-Javadoc)
     * @see com.obs.services.IObsClient#close()
     */
    @Override
    public void close() throws IOException {
        this.shutdown();
    }

    public String base64Md5(InputStream is, long length, long offset) throws NoSuchAlgorithmException, IOException {
        return ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(is, length, offset));
    }

    public String base64Md5(InputStream is) throws NoSuchAlgorithmException, IOException {
        return ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(is));
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }


}

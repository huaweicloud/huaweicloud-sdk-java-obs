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

package com.obs.services;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.obs.log.ILogger;
import com.obs.log.InterfaceLogBean;
import com.obs.log.LoggerBuilder;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.Constants;
import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.ObsProperties;
import com.obs.services.internal.ObsService;
import com.obs.services.internal.ResumableClient;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.consensus.CacheManager;
import com.obs.services.internal.consensus.SegmentLock;
import com.obs.services.internal.security.ProviderCredentials;
import com.obs.services.internal.task.DefaultTaskProgressStatus;
import com.obs.services.internal.task.DropFolderTask;
import com.obs.services.internal.task.LazyTaksCallback;
import com.obs.services.internal.task.PutObjectTask;
import com.obs.services.internal.task.RestoreObjectTask;
import com.obs.services.internal.task.ResumableUploadTask;
import com.obs.services.internal.task.UploadTaskProgressStatus;
import com.obs.services.internal.utils.AccessLoggerUtils;
import com.obs.services.internal.utils.ReflectUtils;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AbortMultipartUploadRequest;
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
import com.obs.services.model.RequestPaymentEnum;
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
import com.obs.services.model.OptionsInfoResult;
import com.obs.services.model.PutObjectResult;
import com.obs.services.model.DownloadFileResult;
import com.obs.services.model.DownloadFileRequest;
import com.obs.services.model.UploadFileRequest;
import com.obs.services.model.MonitorableProgressListener;
import com.obs.services.model.RestoreObjectsRequest;
import com.obs.services.model.TaskProgressStatus;
import com.obs.services.model.PolicyConditionItem;
import com.obs.services.model.ProgressStatus;
import com.obs.services.model.SseKmsHeader;
import com.obs.services.model.SseCHeader;
import com.obs.services.model.UploadObjectsProgressListener;
import com.obs.services.model.PutObjectBasicRequest;
import com.obs.services.model.UploadProgressStatus;
import com.obs.services.model.RestoreTierEnum;
import com.obs.services.model.KeyAndVersion;
import com.obs.services.model.S3BucketCors;
import com.obs.services.model.S3Bucket;
import com.obs.services.model.HttpMethodEnum;
import com.obs.services.model.V4TemporarySignatureResponse;
import com.obs.services.model.V4TemporarySignatureRequest;
import com.obs.services.model.V4PostSignatureRequest;
import com.obs.services.model.ExtensionObjectPermissionEnum;
import com.obs.services.model.PutObjectsRequest;
import com.obs.services.model.fs.DropFileRequest;
import com.obs.services.model.fs.DropFolderRequest;
import com.obs.services.model.fs.GetAttributeRequest;
import com.obs.services.model.fs.GetBucketFSStatusRequest;
import com.obs.services.model.fs.NewBucketRequest;
import com.obs.services.model.fs.NewFileRequest;
import com.obs.services.model.fs.NewFolderRequest;
import com.obs.services.model.fs.ObsFSBucket;
import com.obs.services.model.fs.ObsFSFolder;
import com.obs.services.model.fs.ReadFileRequest;

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

    public ObsClient(IObsCredentialsProvider provider, String endPoint) {
        ServiceUtils.asserParameterNotNull(provider, "ObsCredentialsProvider is null");
        ObsConfiguration config = new ObsConfiguration();
        config.setEndPoint(endPoint);
        this.init(provider.getSecurityKey().getAccessKey(), provider.getSecurityKey().getSecretKey(),
                provider.getSecurityKey().getSecurityToken(), config);
        this.credentials.setObsCredentialsProvider(provider);
    }

    public ObsClient(IObsCredentialsProvider provider, ObsConfiguration config) {
        ServiceUtils.asserParameterNotNull(provider, "ObsCredentialsProvider is null");
        if (config == null) {
            config = new ObsConfiguration();
        }
        this.init(provider.getSecurityKey().getAccessKey(), provider.getSecurityKey().getSecretKey(),
                provider.getSecurityKey().getSecurityToken(), config);
        this.credentials.setObsCredentialsProvider(provider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#refresh(java.lang.String,
     * java.lang.String, java.lang.String)
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
     *             OBS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to OBS fails
     */
    @Deprecated
    public String createSignedUrl(HttpMethodEnum method, String bucketName, String objectKey,
            SpecialParamEnum specialParam, Date expiryTime, Map<String, String> headers,
            Map<String, Object> queryParams) throws ObsException {
        return this.createSignedUrl(method, bucketName, objectKey, specialParam, expiryTime == null
                ? ObsConstraint.DEFAULT_EXPIRE_SECONEDS : (expiryTime.getTime() - System.currentTimeMillis()) / 1000,
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
     *            Time when the temporary authentication expires. The unit is
     *            second and the default value is 300.
     * @param headers
     *            Header information
     * @param queryParams
     *            Query parameter information
     * @return Temporarily authorized URL
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to OBS fails
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
            V4PostSignatureResponse response = (V4PostSignatureResponse) this.createPostSignatureResponse(request, true);
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#createTemporarySignature(com.obs.services.
     * model.TemporarySignatureRequest)
     */
    @Override
    public TemporarySignatureResponse createTemporarySignature(TemporarySignatureRequest request) {
        ServiceUtils.asserParameterNotNull(request, "TemporarySignatureRequest is null");
        InterfaceLogBean reqBean = new InterfaceLogBean("createTemporarySignature", this.getEndpoint(), "");
        try {
            TemporarySignatureResponse response = this.getProviderCredentials().getAuthType() == AuthTypeEnum.V4
                    ? this.createV4TemporarySignature(request) : this.createTemporarySignatureResponse(request);
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
     * Generate temporary authorization parameters for GET requests based on the
     * object name prefix and validity period.
     * 
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param prefix
     *            Object name prefix
     * @param expiryDate
     *            Expiration date (ISO 8601 UTC)
     * @param headers
     *            Header information
     * @param queryParams
     *            Query parameter information
     * @return Response to the request for temporary access authorization
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to OBS fails
     */
    public TemporarySignatureResponse createGetTemporarySignature(String bucketName, String objectKey, String prefix,
            Date expiryDate, Map<String, String> headers, Map<String, Object> queryParams) {
        try {
            PolicyTempSignatureRequest request = createPolicyGetRequest(bucketName, objectKey, prefix, headers,
                    queryParams);
            request.setExpiryDate(expiryDate);
            TemporarySignatureResponse response = this.createTemporarySignatureResponse(request);
            return response;
        } catch (Exception e) {
            throw new ObsException(e.getMessage(), e);
        }
    }

    /**
     * Generate temporary authorization parameters for GET requests based on the
     * object name prefix and validity period.
     * 
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param prefix
     *            Object name prefix
     * @param expires
     *            Validity period (seconds)
     * @param headers
     *            Header information
     * @param queryParams
     *            Query parameter information
     * @return Response to the request for temporary access authorization
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to OBS fails
     */
    public TemporarySignatureResponse createGetTemporarySignature(String bucketName, String objectKey, String prefix,
            long expires, Map<String, String> headers, Map<String, Object> queryParams) {
        try {
            PolicyTempSignatureRequest request = createPolicyGetRequest(bucketName, objectKey, prefix, headers,
                    queryParams);
            request.setExpires(expires);
            TemporarySignatureResponse response = this.createTemporarySignatureResponse(request);
            return response;
        } catch (Exception e) {
            throw new ObsException(e.getMessage(), e);
        }
    }

    private PolicyTempSignatureRequest createPolicyGetRequest(String bucketName, String objectKey, String prefix,
            Map<String, String> headers, Map<String, Object> queryParams) {
        PolicyTempSignatureRequest request = new PolicyTempSignatureRequest(HttpMethodEnum.GET, bucketName, objectKey);
        List<PolicyConditionItem> conditions = new ArrayList<PolicyConditionItem>();
        PolicyConditionItem keyCondition = new PolicyConditionItem(com.obs.services.model.PolicyConditionItem.ConditionOperator.STARTS_WITH, "key", prefix);
        String bucket = this.isCname() ? this.getEndpoint() : bucketName;
        PolicyConditionItem bucketCondition = new PolicyConditionItem(com.obs.services.model.PolicyConditionItem.ConditionOperator.EQUAL, "bucket", bucket);
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
     *             OBS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to OBS fails
     */
    public PostSignatureResponse createPostSignature(String acl, String contentType, long expires, String bucketName,
            String objectKey) throws ObsException {
        PostSignatureRequest request = new PostSignatureRequest(expires, new Date(), bucketName, objectKey);
        request.getFormParams().put(
                this.getProviderCredentials().getAuthType() == AuthTypeEnum.V4 ? "acl" : this.getIHeaders().aclHeader(),
                acl);
        request.getFormParams().put(com.obs.services.internal.Constants.CommonHeaders.CONTENT_TYPE, contentType);
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
     *             OBS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to OBS fails
     */
    public PostSignatureResponse createPostSignature(long expires, String bucketName, String objectKey)
            throws ObsException {
        PostSignatureRequest request = new PostSignatureRequest(expires, new Date(), bucketName, objectKey);
        return this.createPostSignature(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#createPostSignature(com.obs.services.model.
     * PostSignatureRequest)
     */
    @Override
    public PostSignatureResponse createPostSignature(PostSignatureRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "PostSignatureRequest is null");
        InterfaceLogBean reqBean = new InterfaceLogBean("createPostSignature", this.getEndpoint(), "");
        try {
            PostSignatureResponse response = this.createPostSignatureResponse(request,
                    this.getProviderCredentials().getAuthType() == AuthTypeEnum.V4);
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#createBucket(java.lang.String)
     */
    @Override
    public ObsBucket createBucket(String bucketName) throws ObsException {
        ObsBucket obsBucket = new ObsBucket();
        obsBucket.setBucketName(bucketName);
        return this.createBucket(obsBucket);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#createBucket(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ObsBucket createBucket(String bucketName, String location) throws ObsException {
        ObsBucket obsBucket = new ObsBucket();
        obsBucket.setBucketName(bucketName);
        obsBucket.setLocation(location);
        return this.createBucket(obsBucket);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#createBucket(com.obs.services.model.
     * ObsBucket)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#createBucket(com.obs.services.model.
     * CreateBucketRequest)
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
                            if (ObsClient.this.isAuthTypeNegotiation() && e.getResponseCode() == 400
                                    && "Unsupported Authorization Type".equals(e.getErrorMessage())
                                    && ObsClient.this.getProviderCredentials().getAuthType() == AuthTypeEnum.OBS) {
                                ObsClient.this.getProviderCredentials().setThreadLocalAuthType(AuthTypeEnum.V2);
                                return ObsClient.this.createBucketImpl(request);
                            } else {
                                throw e;
                            }
                        }
                    }

                    @Override
                    void authTypeNegotiate(String bucketName) throws ServiceException {
                        AuthTypeEnum authTypeEnum = ObsClient.this.getApiVersionCache()
                                .getApiVersionInCache(bucketName);
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#listBuckets(com.obs.services.model.
     * ListBucketsRequest)
     */
    @Override
    public List<ObsBucket> listBuckets(final ListBucketsRequest request) throws ObsException {
        return this.listBucketsV2(request).getBuckets();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#listBucketsV2(com.obs.services.model.
     * ListBucketsRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucket(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucket(final String bucketName) throws ObsException {
        return deleteBucket(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucket(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucket(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("deleteBucket", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#listObjects(com.obs.services.model.
     * ListObjectsRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#listObjects(java.lang.String)
     */
    @Override
    public ObjectListing listObjects(String bucketName) throws ObsException {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        return this.listObjects(listObjectsRequest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#headBucket(java.lang.String)
     */
    @Override
    public boolean headBucket(final String bucketName) throws ObsException {
        return headBucket(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#headBucket(java.lang.String)
     */
    @Override
    public boolean headBucket(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("headBucket", request.getBucketName(), new ActionCallbackWithResult<Boolean>() {

            @Override
            public Boolean action() throws ServiceException {
                return ObsClient.this.headBucketImpl(request);
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
     *            Start position for listing versioning objects (sorted by
     *            object name)
     * @param versionIdMarker
     *            Start position for listing versioning objects (sorted by
     *            version ID)
     * @param maxKeys
     *            Maximum number of versioning objects to be listed
     * @param nextVersionIdMarker
     *            Deprecated field
     * @return Response to the request for listing versioning objects in the
     *         bucket
     * @throws ObsException
     *             OBS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to OBS fails
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#listVersions(com.obs.services.model.
     * ListVersionsRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#listVersions(java.lang.String)
     */
    @Override
    public ListVersionsResult listVersions(final String bucketName) throws ObsException {
        ListVersionsRequest request = new ListVersionsRequest();
        request.setBucketName(bucketName);
        return this.listVersions(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#listVersions(java.lang.String, long)
     */
    @Override
    public ListVersionsResult listVersions(final String bucketName, final long maxKeys) throws ObsException {
        ListVersionsRequest request = new ListVersionsRequest();
        request.setBucketName(bucketName);
        request.setMaxKeys((int) maxKeys);
        return this.listVersions(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#listVersions(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * long)
     */
    @Override
    public ListVersionsResult listVersions(final String bucketName, final String prefix, final String delimiter,
            final String keyMarker, final String versionIdMarker, final long maxKeys) throws ObsException {
        return this.listVersions(bucketName, prefix, delimiter, keyMarker, versionIdMarker, maxKeys, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#getBucketMetadata(com.obs.services.model.
     * BucketMetadataInfoRequest)
     */
    @Override
    public BucketMetadataInfoResult getBucketMetadata(final BucketMetadataInfoRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BucketMetadataInfoRequest is null");
        return this.doActionWithResult("getBucketMetadata", request.getBucketName(),
                new ActionCallbackWithResult<BucketMetadataInfoResult>() {
                    @Override
                    public BucketMetadataInfoResult action() throws ServiceException {
                        return ObsClient.this.getBucketMetadataImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketAcl(java.lang.String)
     */
    @Override
    public AccessControlList getBucketAcl(final String bucketName) throws ObsException {
        return getBucketAcl(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketAcl(java.lang.String)
     */
    @Override
    public AccessControlList getBucketAcl(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("getBucketAcl", request.getBucketName(),
                new ActionCallbackWithResult<AccessControlList>() {

                    @Override
                    public AccessControlList action() throws ServiceException {
                        return ObsClient.this.getBucketAclImpl(request);
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
     *             OBS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to OBS fails
     */
    @Deprecated
    public HeaderResponse setBucketAcl(final String bucketName, final String cannedACL, final AccessControlList acl)
            throws ObsException {
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName, acl);
        request.setCannedACL(cannedACL);

        return setBucketAcl(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketAcl(java.lang.String,
     * com.obs.services.model.AccessControlList)
     */
    @Override
    public HeaderResponse setBucketAcl(final String bucketName, final AccessControlList acl) throws ObsException {
        return setBucketAcl(new SetBucketAclRequest(bucketName, acl));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketAcl(java.lang.String,
     * com.obs.services.model.AccessControlList)
     */
    @Override
    public HeaderResponse setBucketAcl(final SetBucketAclRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketAclRequest is null");
        return this.doActionWithResult("setBucketAcl", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        if (request.getAcl() == null && null == request.getCannedACL()) {
                            throw new IllegalArgumentException("Both CannedACL and AccessControlList is null");
                        }
                        return ObsClient.this.setBucketAclImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketLocation(java.lang.String)
     */
    @Override
    public String getBucketLocation(final String bucketName) throws ObsException {
        return this.getBucketLocation(new BaseBucketRequest(bucketName)).getLocation();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketLocationV2(java.lang.String)
     */
    @Override
    public BucketLocationResponse getBucketLocation(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        return this.doActionWithResult("getBucketLocation", request.getBucketName(),
                new ActionCallbackWithResult<BucketLocationResponse>() {
                    @Override
                    public BucketLocationResponse action() throws ServiceException {
                        return ObsClient.this.getBucketLocationImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketLocationV2(java.lang.String)
     */
    @Override
    public BucketLocationResponse getBucketLocationV2(final String bucketName) throws ObsException {
        return this.getBucketLocation(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketStorageInfo(java.lang.String)
     */
    @Override
    public BucketStorageInfo getBucketStorageInfo(final String bucketName) throws ObsException {
        return this.getBucketStorageInfo(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketStorageInfo(java.lang.String)
     */
    @Override
    public BucketStorageInfo getBucketStorageInfo(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("getBucketStorageInfo", request.getBucketName(),
                new ActionCallbackWithResult<BucketStorageInfo>() {

                    @Override
                    public BucketStorageInfo action() throws ServiceException {
                        return ObsClient.this.getBucketStorageInfoImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketQuota(java.lang.String)
     */
    @Override
    public BucketQuota getBucketQuota(final String bucketName) throws ObsException {
        return this.getBucketQuota(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketQuota(java.lang.String)
     */
    @Override
    public BucketQuota getBucketQuota(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        return this.doActionWithResult("getBucketQuota", request.getBucketName(),
                new ActionCallbackWithResult<BucketQuota>() {

                    @Override
                    public BucketQuota action() throws ServiceException {
                        return ObsClient.this.getBucketQuotaImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketQuota(java.lang.String,
     * com.obs.services.model.BucketQuota)
     */
    @Override
    public HeaderResponse setBucketQuota(final String bucketName, final BucketQuota bucketQuota) throws ObsException {
        return this.setBucketQuota(new SetBucketQuotaRequest(bucketName, bucketQuota));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketQuota(java.lang.String,
     * com.obs.services.model.BucketQuota)
     */
    @Override
    public HeaderResponse setBucketQuota(final SetBucketQuotaRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketQuotaRequest is null");
        ServiceUtils.asserParameterNotNull(request.getBucketQuota(),
                "The bucket '" + request.getBucketName() + "' does not include Quota information");
        return this.doActionWithResult("setBucketQuota", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketQuotaImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketStoragePolicy(java.lang.String)
     */
    @Override
    public BucketStoragePolicyConfiguration getBucketStoragePolicy(final String bucketName) throws ObsException {
        return this.getBucketStoragePolicy(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketStoragePolicy(java.lang.String)
     */
    @Override
    public BucketStoragePolicyConfiguration getBucketStoragePolicy(final BaseBucketRequest request)
            throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        return this.doActionWithResult("getBucketStoragePolicy", request.getBucketName(),
                new ActionCallbackWithResult<BucketStoragePolicyConfiguration>() {

                    @Override
                    public BucketStoragePolicyConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketStoragePolicyImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketStoragePolicy(java.lang.String,
     * com.obs.services.model.BucketStoragePolicyConfiguration)
     */
    @Override
    public HeaderResponse setBucketStoragePolicy(final String bucketName,
            final BucketStoragePolicyConfiguration bucketStorage) throws ObsException {
        return this.setBucketStoragePolicy(new SetBucketStoragePolicyRequest(bucketName, bucketStorage));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketStoragePolicy(java.lang.String,
     * com.obs.services.model.BucketStoragePolicyConfiguration)
     */
    @Override
    public HeaderResponse setBucketStoragePolicy(final SetBucketStoragePolicyRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketStoragePolicyRequest is null");
        ServiceUtils.asserParameterNotNull(request.getBucketStorage(),
                "The bucket '" + request.getBucketName() + "' does not include storagePolicy information");

        return this.doActionWithResult("setBucketStoragePolicy", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketStorageImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketCors(java.lang.String,
     * com.obs.services.model.BucketCors)
     */
    @Override
    public HeaderResponse setBucketCors(final String bucketName, final BucketCors bucketCors) throws ObsException {
        return this.setBucketCors(new SetBucketCorsRequest(bucketName, bucketCors));
    }

    @Deprecated
    public HeaderResponse setBucketCors(final String bucketName, final S3BucketCors s3BucketCors) throws ObsException {
        ServiceUtils.asserParameterNotNull(s3BucketCors,
                "The bucket '" + bucketName + "' does not include Cors information");
        BucketCors bucketCors = new BucketCors();
        bucketCors.setRules(s3BucketCors.getRules());
        return this.setBucketCors(new SetBucketCorsRequest(bucketName, bucketCors));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketCors(java.lang.String,
     * com.obs.services.model.BucketCors)
     */
    @Override
    public HeaderResponse setBucketCors(final SetBucketCorsRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketCorsRequest is null");
        return this.doActionWithResult("setBucketCors", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketCorsImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketCors(java.lang.String)
     */
    @Override
    public BucketCors getBucketCors(final String bucketName) throws ObsException {
        return this.getBucketCors(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketCors(java.lang.String)
     */
    @Override
    public BucketCors getBucketCors(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        return this.doActionWithResult("getBucketCors", request.getBucketName(),
                new ActionCallbackWithResult<BucketCors>() {

                    @Override
                    public BucketCors action() throws ServiceException {
                        return ObsClient.this.getBucketCorsImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketCors(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketCors(final String bucketName) throws ObsException {
        return this.deleteBucketCors(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketCors(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketCors(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        return this.doActionWithResult("deleteBucketCors", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketCorsImpl(request);
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
     *             OBS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to OBS fails
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
     *             OBS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to OBS fails
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
     *             OBS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to OBS fails
     */
    @Deprecated
    public BucketLoggingConfiguration getBucketLoggingConfiguration(final String bucketName) throws ObsException {
        return getBucketLogging(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketLogging(java.lang.String)
     */
    @Override
    public BucketLoggingConfiguration getBucketLogging(final String bucketName) throws ObsException {
        return getBucketLogging(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketLogging(java.lang.String)
     */
    @Override
    public BucketLoggingConfiguration getBucketLogging(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        return this.doActionWithResult("getBucketLoggingConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<BucketLoggingConfiguration>() {
                    @Override
                    public BucketLoggingConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketLoggingConfigurationImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketLoggingConfiguration(java.lang.
     * String, com.obs.services.model.BucketLoggingConfiguration, boolean)
     */
    @Override
    public HeaderResponse setBucketLoggingConfiguration(final String bucketName,
            final BucketLoggingConfiguration loggingConfiguration, final boolean updateTargetACLifRequired)
                    throws ObsException {
        return this.setBucketLogging(
                new SetBucketLoggingRequest(bucketName, loggingConfiguration, updateTargetACLifRequired));
    }

    @Deprecated
    public HeaderResponse setBucketLoggingConfiguration(final String bucketName,
            final BucketLoggingConfiguration loggingConfiguration) throws ObsException {
        return this.setBucketLogging(new SetBucketLoggingRequest(bucketName, loggingConfiguration));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketLogging(java.lang.String,
     * com.obs.services.model.BucketLoggingConfiguration)
     */
    @Override
    public HeaderResponse setBucketLogging(final String bucketName,
            final BucketLoggingConfiguration loggingConfiguration) throws ObsException {
        return this.setBucketLogging(new SetBucketLoggingRequest(bucketName, loggingConfiguration));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketLogging(java.lang.String,
     * com.obs.services.model.BucketLoggingConfiguration)
     */
    @Override
    public HeaderResponse setBucketLogging(final SetBucketLoggingRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketLoggingRequest is null");
        return this.doActionWithResult("setBucketLoggingConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketLoggingConfigurationImpl(request);
                    }
                });
    }

    @Deprecated
    public HeaderResponse setBucketVersioning(String bucketName, String status) throws ObsException {
        return this.setBucketVersioning(bucketName, new BucketVersioningConfiguration(status));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketVersioning(java.lang.String,
     * com.obs.services.model.BucketVersioningConfiguration)
     */
    @Override
    public HeaderResponse setBucketVersioning(final String bucketName,
            final BucketVersioningConfiguration versioningConfiguration) throws ObsException {
        return setBucketVersioning(
                new SetBucketVersioningRequest(bucketName, versioningConfiguration.getVersioningStatus()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketVersioning(java.lang.String,
     * com.obs.services.model.BucketVersioningConfiguration)
     */
    @Override
    public HeaderResponse setBucketVersioning(final SetBucketVersioningRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketVersioningRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");

        return this.doActionWithResult("setBucketVersioning", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketVersioningImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketVersioning(java.lang.String)
     */
    @Override
    public BucketVersioningConfiguration getBucketVersioning(final String bucketName) throws ObsException {
        return getBucketVersioning(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketVersioning(java.lang.String)
     */
    @Override
    public BucketVersioningConfiguration getBucketVersioning(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("getBucketVersioning", request.getBucketName(),
                new ActionCallbackWithResult<BucketVersioningConfiguration>() {

                    @Override
                    public BucketVersioningConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketVersioningImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketRequestPayment()
     */
    @Override
    public HeaderResponse setBucketRequestPayment(String bucketName, RequestPaymentEnum payer) throws ObsException {
        return setBucketRequestPayment(new SetBucketRequestPaymentRequest(bucketName, payer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#setBucketRequestPayment(java.lang.String,
     * com.obs.services.model.BucketVersioningConfiguration)
     */
    @Override
    public HeaderResponse setBucketRequestPayment(final SetBucketRequestPaymentRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketRequestPaymentRequest is null");
        return this.doActionWithResult("setBucketRequestPayment", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
                        ServiceUtils.asserParameterNotNull(request.getPayer(), "payer is null");
                        return ObsClient.this.setBucketRequestPaymentImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#getBucketRequestPayment(java.lang.String)
     */
    public RequestPaymentConfiguration getBucketRequestPayment(String bucketName) throws ObsException {
        return getBucketRequestPayment(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#getBucketRequestPayment(java.lang.String)
     */
    @Override
    public RequestPaymentConfiguration getBucketRequestPayment(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("getBucketRequestPayment", request.getBucketName(),
                new ActionCallbackWithResult<RequestPaymentConfiguration>() {

                    @Override
                    public RequestPaymentConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketRequestPaymentImpl(request);
                    }
                });
    }

    @Deprecated
    public LifecycleConfiguration getBucketLifecycleConfiguration(final String bucketName) throws ObsException {
        return this.getBucketLifecycle(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketLifecycle(java.lang.String)
     */
    @Override
    public LifecycleConfiguration getBucketLifecycle(final String bucketName) throws ObsException {
        return this.getBucketLifecycle(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketLifecycle(java.lang.String)
     */
    @Override
    public LifecycleConfiguration getBucketLifecycle(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("getBucketLifecycleConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<LifecycleConfiguration>() {

                    @Override
                    public LifecycleConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketLifecycleConfigurationImpl(request);
                    }
                });
    }

    @Deprecated
    public HeaderResponse setBucketLifecycleConfiguration(final String bucketName,
            final LifecycleConfiguration lifecycleConfig) throws ObsException {
        return this.setBucketLifecycle(new SetBucketLifecycleRequest(bucketName, lifecycleConfig));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketLifecycle(java.lang.String,
     * com.obs.services.model.LifecycleConfiguration)
     */
    @Override
    public HeaderResponse setBucketLifecycle(final String bucketName, final LifecycleConfiguration lifecycleConfig)
            throws ObsException {
        return this.setBucketLifecycle(new SetBucketLifecycleRequest(bucketName, lifecycleConfig));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketLifecycle(java.lang.String,
     * com.obs.services.model.LifecycleConfiguration)
     */
    @Override
    public HeaderResponse setBucketLifecycle(final SetBucketLifecycleRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketLifecycleRequest is null");
        ServiceUtils.asserParameterNotNull(request.getLifecycleConfig(), "LifecycleConfiguration is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("setBucketLifecycleConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketLifecycleConfigurationImpl(request);
                    }
                });
    }

    @Deprecated
    public HeaderResponse deleteBucketLifecycleConfiguration(final String bucketName) throws ObsException {
        return this.deleteBucketLifecycle(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketLifecycle(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketLifecycle(final String bucketName) throws ObsException {
        return this.deleteBucketLifecycle(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketLifecycle(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketLifecycle(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("deleteBucketLifecycleConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketLifecycleConfigurationImpl(request);
                    }
                });

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketPolicy(java.lang.String)
     */
    @Override
    public String getBucketPolicy(final String bucketName) throws ObsException {
        return this.getBucketPolicyV2(new BaseBucketRequest(bucketName)).getPolicy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketPolicy(java.lang.String)
     */
    @Override
    public String getBucketPolicy(final BaseBucketRequest request) throws ObsException {
        return this.getBucketPolicyV2(request).getPolicy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketPolicyV2(java.lang.String)
     */
    @Override
    public BucketPolicyResponse getBucketPolicyV2(final String bucketName) throws ObsException {
        return getBucketPolicyV2(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketPolicyV2(java.lang.String)
     */
    @Override
    public BucketPolicyResponse getBucketPolicyV2(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("getBucketPolicy", request.getBucketName(),
                new ActionCallbackWithResult<BucketPolicyResponse>() {
                    @Override
                    public BucketPolicyResponse action() throws ServiceException {
                        return ObsClient.this.getBucketPolicyImpl(request);
                    }

                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketPolicy(java.lang.String,
     * java.lang.String)
     */
    @Override
    public HeaderResponse setBucketPolicy(final String bucketName, final String policy) throws ObsException {
        return setBucketPolicy(new SetBucketPolicyRequest(bucketName, policy));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketPolicy(java.lang.String,
     * java.lang.String)
     */
    @Override
    public HeaderResponse setBucketPolicy(final SetBucketPolicyRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketPolicyRequest is null");
        ServiceUtils.asserParameterNotNull(request.getPolicy(), "policy is null");
        return this.doActionWithResult("setBucketPolicy", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketPolicyImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketPolicy(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketPolicy(final String bucketName) throws ObsException {
        return deleteBucketPolicy(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketPolicy(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketPolicy(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("deleteBucketPolicy", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketPolicyImpl(request);
                    }
                });
    }

    @Deprecated
    public WebsiteConfiguration getBucketWebsiteConfiguration(final String bucketName) throws ObsException {
        return this.getBucketWebsite(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketWebsite(java.lang.String)
     */
    @Override
    public WebsiteConfiguration getBucketWebsite(final String bucketName) throws ObsException {
        return getBucketWebsite(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketWebsite(java.lang.String)
     */
    @Override
    public WebsiteConfiguration getBucketWebsite(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("getBucketWebsiteConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<WebsiteConfiguration>() {

                    @Override
                    public WebsiteConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketWebsiteConfigurationImpl(request);
                    }
                });
    }

    @Deprecated
    public HeaderResponse setBucketWebsiteConfiguration(final String bucketName,
            final WebsiteConfiguration websiteConfig) throws ObsException {
        return setBucketWebsite(new SetBucketWebsiteRequest(bucketName, websiteConfig));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketWebsite(java.lang.String,
     * com.obs.services.model.WebsiteConfiguration)
     */
    @Override
    public HeaderResponse setBucketWebsite(final String bucketName, final WebsiteConfiguration websiteConfig)
            throws ObsException {
        return setBucketWebsite(new SetBucketWebsiteRequest(bucketName, websiteConfig));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketWebsite(java.lang.String,
     * com.obs.services.model.WebsiteConfiguration)
     */
    @Override
    public HeaderResponse setBucketWebsite(final SetBucketWebsiteRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketWebsiteRequest is null");
        ServiceUtils.asserParameterNotNull(request.getWebsiteConfig(), "WebsiteConfiguration is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");

        return this.doActionWithResult("setBucketWebsiteConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {

                        return ObsClient.this.setBucketWebsiteConfigurationImpl(request);
                    }
                });
    }

    @Deprecated
    public HeaderResponse deleteBucketWebsiteConfiguration(final String bucketName) throws ObsException {
        return this.deleteBucketWebsite(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketWebsite(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketWebsite(final String bucketName) throws ObsException {
        return deleteBucketWebsite(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketWebsite(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketWebsite(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("deleteBucketWebsiteConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketWebsiteConfigurationImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketTagging(java.lang.String)
     */
    @Override
    public BucketTagInfo getBucketTagging(final String bucketName) throws ObsException {
        return getBucketTagging(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketTagging(java.lang.String)
     */
    @Override
    public BucketTagInfo getBucketTagging(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("getBucketTagging", request.getBucketName(),
                new ActionCallbackWithResult<BucketTagInfo>() {

                    @Override
                    public BucketTagInfo action() throws ServiceException {
                        return ObsClient.this.getBucketTaggingImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketTagging(java.lang.String,
     * com.obs.services.model.BucketTagInfo)
     */
    @Override
    public HeaderResponse setBucketTagging(final String bucketName, final BucketTagInfo bucketTagInfo)
            throws ObsException {
        return setBucketTagging(new SetBucketTaggingRequest(bucketName, bucketTagInfo));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketTagging(java.lang.String,
     * com.obs.services.model.BucketTagInfo)
     */
    @Override
    public HeaderResponse setBucketTagging(final SetBucketTaggingRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketTaggingRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("setBucketTagging", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {

                        return ObsClient.this.setBucketTaggingImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketTagging(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketTagging(final String bucketName) throws ObsException {
        return deleteBucketTagging(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketTagging(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketTagging(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");

        return this.doActionWithResult("deleteBucketTagging", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketTaggingImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketEncryption(java.lang.String)
     */
    @Override
    public BucketEncryption getBucketEncryption(final String bucketName) throws ObsException {
        return this.getBucketEncryption(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketEncryption(java.lang.String)
     */
    @Override
    public BucketEncryption getBucketEncryption(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        return this.doActionWithResult("getBucketEncryption", request.getBucketName(),
                new ActionCallbackWithResult<BucketEncryption>() {

                    @Override
                    BucketEncryption action() throws ServiceException {
                        return ObsClient.this.getBucketEncryptionImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketEncryption(java.lang.String,
     * com.obs.services.model.BucketEncryption)
     */
    @Override
    public HeaderResponse setBucketEncryption(final String bucketName, final BucketEncryption bucketEncryption)
            throws ObsException {
        return this.setBucketEncryption(new SetBucketEncryptionRequest(bucketName, bucketEncryption));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketEncryption(java.lang.String,
     * com.obs.services.model.BucketEncryption)
     */
    @Override
    public HeaderResponse setBucketEncryption(final SetBucketEncryptionRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketEncryptionRequest is null");
        ServiceUtils.asserParameterNotNull(request.getBucketEncryption(),
                "SetBucketEncryptionRequest.bucketEncryption is null");
        return this.doActionWithResult("setBucketEncryption", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketEncryptionImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketEncryption(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketEncryption(final String bucketName) throws ObsException {
        return this.deleteBucketEncryption(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketEncryption(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketEncryption(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        return this.doActionWithResult("deleteBucketEncryption", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketEncryptionImpl(request);
                    }
                });
    }

    @Deprecated
    public HeaderResponse setBucketReplicationConfiguration(final String bucketName,
            final ReplicationConfiguration replicationConfiguration) throws ObsException {
        return setBucketReplication(new SetBucketReplicationRequest(bucketName, replicationConfiguration));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketReplication(java.lang.String,
     * com.obs.services.model.ReplicationConfiguration)
     */
    @Override
    public HeaderResponse setBucketReplication(final String bucketName,
            final ReplicationConfiguration replicationConfiguration) throws ObsException {
        return setBucketReplication(new SetBucketReplicationRequest(bucketName, replicationConfiguration));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketReplication(java.lang.String,
     * com.obs.services.model.ReplicationConfiguration)
     */
    @Override
    public HeaderResponse setBucketReplication(final SetBucketReplicationRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketReplicationRequest is null");
        ServiceUtils.asserParameterNotNull(request.getReplicationConfiguration(), "ReplicationConfiguration is null");
        ServiceUtils.asserParameterNotNull(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("setBucketReplication", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketReplicationConfigurationImpl(request);
                    }
                });
    }

    @Deprecated
    public ReplicationConfiguration getBucketReplicationConfiguration(final String bucketName) throws ObsException {
        return getBucketReplication(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketReplication(java.lang.String)
     */
    @Override
    public ReplicationConfiguration getBucketReplication(final String bucketName) throws ObsException {
        return getBucketReplication(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketReplication(java.lang.String)
     */
    @Override
    public ReplicationConfiguration getBucketReplication(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("getBucketReplicationConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<ReplicationConfiguration>() {

                    @Override
                    public ReplicationConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketReplicationConfigurationImpl(request);
                    }
                });
    }

    @Deprecated
    public HeaderResponse deleteBucketReplicationConfiguration(final String bucketName) throws ObsException {
        return deleteBucketReplication(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#deleteBucketReplication(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketReplication(final String bucketName) throws ObsException {
        return deleteBucketReplication(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#deleteBucketReplication(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketReplication(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");

        return this.doActionWithResult("deleteBucketReplicationConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketReplicationConfigurationImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketNotification(java.lang.String)
     */
    @Override
    public BucketNotificationConfiguration getBucketNotification(final String bucketName) throws ObsException {
        return this.getBucketNotification(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketNotification(java.lang.String)
     */
    @Override
    public BucketNotificationConfiguration getBucketNotification(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        return this.doActionWithResult("getBucketNotification", request.getBucketName(),
                new ActionCallbackWithResult<BucketNotificationConfiguration>() {

                    @Override
                    public BucketNotificationConfiguration action() throws ServiceException {
                        return ObsClient.this.getBucketNotificationConfigurationImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketNotification(java.lang.String,
     * com.obs.services.model.BucketNotificationConfiguration)
     */
    @Override
    public HeaderResponse setBucketNotification(final String bucketName,
            final BucketNotificationConfiguration bucketNotificationConfiguration) throws ObsException {
        return this
                .setBucketNotification(new SetBucketNotificationRequest(bucketName, bucketNotificationConfiguration));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketNotification(java.lang.String,
     * com.obs.services.model.BucketNotificationConfiguration)
     */
    @Override
    public HeaderResponse setBucketNotification(final SetBucketNotificationRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketNotificationRequest is null");
        return this.doActionWithResult("setBucketNotification", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        if (null == request.getBucketNotificationConfiguration()) {
                            request.setBucketNotificationConfiguration(new BucketNotificationConfiguration());
                        }
                        return ObsClient.this.setBucketNotificationImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#putObject(java.lang.String,
     * java.lang.String, java.io.InputStream,
     * com.obs.services.model.ObjectMetadata)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#putObject(java.lang.String,
     * java.lang.String, java.io.InputStream)
     */
    @Override
    public PutObjectResult putObject(String bucketName, String objectKey, InputStream input) throws ObsException {
        return this.putObject(bucketName, objectKey, input, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#putObject(com.obs.services.model.
     * PutObjectRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#putObject(java.lang.String,
     * java.lang.String, java.io.File)
     */
    @Override
    public PutObjectResult putObject(String bucketName, String objectKey, File file) throws ObsException {
        return this.putObject(bucketName, objectKey, file, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#putObject(java.lang.String,
     * java.lang.String, java.io.File, com.obs.services.model.ObjectMetadata)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#appendObject(com.obs.services.model.
     * AppendObjectRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#uploadFile(com.obs.services.model.
     * UploadFileRequest)
     */
    @Override
    public CompleteMultipartUploadResult uploadFile(UploadFileRequest uploadFileRequest) throws ObsException {
        return new ResumableClient(this).uploadFileResume(uploadFileRequest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#downloadFile(com.obs.services.model.
     * DownloadFileRequest)
     */
    @Override
    public DownloadFileResult downloadFile(DownloadFileRequest downloadFileRequest) throws ObsException {
        try {
            return new ResumableClient(this).downloadFileResume(downloadFileRequest);
        } finally {
            if (null != downloadFileRequest.getProgressListener()
                    && downloadFileRequest.getProgressListener() instanceof MonitorableProgressListener) {
                ((MonitorableProgressListener)downloadFileRequest.getProgressListener()).finishOneTask();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getObject(com.obs.services.model.
     * GetObjectRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getObject(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ObsObject getObject(final String bucketName, final String objectKey, final String versionId)
            throws ObsException {
        return this.getObject(new GetObjectRequest(bucketName, objectKey, versionId));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getObject(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ObsObject getObject(final String bucketName, final String objectKey) throws ObsException {
        return this.getObject(bucketName, objectKey, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#getObjectMetadata(com.obs.services.model.
     * GetObjectMetadataRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#setObjectMetadata(com.obs.services.model.
     * SetObjectMetadataRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getObjectMetadata(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String objectKey, String versionId) throws ObsException {
        GetObjectMetadataRequest request = new GetObjectMetadataRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setVersionId(versionId);
        return this.getObjectMetadata(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getObjectMetadata(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String objectKey) throws ObsException {
        return this.getObjectMetadata(bucketName, objectKey, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#restoreObject(com.obs.services.model.
     * RestoreObjectRequest)
     */
    @Deprecated
    public com.obs.services.model.RestoreObjectRequest.RestoreObjectStatus restoreObject(final RestoreObjectRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "RestoreObjectRequest is null");
        return this.doActionWithResult("restoreObject", request.getBucketName(),
                new ActionCallbackWithResult<com.obs.services.model.RestoreObjectRequest.RestoreObjectStatus>() {

                    @Override
                    public com.obs.services.model.RestoreObjectRequest.RestoreObjectStatus action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
                        return ObsClient.this.restoreObjectImpl(request);
                    }
                });

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#restoreObjectV2(com.obs.services.model.
     * RestoreObjectRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#restoreObjects(com.obs.services.model.
     * RestoreObjectsRequest)
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
            callback = (request.getCallback() == null)
                    ? new LazyTaksCallback<RestoreObjectResult, RestoreObjectRequest>() : request.getCallback();
            listener = request.getProgressListener();
            int progressInterval = request.getProgressInterval();
            int totalTasks = 0;
            if (request.getKeyAndVersions() != null) {
                totalTasks = request.getKeyAndVersions().size();
                for (KeyAndVersion kv : request.getKeyAndVersions()) {
                    RestoreObjectRequest taskRequest = new RestoreObjectRequest(bucketName, kv.getKey(),
                            kv.getVersion(), days, tier);
                    taskRequest.setRequesterPays(request.isRequesterPays());
                    RestoreObjectTask task = new RestoreObjectTask(this, bucketName, taskRequest, callback, listener,
                            progreStatus, progressInterval);
                    executor.execute(task);
                }
            } else {
                if (versionRestored) {
                    ListVersionsResult versionResult;
                    ListVersionsRequest listRequest = new ListVersionsRequest(bucketName);
                    listRequest.setRequesterPays(request.isRequesterPays());

                    listRequest.setPrefix(prefix);
                    do {
                        versionResult = this.listVersions(listRequest);
                        for (VersionOrDeleteMarker v : versionResult.getVersions()) {
                            if (v.getObjectStorageClass() == StorageClassEnum.COLD) {
                                totalTasks++;
                                RestoreObjectRequest taskRequest = new RestoreObjectRequest(bucketName, v.getKey(),
                                        v.getVersionId(), days, tier);
                                taskRequest.setRequesterPays(request.isRequesterPays());

                                RestoreObjectTask task = new RestoreObjectTask(this, bucketName, taskRequest, callback,
                                        listener, progreStatus, progressInterval);
                                executor.execute(task);
                                if (ILOG.isInfoEnabled()) {
                                    if (totalTasks % 1000 == 0) {
                                        ILOG.info("RestoreObjects: " + totalTasks
                                                + " tasks have submitted to restore objects");
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
                    listObjectsRequest.setRequesterPays(request.isRequesterPays());
                    listObjectsRequest.setPrefix(prefix);
                    do {
                        objectsResult = this.listObjects(listObjectsRequest);
                        for (ObsObject o : objectsResult.getObjects()) {
                            if (o.getMetadata().getObjectStorageClass() == StorageClassEnum.COLD) {
                                totalTasks++;
                                RestoreObjectRequest taskRequest = new RestoreObjectRequest(bucketName,
                                        o.getObjectKey(), null, days, tier);
                                taskRequest.setRequesterPays(request.isRequesterPays());

                                RestoreObjectTask task = new RestoreObjectTask(this, bucketName, taskRequest, callback,
                                        listener, progreStatus, progressInterval);
                                executor.execute(task);
                                if (ILOG.isInfoEnabled()) {
                                    if (totalTasks % 1000 == 0) {
                                        ILOG.info("RestoreObjects: " + totalTasks
                                                + " tasks have submitted to restore objects");
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
            TaskCallback<PutObjectResult, PutObjectBasicRequest> callback = (request.getCallback() == null)
                    ? new LazyTaksCallback<PutObjectResult, PutObjectBasicRequest>() : request.getCallback();
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
                        File tempFile;
                        while (!list.isEmpty()) {
                            tempFile = list.removeFirst();
                            if (null == tempFile) {
                                continue;
                            }
                            files = tempFile.listFiles();
                            if (null == files) {
                                continue;
                            }
                            for (File file : files) {
                                if (file.isDirectory()) {
                                    if (!file.exists()) {
                                        String filePath = file.getCanonicalPath();
                                        String erroInfo = "putObjects: the folder \"" + filePath
                                                + "\" dose not a folder";
                                        ILOG.warn(erroInfo);
                                    } else {
                                        list.add(file);
                                    }
                                } else {
                                    // File upload
                                    // FIXME
                                    String filePath = file.getCanonicalPath();
                                    if (!file.exists()) {
                                        ILOG.warn("putObjects: the file \"" + filePath + "\" dose not exist");
                                        continue;
                                    }
                                    totalTasks++;
                                    String objectKey = prefix + folderRoot + filePath
                                            .substring(folderPath.length(), filePath.length()).replace("\\", "/");
                                    uploadObjectTask(request, filePath, objectKey, executor, progressStatus, callback,
                                            listener);
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

    private void uploadObjectTask(final PutObjectsRequest request, final String filePath, final String objectKey,
            final ThreadPoolExecutor executor, final UploadTaskProgressStatus progressStatus,
            final TaskCallback<PutObjectResult, PutObjectBasicRequest> callback,
            final UploadObjectsProgressListener listener) {
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
            taskRequest.setRequesterPays(request.isRequesterPays());

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

            ResumableUploadTask task = new ResumableUploadTask(this, bucketName, taskRequest, callback, listener,
                    progressStatus, progressInterval);
            executor.execute(task);
        } else {
            PutObjectRequest taskRequest = new PutObjectRequest(bucketName, objectKey, fileObject);
            taskRequest.setExtensionPermissionMap(extensionPermissionMap);
            taskRequest.setAcl(acl);
            taskRequest.setSuccessRedirectLocation(successRedirectLocation);
            taskRequest.setSseCHeader(sseCHeader);
            taskRequest.setSseKmsHeader(sseKmsHeader);
            progressStatus.addTotalSize(fileObject.length());
            taskRequest.setRequesterPays(request.isRequesterPays());

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
            PutObjectTask task = new PutObjectTask(this, bucketName, taskRequest, callback, listener, progressStatus,
                    progressInterval);
            executor.execute(task);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IFSClient#deleteFolder(com.obs.services.model.fs.
     * DeleteFSFolderRequest)
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
            String folderName = request.getFolderName();
            String delimiter = this.getFileSystemDelimiter();
            if (!folderName.endsWith(delimiter)) {
                folderName = folderName + delimiter;
            }
            TaskCallback<DeleteObjectResult, String> callback = (request.getCallback() == null) ? new LazyTaksCallback<DeleteObjectResult, String>()
                    : request.getCallback();
            TaskProgressListener listener = request.getProgressListener();
            int interval = request.getProgressInterval();
            int[] totalTasks = { 0 };
            boolean isSubDeleted = recurseFolders(request, folderName, callback, progressStatus, executor, totalTasks);
            Map<String, Future<?>> futures = new HashMap<String, Future<?>>();
            totalTasks[0]++;
            progressStatus.setTotalTaskNum(totalTasks[0]);

            if (isSubDeleted) {
                submitDropTask(request, folderName, callback, progressStatus, executor, futures);
                checkDropFutures(futures, progressStatus, callback, listener, interval);
            } else {
                progressStatus.failTaskIncrement();
                callback.onException(new ObsException("Failed to delete due to child file deletion failed"),
                        folderName);
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

    /* (non-Javadoc)
     * @see com.obs.services.IFSClient#listContentSummary(com.obs.services.model.fs.listContentSummaryRequest)
     */
    @Override
    public ListContentSummaryResult listContentSummary(final ListContentSummaryRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "ListContentSummaryRequest is null");
        return this.doActionWithResult("listcontentsummary", request.getBucketName(),
            new ActionCallbackWithResult<ListContentSummaryResult>() {
                @Override
                public ListContentSummaryResult action() throws ServiceException {
                    return ObsClient.this.listContentSummaryImpl(request);
                }
            });
    }

    private boolean recurseFolders(DropFolderRequest dropRequest, String folders, TaskCallback<DeleteObjectResult, String> callback,
            DefaultTaskProgressStatus progressStatus, ThreadPoolExecutor executor, int[] count) {
        ListObjectsRequest request = new ListObjectsRequest(dropRequest.getBucketName());
        request.setDelimiter("/");
        request.setPrefix(folders);
        request.setRequesterPays(dropRequest.isRequesterPays());
        ObjectListing result;
        boolean isDeleted = true;
        do {
            result = this.listObjects(request);
            Map<String, Future<?>> futures = new HashMap<String, Future<?>>();

            for (ObsObject o : result.getObjects()) {
                if (!o.getObjectKey().endsWith("/")) {
                    count[0]++;
                    isDeleted = submitDropTask(dropRequest, o.getObjectKey(), callback, progressStatus, executor, futures) && isDeleted;
                    if (ILOG.isInfoEnabled()) {
                        if (count[0] % 1000 == 0) {
                            ILOG.info("DropFolder: " + Arrays.toString(count)
                                    + " tasks have submitted to delete objects");
                        }
                    }
                }
            }

            for (String prefix : result.getCommonPrefixes()) {
                boolean isSubDeleted = recurseFolders(dropRequest, prefix, callback, progressStatus, executor, count);
                count[0]++;
                if (isSubDeleted) {
                    isDeleted = submitDropTask(dropRequest, prefix, callback, progressStatus, executor, futures) && isDeleted;
                } else {
                    progressStatus.failTaskIncrement();
                    callback.onException(new ObsException("Failed to delete due to child file deletion failed"),
                            prefix);
                    recordBulkTaskStatus(progressStatus, callback, dropRequest.getProgressListener(), dropRequest.getProgressInterval());
                }
                if (ILOG.isInfoEnabled()) {
                    if (count[0] % 1000 == 0) {
                        ILOG.info("DropFolder: " + Arrays.toString(count) + " tasks have submitted to delete objects");
                    }
                }
            }

            request.setMarker(result.getNextMarker());
            isDeleted = checkDropFutures(futures, progressStatus, callback, dropRequest.getProgressListener(), dropRequest.getProgressInterval()) && isDeleted;
        } while (result.isTruncated());
        return isDeleted;
    }

    private boolean submitDropTask(DropFolderRequest request, String key, TaskCallback<DeleteObjectResult, String> callback,
            DefaultTaskProgressStatus progreStatus,
            ThreadPoolExecutor executor, Map<String, Future<?>> futures) {
        DropFolderTask task = new DropFolderTask(this, request.getBucketName(), key, progreStatus, request.getProgressListener(), request.getProgressInterval(), callback,
                request.isRequesterPays());
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteObject(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public DeleteObjectResult deleteObject(final String bucketName, final String objectKey, final String versionId)
            throws ObsException {
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, objectKey, versionId);
        return this.deleteObject(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteObject(java.lang.String,
     * java.lang.String)
     */
    @Override
    public DeleteObjectResult deleteObject(final String bucketName, final String objectKey) throws ObsException {
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, objectKey);
        return this.deleteObject(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteObject(java.lang.String,
     * java.lang.String)
     */
    @Override
    public DeleteObjectResult deleteObject(final DeleteObjectRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "DeleteObjectRequest is null");
        return this.doActionWithResult("deleteObject", request.getBucketName(),
                new ActionCallbackWithResult<DeleteObjectResult>() {

                    @Override
                    public DeleteObjectResult action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
                        return ObsClient.this.deleteObjectImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteObjects(com.obs.services.model.
     * DeleteObjectsRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getObjectAcl(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public AccessControlList getObjectAcl(final String bucketName, final String objectKey, final String versionId)
            throws ObsException {
        return getObjectAcl(new GetObjectAclRequest(bucketName, objectKey, versionId));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getObjectAcl(java.lang.String,
     * java.lang.String)
     */
    @Override
    public AccessControlList getObjectAcl(final String bucketName, final String objectKey) throws ObsException {
        return getObjectAcl(new GetObjectAclRequest(bucketName, objectKey, null));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getObjectAcl(java.lang.String,
     * java.lang.String)
     */
    @Override
    public AccessControlList getObjectAcl(final GetObjectAclRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "GetObjectAclRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        return this.doActionWithResult("getObjectAcl", request.getBucketName(),
                new ActionCallbackWithResult<AccessControlList>() {
                    @Override
                    public AccessControlList action() throws ServiceException {
                        return ObsClient.this.getObjectAclImpl(request);
                    }

                });
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
     *             OBS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to OBS fails
     */
    @Deprecated
    public HeaderResponse setObjectAcl(final String bucketName, final String objectKey, final String cannedACL,
            final AccessControlList acl, final String versionId) throws ObsException {
        SetObjectAclRequest request = new SetObjectAclRequest(bucketName, objectKey, acl, versionId);
        request.setCannedACL(cannedACL);
        return setObjectAcl(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setObjectAcl(java.lang.String,
     * java.lang.String, com.obs.services.model.AccessControlList,
     * java.lang.String)
     */
    @Override
    public HeaderResponse setObjectAcl(final SetObjectAclRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetObjectAclRequest is null");
        return this.doActionWithResult("setObjectAcl", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        if (request.getAcl() == null && null == request.getCannedACL()) {
                            throw new IllegalArgumentException("Both cannedACL and AccessControlList is null");
                        }
                        return ObsClient.this.setObjectAclImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setObjectAcl(java.lang.String,
     * java.lang.String, com.obs.services.model.AccessControlList,
     * java.lang.String)
     */
    @Override
    public HeaderResponse setObjectAcl(final String bucketName, final String objectKey, final AccessControlList acl,
            final String versionId) throws ObsException {
        return this.setObjectAcl(new SetObjectAclRequest(bucketName, objectKey, acl, versionId));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setObjectAcl(java.lang.String,
     * java.lang.String, com.obs.services.model.AccessControlList)
     */
    @Override
    public HeaderResponse setObjectAcl(final String bucketName, final String objectKey, final AccessControlList acl)
            throws ObsException {
        return this.setObjectAcl(new SetObjectAclRequest(bucketName, objectKey, acl));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#copyObject(com.obs.services.model.
     * CopyObjectRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#copyObject(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public CopyObjectResult copyObject(String sourceBucketName, String sourceObjectKey, String destBucketName,
            String destObjectKey) throws ObsException {
        return this.copyObject(new CopyObjectRequest(sourceBucketName, sourceObjectKey, destBucketName, destObjectKey));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#initiateMultipartUpload(com.obs.services.
     * model.InitiateMultipartUploadRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#abortMultipartUpload(com.obs.services.model.
     * AbortMultipartUploadRequest)
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
                        return ObsClient.this.abortMultipartUploadImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#uploadPart(java.lang.String,
     * java.lang.String, java.lang.String, int, java.io.InputStream)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#uploadPart(java.lang.String,
     * java.lang.String, java.lang.String, int, java.io.File)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#uploadPart(com.obs.services.model.
     * UploadPartRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#copyPart(com.obs.services.model.
     * CopyPartRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#completeMultipartUpload(com.obs.services.
     * model.CompleteMultipartUploadRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#listParts(com.obs.services.model.
     * ListPartsRequest)
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.obs.services.IObsClient#listMultipartUploads(com.obs.services.model.
     * ListMultipartUploadsRequest)
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
    public RenameObjectResult renameObject(final String bucketName, final String objectKey, final String newObjectKey)
            throws ObsException {
        RenameObjectRequest request = new RenameObjectRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setNewObjectKey(newObjectKey);
        return this.renameObject(request);
    }

    @Override
    public RenameObjectResult renameObject(final RenameObjectRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "RenameObjectRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "ObjectKey is null");
        ServiceUtils.asserParameterNotNull2(request.getNewObjectKey(), "NewObjectKey is null");

        return this.doActionWithResult("renameObject", request.getBucketName(),
                new ActionCallbackWithResult<RenameObjectResult>() {
                    @Override
                    public RenameObjectResult action() throws ServiceException {
                        return ObsClient.this.renameObjectImpl(request);
                    }
                });
    }

    @Override
    public TruncateObjectResult truncateObject(final String bucketName, final String objectKey, final long newLength)
            throws ObsException {
        TruncateObjectRequest request = new TruncateObjectRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setNewLength(newLength);
        return this.truncateObject(request);
    }

    @Override
    public TruncateObjectResult truncateObject(final TruncateObjectRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "TruncateObjectRequest is null");
        ServiceUtils.asserParameterNotNull(request.getNewLength(), "NewLength is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "ObjectKey is null");
        return this.doActionWithResult("truncateObject", request.getBucketName(),
                new ActionCallbackWithResult<TruncateObjectResult>() {

                    @Override
                    public TruncateObjectResult action() throws ServiceException {
                        return ObsClient.this.truncateObjectImpl(request);
                    }
                });
    }

    @Override
    public ModifyObjectResult modifyObject(final String bucketName, final String objectKey, final long position,
            File file) throws ObsException {
        ModifyObjectRequest request = new ModifyObjectRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setPosition(position);
        request.setFile(file);
        return this.modifyObject(request);
    }

    @Override
    public ModifyObjectResult modifyObject(final String bucketName, final String objectKey, final long position,
            InputStream input) throws ObsException {
        ModifyObjectRequest request = new ModifyObjectRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setPosition(position);
        request.setInput(input);
        return this.modifyObject(request);
    }

    @Override
    public ModifyObjectResult modifyObject(final ModifyObjectRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "ModifyObjectRequest is null");
        ServiceUtils.asserParameterNotNull(request.getPosition(), "position is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        return this.doActionWithResult("modifyObject", request.getBucketName(),
                new ActionCallbackWithResult<ModifyObjectResult>() {

                    @Override
                    public ModifyObjectResult action() throws ServiceException {
                        return ObsClient.this.modifyObjectImpl(request);
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
        ObjectMetadata metadata = this
                .getObjectMetadata(new GetObjectMetadataRequest(request.getBucketName(), request.getObjectKey()));
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
                return ObsClient.this.renameFileImpl(request);
            }
        });
    }

    @Override
    public RenameResult renameFolder(RenameRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "RenameRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "ObjectKey is null");
        ServiceUtils.asserParameterNotNull2(request.getNewObjectKey(), "NewObjectKey is null");
        
        String delimiter = this.getFileSystemDelimiter();
        if (!request.getObjectKey().endsWith(delimiter)) {
            request.setObjectKey(request.getObjectKey() + delimiter);
        }

        if (!request.getNewObjectKey().endsWith(delimiter)) {
            request.setNewObjectKey(request.getNewObjectKey() + delimiter);
        }
        
        return this.renameFile(request);
    }

    @Override
    public TruncateFileResult truncateFile(final TruncateFileRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "TruncateFileRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "ObjectKey is null");
        return this.doActionWithResult("truncateFile", request.getBucketName(),
                new ActionCallbackWithResult<TruncateFileResult>() {

                    @Override
                    public TruncateFileResult action() throws ServiceException {
                        return ObsClient.this.truncateFileImpl(request);
                    }
                });
    }

    @Override
    public HeaderResponse setBucketFSStatus(final SetBucketFSStatusRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketFileInterfaceRequest is null");
        return this.doActionWithResult("setBucketFSStatus", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketFSStatusImpl(request);
                    }
                });
    }

    @Override
    public GetBucketFSStatusResult getBucketFSStatus(final GetBucketFSStatusRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "GetBucketFSStatusRequest is null");
        return this.doActionWithResult("getBucketFSStatus", request.getBucketName(),
                new ActionCallbackWithResult<GetBucketFSStatusResult>() {

                    @Override
                    public GetBucketFSStatusResult action() throws ServiceException {
                        return ObsClient.this.getBucketMetadataImpl(request);
                    }
                });
    }

    @Override
    public DropFileResult dropFile(final DropFileRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "DropFileRequest is null");
        return this.doActionWithResult("dropFile", request.getBucketName(),
                new ActionCallbackWithResult<DropFileResult>() {

                    @Override
                    public DropFileResult action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
                        return (DropFileResult) ObsClient.this.deleteObjectImpl(new DeleteObjectRequest(
                                request.getBucketName(), request.getObjectKey(), request.getVersionId()));
                    }
                });
    }

    @Override
    public ReadAheadResult readAheadObjects(final ReadAheadRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "request is null");
        return this.doActionWithResult("readAheadObjects", request.getBucketName(),
                new ActionCallbackWithResult<ReadAheadResult>() {
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
        return this.doActionWithResult("deleteReadAheadObjects", bucketName,
                new ActionCallbackWithResult<ReadAheadResult>() {
                    @Override
                    public ReadAheadResult action() throws ServiceException {
                        return ObsClient.this.deleteReadAheadObjectsImpl(bucketName, prefix);

                    }
                });
    }

    @Override
    public ReadAheadQueryResult queryReadAheadObjectsTask(final String bucketName, final String taskId)
            throws ObsException {
        ServiceUtils.asserParameterNotNull(bucketName, "bucketName is null");
        ServiceUtils.asserParameterNotNull(taskId, "taskId is null");
        return this.doActionWithResult("queryReadAheadObjectsTask", bucketName,
                new ActionCallbackWithResult<ReadAheadQueryResult>() {
                    @Override
                    public ReadAheadQueryResult action() throws ServiceException {
                        return ObsClient.this.queryReadAheadObjectsTaskImpl(bucketName, taskId);

                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketDirectColdAccess()
     */
    @Override
    public HeaderResponse setBucketDirectColdAccess(final String bucketName, final BucketDirectColdAccess access)
            throws ObsException {
        return this.setBucketDirectColdAccess(new SetBucketDirectColdAccessRequest(bucketName, access));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#setBucketDirectColdAccess()
     */
    @Override
    public HeaderResponse setBucketDirectColdAccess(final SetBucketDirectColdAccessRequest request)
            throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketDirectColdAccessRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        ServiceUtils.asserParameterNotNull(request.getAccess(), "bucketDirectColdAccess is null");

        return this.doActionWithResult("setBucketDirectColdAccess", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.setBucketDirectColdAccessImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketDirectColdAccess()
     */
    @Override
    public BucketDirectColdAccess getBucketDirectColdAccess(final String bucketName) throws ObsException {
        return this.getBucketDirectColdAccess(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#getBucketDirectColdAccess()
     */
    @Override
    public BucketDirectColdAccess getBucketDirectColdAccess(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("getBucketDirectColdAccess", request.getBucketName(),
                new ActionCallbackWithResult<BucketDirectColdAccess>() {
                    @Override
                    public BucketDirectColdAccess action() throws ServiceException {
                        return ObsClient.this.getBucketDirectColdAccessImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketDirectColdAccess()
     */
    @Override
    public HeaderResponse deleteBucketDirectColdAccess(final String bucketName) throws ObsException {
        return this.deleteBucketDirectColdAccess(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.obs.services.IObsClient#deleteBucketDirectColdAccess()
     */
    @Override
    public HeaderResponse deleteBucketDirectColdAccess(final BaseBucketRequest request) throws ObsException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("deleteBucketDirectColdAccess", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return ObsClient.this.deleteBucketDirectColdAccessImpl(request);
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
            if (ObsClient.this.isAuthTypeNegotiation() && e.getResponseCode() == 400
                    && "Unsupported Authorization Type".equals(e.getErrorMessage())
                    && ObsClient.this.getProviderCredentials().getAuthType() == AuthTypeEnum.OBS) {
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

    /*
     * (non-Javadoc)
     * 
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
}

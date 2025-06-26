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

import java.util.List;

import com.obs.services.exception.ObsException;
import com.obs.services.internal.security.ProviderCredentialThreadContext;
import com.obs.services.internal.security.ProviderCredentials;
import com.obs.services.model.AbortMultipartUploadRequest;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.BucketCors;
import com.obs.services.model.BucketLocationResponse;
import com.obs.services.model.BucketLoggingConfiguration;
import com.obs.services.model.BucketMetadataInfoRequest;
import com.obs.services.model.BucketMetadataInfoResult;
import com.obs.services.model.BucketNotificationConfiguration;
import com.obs.services.model.BucketPolicyResponse;
import com.obs.services.model.BucketQuota;
import com.obs.services.model.BucketStorageInfo;
import com.obs.services.model.BucketStoragePolicyConfiguration;
import com.obs.services.model.BucketTagInfo;
import com.obs.services.model.BucketVersioningConfiguration;
import com.obs.services.model.CompleteMultipartUploadRequest;
import com.obs.services.model.CompleteMultipartUploadResult;
import com.obs.services.model.CopyObjectRequest;
import com.obs.services.model.CopyObjectResult;
import com.obs.services.model.CopyPartRequest;
import com.obs.services.model.CopyPartResult;
import com.obs.services.model.DeleteObjectsRequest;
import com.obs.services.model.DeleteObjectsResult;
import com.obs.services.model.GetObjectMetadataRequest;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.InitiateMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.LifecycleConfiguration;
import com.obs.services.model.ListBucketsRequest;
import com.obs.services.model.ListMultipartUploadsRequest;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ListPartsRequest;
import com.obs.services.model.ListPartsResult;
import com.obs.services.model.ListVersionsRequest;
import com.obs.services.model.ListVersionsResult;
import com.obs.services.model.MultipartUploadListing;
import com.obs.services.model.ObjectListing;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObsBucket;
import com.obs.services.model.ObsObject;
import com.obs.services.model.OptionsInfoRequest;
import com.obs.services.model.OptionsInfoResult;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import com.obs.services.model.ReplicationConfiguration;
import com.obs.services.model.RestoreObjectRequest;
import com.obs.services.model.RestoreObjectRequest.RestoreObjectStatus;
import com.obs.services.model.UploadPartRequest;
import com.obs.services.model.UploadPartResult;
import com.obs.services.model.WebsiteConfiguration;

/**
 * ObsClient that supports transparent transfer of AK/SK, inherited from
 * {@link com.obs.services.ObsClient}
 *
 */
public class SecretFlexibleObsClient extends ObsClient {

    /**
     * Constructor
     * 
     * @param config
     *            Configuration parameters of ObsClient
     */
    public SecretFlexibleObsClient(ObsConfiguration config) {
        this("", "", config);
    }

    /**
     * Constructor
     * 
     * @param endPoint
     *            OBS endpoint
     */
    public SecretFlexibleObsClient(String endPoint) {
        this("", "", endPoint);
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
     */
    public SecretFlexibleObsClient(String accessKey, String secretKey, ObsConfiguration config) {
        super(accessKey, secretKey, config);
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
     */
    public SecretFlexibleObsClient(String accessKey, String secretKey, String endPoint) {
        super(accessKey, secretKey, endPoint);
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
     */
    public SecretFlexibleObsClient(String accessKey, String secretKey, String securityToken, ObsConfiguration config) {
        super(accessKey, secretKey, securityToken, config);
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
     */
    public SecretFlexibleObsClient(String accessKey, String secretKey, String securityToken, String endPoint) {
        super(accessKey, secretKey, securityToken, endPoint);
    }

    private void setContextProviderCredentials(String accessKey, String secretKey, String securityToken) {
        ProviderCredentials providerCredentials = new ProviderCredentials(accessKey, secretKey, securityToken);
        providerCredentials.setAuthType(this.getProviderCredentials().getAuthType());
        ProviderCredentialThreadContext.getInstance().setProviderCredentials(providerCredentials);
    }

    private void setContextProviderCredentials(String accessKey, String secretKey) {
        this.setContextProviderCredentials(accessKey, secretKey, null);
    }

    private void clearContextProviderCredentials() {
        ProviderCredentialThreadContext.getInstance().clearProviderCredentials();
    }

    public ObsBucket createBucket(ObsBucket bucket, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.createBucket(bucket);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ObsBucket createBucket(ObsBucket bucket, String accessKey, String secretKey, String securityToken)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.createBucket(bucket);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public List<ObsBucket> listBuckets(String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.listBuckets(null);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public List<ObsBucket> listBuckets(String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.listBuckets(null);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public List<ObsBucket> listBuckets(ListBucketsRequest request, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.listBuckets(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public List<ObsBucket> listBuckets(ListBucketsRequest request, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.listBuckets(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucket(String bucketName, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteBucket(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucket(String bucketName, String accessKey, String secretKey, String securityToken)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.deleteBucket(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.listObjects(listObjectsRequest);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.listObjects(listObjectsRequest);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public boolean headBucket(String bucketName, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.headBucket(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public boolean headBucket(String bucketName, String accessKey, String secretKey, String securityToken)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.headBucket(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public ListVersionsResult listVersions(String bucketName, String prefix, String delimiter, String keyMarker,
            String versionIdMarker, long maxKeys, String nextVersionIdMarker, String accessKey, String secretKey)
                    throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.listVersions(bucketName, prefix, delimiter, keyMarker, versionIdMarker, maxKeys,
                    nextVersionIdMarker);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public ListVersionsResult listVersions(String bucketName, String prefix, String delimiter, String keyMarker,
            String versionIdMarker, long maxKeys, String nextVersionIdMarker, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.listVersions(bucketName, prefix, delimiter, keyMarker, versionIdMarker, maxKeys,
                    nextVersionIdMarker);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ListVersionsResult listVersions(ListVersionsRequest request, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.listVersions(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ListVersionsResult listVersions(ListVersionsRequest request, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.listVersions(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ListVersionsResult listVersions(String bucketName, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.listVersions(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ListVersionsResult listVersions(String bucketName, String accessKey, String secretKey, String securityToken)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.listVersions(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ListVersionsResult listVersions(String bucketName, long maxKeys, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.listVersions(bucketName, maxKeys);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ListVersionsResult listVersions(String bucketName, long maxKeys, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.listVersions(bucketName, maxKeys);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketMetadataInfoResult getBucketMetadata(BucketMetadataInfoRequest bucketMetadataInfoRequest,
            String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketMetadata(bucketMetadataInfoRequest);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketMetadataInfoResult getBucketMetadata(BucketMetadataInfoRequest bucketMetadataInfoRequest,
            String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketMetadata(bucketMetadataInfoRequest);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public AccessControlList getBucketAcl(String bucketName, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketAcl(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public AccessControlList getBucketAcl(String bucketName, String accessKey, String secretKey, String securityToken)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketAcl(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setBucketAcl(String bucketName, String cannedACL, AccessControlList acl, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketAcl(bucketName, cannedACL, acl);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setBucketAcl(String bucketName, String cannedACL, AccessControlList acl, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketAcl(bucketName, cannedACL, acl);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketAcl(String bucketName, AccessControlList acl, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketAcl(bucketName, acl);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketAcl(String bucketName, AccessControlList acl, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketAcl(bucketName, acl);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketLocationResponse getBucketLocation(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketLocationV2(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketLocationResponse getBucketLocation(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketLocationV2(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketStorageInfo getBucketStorageInfo(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketStorageInfo(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketStorageInfo getBucketStorageInfo(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketStorageInfo(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketQuota getBucketQuota(String bucketName, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketQuota(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketQuota getBucketQuota(String bucketName, String accessKey, String secretKey, String securityToken)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketQuota(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketQuota(String bucketName, BucketQuota bucketQuota, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketQuota(bucketName, bucketQuota);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketQuota(String bucketName, BucketQuota bucketQuota, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketQuota(bucketName, bucketQuota);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketCors(String bucketName, BucketCors bucketCors, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketCors(bucketName, bucketCors);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketCors(String bucketName, BucketCors bucketCors, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketCors(bucketName, bucketCors);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketCors getBucketCors(String bucketName, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketCors(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketCors getBucketCors(String bucketName, String accessKey, String secretKey, String securityToken)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketCors(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucketCors(String bucketName, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteBucketCors(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucketCors(String bucketName, String accessKey, String secretKey, String securityToken)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.deleteBucketCors(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public OptionsInfoResult optionsBucket(String bucketName, OptionsInfoRequest optionInfo, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.optionsBucket(bucketName, optionInfo);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public OptionsInfoResult optionsBucket(String bucketName, OptionsInfoRequest optionInfo, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.optionsBucket(bucketName, optionInfo);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public OptionsInfoResult optionsObject(String bucketName, String objectKey, OptionsInfoRequest optionInfo,
            String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.optionsObject(bucketName, objectKey, optionInfo);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public OptionsInfoResult optionsObject(String bucketName, String objectKey, OptionsInfoRequest optionInfo,
            String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.optionsObject(bucketName, objectKey, optionInfo);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public BucketLoggingConfiguration getBucketLoggingConfiguration(String bucketName, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketLoggingConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketLoggingConfiguration getBucketLogging(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketLogging(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public BucketLoggingConfiguration getBucketLoggingConfiguration(String bucketName, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketLoggingConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketLoggingConfiguration getBucketLogging(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketLogging(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setBucketLoggingConfiguration(String bucketName,
            BucketLoggingConfiguration loggingConfiguration, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketLoggingConfiguration(bucketName, loggingConfiguration);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketLogging(String bucketName, BucketLoggingConfiguration loggingConfiguration,
            String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketLogging(bucketName, loggingConfiguration);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setBucketLoggingConfiguration(String bucketName,
            BucketLoggingConfiguration loggingConfiguration, String accessKey, String secretKey, String securityToken)
                    throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketLoggingConfiguration(bucketName, loggingConfiguration);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketLogging(String bucketName, BucketLoggingConfiguration loggingConfiguration,
            String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketLogging(bucketName, loggingConfiguration);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setBucketLoggingConfiguration(String bucketName,
            BucketLoggingConfiguration loggingConfiguration, boolean updateTargetACLifRequired, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketLoggingConfiguration(bucketName, loggingConfiguration, updateTargetACLifRequired);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setBucketLoggingConfiguration(String bucketName,
            BucketLoggingConfiguration loggingConfiguration, boolean updateTargetACLifRequired, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketLoggingConfiguration(bucketName, loggingConfiguration, updateTargetACLifRequired);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketVersioning(String bucketName, BucketVersioningConfiguration versioningConfiguration,
            String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketVersioning(bucketName, versioningConfiguration);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketVersioning(String bucketName, BucketVersioningConfiguration versioningConfiguration,
            String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketVersioning(bucketName, versioningConfiguration);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketVersioningConfiguration getBucketVersioning(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketVersioning(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketVersioningConfiguration getBucketVersioning(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketVersioning(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public LifecycleConfiguration getBucketLifecycleConfiguration(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketLifecycleConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public LifecycleConfiguration getBucketLifecycle(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketLifecycle(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public LifecycleConfiguration getBucketLifecycleConfiguration(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketLifecycleConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public LifecycleConfiguration getBucketLifecycle(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketLifecycle(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setBucketLifecycleConfiguration(String bucketName, LifecycleConfiguration lifecycleConfig,
            String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketLifecycleConfiguration(bucketName, lifecycleConfig);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketLifecycle(String bucketName, LifecycleConfiguration lifecycleConfig,
            String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketLifecycle(bucketName, lifecycleConfig);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setBucketLifecycleConfiguration(String bucketName, LifecycleConfiguration lifecycleConfig,
            String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketLifecycleConfiguration(bucketName, lifecycleConfig);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketLifecycle(String bucketName, LifecycleConfiguration lifecycleConfig,
            String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketLifecycle(bucketName, lifecycleConfig);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse deleteBucketLifecycleConfiguration(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteBucketLifecycleConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucketLifecycle(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteBucketLifecycle(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse deleteBucketLifecycleConfiguration(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.deleteBucketLifecycleConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucketLifecycle(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.deleteBucketLifecycle(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketPolicyResponse getBucketPolicy(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketPolicyV2(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketPolicyResponse getBucketPolicy(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketPolicyV2(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketPolicy(String bucketName, String policy, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketPolicy(bucketName, policy);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketPolicy(String bucketName, String policy, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketPolicy(bucketName, policy);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucketPolicy(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteBucketPolicy(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucketPolicy(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.deleteBucketPolicy(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public WebsiteConfiguration getBucketWebsiteConfiguration(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketWebsiteConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public WebsiteConfiguration getBucketWebsite(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketWebsite(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public WebsiteConfiguration getBucketWebsiteConfiguration(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketWebsiteConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public WebsiteConfiguration getBucketWebsite(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketWebsite(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setBucketWebsiteConfiguration(String bucketName, WebsiteConfiguration websiteConfig,
            String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketWebsiteConfiguration(bucketName, websiteConfig);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketWebsite(String bucketName, WebsiteConfiguration websiteConfig, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketWebsite(bucketName, websiteConfig);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setBucketWebsiteConfiguration(String bucketName, WebsiteConfiguration websiteConfig,
            String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketWebsiteConfiguration(bucketName, websiteConfig);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketWebsite(String bucketName, WebsiteConfiguration websiteConfig, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketWebsite(bucketName, websiteConfig);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse deleteBucketWebsiteConfiguration(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteBucketWebsiteConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucketWebsite(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteBucketWebsite(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse deleteBucketWebsiteConfiguration(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.deleteBucketWebsiteConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucketWebsite(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.deleteBucketWebsite(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketTagging(String bucketName, BucketTagInfo bucketTagInfo, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketTagging(bucketName, bucketTagInfo);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketTagging(String bucketName, BucketTagInfo bucketTagInfo, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketTagging(bucketName, bucketTagInfo);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketTagInfo getBucketTagging(String bucketName, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketTagging(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketTagInfo getBucketTagging(String bucketName, String accessKey, String secretKey, String securityToken)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketTagging(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucketTagging(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteBucketTagging(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucketTagging(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.deleteBucketTagging(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setBucketReplicationConfiguration(String bucketName,
            ReplicationConfiguration replicationConfiguration, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketReplicationConfiguration(bucketName, replicationConfiguration);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketReplication(String bucketName, ReplicationConfiguration replicationConfiguration,
            String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketReplication(bucketName, replicationConfiguration);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setBucketReplicationConfiguration(String bucketName,
            ReplicationConfiguration replicationConfiguration, String accessKey, String secretKey, String securityToken)
                    throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketReplicationConfiguration(bucketName, replicationConfiguration);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketReplication(String bucketName, ReplicationConfiguration replicationConfiguration,
            String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketReplication(bucketName, replicationConfiguration);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public ReplicationConfiguration getBucketReplicationConfiguration(String bucketName, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketReplicationConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ReplicationConfiguration getBucketReplication(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketReplication(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public ReplicationConfiguration getBucketReplicationConfiguration(String bucketName, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketReplicationConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ReplicationConfiguration getBucketReplication(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketReplication(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse deleteBucketReplicationConfiguration(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteBucketReplicationConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucketReplication(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteBucketReplication(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse deleteBucketReplicationConfiguration(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.deleteBucketReplicationConfiguration(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteBucketReplication(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.deleteBucketReplication(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketNotificationConfiguration getBucketNotification(String bucketName, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketNotification(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketNotificationConfiguration getBucketNotification(String bucketName, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketNotification(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketNotification(String bucketName,
            BucketNotificationConfiguration bucketNotificationConfiguration, String accessKey, String secretKey)
                    throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketNotification(bucketName, bucketNotificationConfiguration);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketNotification(String bucketName,
            BucketNotificationConfiguration bucketNotificationConfiguration, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketNotification(bucketName, bucketNotificationConfiguration);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketStoragePolicy(final String bucketName,
            final BucketStoragePolicyConfiguration bucketStorage, String accessKey, String secretKey)
                    throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setBucketStoragePolicy(bucketName, bucketStorage);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setBucketStoragePolicy(final String bucketName,
            final BucketStoragePolicyConfiguration bucketStorage, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setBucketStoragePolicy(bucketName, bucketStorage);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketStoragePolicyConfiguration getBucketStoragePolicy(final String bucketName, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getBucketStoragePolicy(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public BucketStoragePolicyConfiguration getBucketStoragePolicy(final String bucketName, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getBucketStoragePolicy(bucketName);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public PutObjectResult putObject(PutObjectRequest request, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.putObject(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public PutObjectResult putObject(PutObjectRequest request, String accessKey, String secretKey, String securityToken)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.putObject(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ObsObject getObject(GetObjectRequest getObjectRequest, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getObject(getObjectRequest);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ObsObject getObject(GetObjectRequest getObjectRequest, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getObject(getObjectRequest);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ObjectMetadata getObjectMetadata(GetObjectMetadataRequest request, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getObjectMetadata(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ObjectMetadata getObjectMetadata(GetObjectMetadataRequest request, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getObjectMetadata(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public RestoreObjectStatus restoreObject(RestoreObjectRequest restoreObjectRequest, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.restoreObject(restoreObjectRequest);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public RestoreObjectStatus restoreObject(RestoreObjectRequest restoreObjectRequest, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.restoreObject(restoreObjectRequest);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteObject(String bucketName, String objectKey, String versionId, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteObject(bucketName, objectKey, versionId);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteObject(String bucketName, String objectKey, String versionId, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.deleteObject(bucketName, objectKey, versionId);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse deleteObject(String bucketName, String objectKey, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteObject(bucketName, objectKey);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.deleteObjects(deleteObjectsRequest);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.deleteObjects(deleteObjectsRequest);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public AccessControlList getObjectAcl(String bucketName, String objectKey, String versionId, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getObjectAcl(bucketName, objectKey, versionId);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public AccessControlList getObjectAcl(String bucketName, String objectKey, String versionId, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.getObjectAcl(bucketName, objectKey, versionId);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public AccessControlList getObjectAcl(String bucketName, String objectKey, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.getObjectAcl(bucketName, objectKey);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setObjectAcl(String bucketName, String objectKey, String cannedACL, AccessControlList acl,
            String versionId, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setObjectAcl(bucketName, objectKey, cannedACL, acl, versionId);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    @Deprecated
    public HeaderResponse setObjectAcl(String bucketName, String objectKey, String cannedACL, AccessControlList acl,
            String versionId, String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setObjectAcl(bucketName, objectKey, cannedACL, acl, versionId);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setObjectAcl(String bucketName, String objectKey, AccessControlList acl, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setObjectAcl(bucketName, objectKey, acl);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setObjectAcl(String bucketName, String objectKey, AccessControlList acl, String versionId,
            String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.setObjectAcl(bucketName, objectKey, acl, versionId);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse setObjectAcl(String bucketName, String objectKey, AccessControlList acl, String versionId,
            String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.setObjectAcl(bucketName, objectKey, acl, versionId);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.copyObject(copyObjectRequest);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.copyObject(copyObjectRequest);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request,
            String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.initiateMultipartUpload(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request,
            String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.initiateMultipartUpload(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse abortMultipartUpload(AbortMultipartUploadRequest request, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.abortMultipartUpload(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public HeaderResponse abortMultipartUpload(AbortMultipartUploadRequest request, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.abortMultipartUpload(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public UploadPartResult uploadPart(UploadPartRequest request, String accessKey, String secretKey)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.uploadPart(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public UploadPartResult uploadPart(UploadPartRequest request, String accessKey, String secretKey,
            String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.uploadPart(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public CopyPartResult copyPart(CopyPartRequest request, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.copyPart(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public CopyPartResult copyPart(CopyPartRequest request, String accessKey, String secretKey, String securityToken)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.copyPart(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request,
            String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.completeMultipartUpload(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request,
            String accessKey, String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.completeMultipartUpload(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ListPartsResult listParts(ListPartsRequest request, String accessKey, String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.listParts(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public ListPartsResult listParts(ListPartsRequest request, String accessKey, String secretKey, String securityToken)
            throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.listParts(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request, String accessKey,
            String secretKey) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey);
        try {
            return super.listMultipartUploads(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }

    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request, String accessKey,
            String secretKey, String securityToken) throws ObsException {
        this.setContextProviderCredentials(accessKey, secretKey, securityToken);
        try {
            return super.listMultipartUploads(request);
        } finally {
            this.clearContextProviderCredentials();
        }
    }
}

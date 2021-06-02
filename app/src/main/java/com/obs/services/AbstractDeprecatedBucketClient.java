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

import java.util.ArrayList;
import java.util.List;

import com.obs.services.exception.ObsException;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.BucketCors;
import com.obs.services.model.BucketLoggingConfiguration;
import com.obs.services.model.BucketVersioningConfiguration;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.LifecycleConfiguration;
import com.obs.services.model.ObsBucket;
import com.obs.services.model.OptionsInfoRequest;
import com.obs.services.model.OptionsInfoResult;
import com.obs.services.model.ReplicationConfiguration;
import com.obs.services.model.S3Bucket;
import com.obs.services.model.S3BucketCors;
import com.obs.services.model.SetBucketAclRequest;
import com.obs.services.model.SetBucketCorsRequest;
import com.obs.services.model.SetBucketLifecycleRequest;
import com.obs.services.model.SetBucketLoggingRequest;
import com.obs.services.model.SetBucketReplicationRequest;
import com.obs.services.model.SetBucketWebsiteRequest;
import com.obs.services.model.WebsiteConfiguration;

public abstract class AbstractDeprecatedBucketClient extends AbstractClient {
    
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
    
    /**
     * 设置桶的访问权限<br>
     * 
     * @param bucketName
     *            桶名
     * @param cannedACL
     *            预定义访问策略
     * @param acl
     *            访问权限 （ acl和cannedACL不能同时使用）
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    @Deprecated
    public HeaderResponse setBucketAcl(final String bucketName, final String cannedACL, final AccessControlList acl)
            throws ObsException {
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName, acl);
        request.setCannedACL(cannedACL);

        return setBucketAcl(request);
    }
    
    @Deprecated
    public HeaderResponse setBucketCors(final String bucketName, final S3BucketCors s3BucketCors) throws ObsException {
        ServiceUtils.asserParameterNotNull(s3BucketCors,
                "The bucket '" + bucketName + "' does not include Cors information");
        BucketCors bucketCors = new BucketCors();
        bucketCors.setRules(s3BucketCors.getRules());
        return this.setBucketCors(new SetBucketCorsRequest(bucketName, bucketCors));
    }
    
    /**
     * OPTIONS桶
     * 
     * @param bucketName
     *            桶名
     * @param optionInfo
     *            OPTIONS桶的请求参数
     * @return OPTIONS桶的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    @Deprecated
    public OptionsInfoResult optionsBucket(final String bucketName, final OptionsInfoRequest optionInfo)
            throws ObsException {
        return this.doActionWithResult("optionsBucket", bucketName, new ActionCallbackWithResult<OptionsInfoResult>() {

            @Override
            public OptionsInfoResult action() throws ServiceException {
                ServiceUtils.asserParameterNotNull(optionInfo, "OptionsInfoRequest is null");
                return AbstractDeprecatedBucketClient.this.optionsImpl(bucketName, null, optionInfo);
            }
        });
    }
    
    /**
     * 获取桶的日志管理配置
     * 
     * @param bucketName
     *            桶名
     * @return 桶的日志管理配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    @Deprecated
    public BucketLoggingConfiguration getBucketLoggingConfiguration(final String bucketName) throws ObsException {
        return getBucketLogging(new BaseBucketRequest(bucketName));
    }
    
    @Deprecated
    public HeaderResponse setBucketLoggingConfiguration(final String bucketName,
            final BucketLoggingConfiguration loggingConfiguration) throws ObsException {
        return this.setBucketLogging(new SetBucketLoggingRequest(bucketName, loggingConfiguration));
    }
    
    @Deprecated
    public HeaderResponse setBucketVersioning(String bucketName, String status) throws ObsException {
        return this.setBucketVersioning(bucketName, new BucketVersioningConfiguration(status));
    }
    
    @Deprecated
    public LifecycleConfiguration getBucketLifecycleConfiguration(final String bucketName) throws ObsException {
        return this.getBucketLifecycle(new BaseBucketRequest(bucketName));
    }
    
    @Deprecated
    public HeaderResponse setBucketLifecycleConfiguration(final String bucketName,
            final LifecycleConfiguration lifecycleConfig) throws ObsException {
        return this.setBucketLifecycle(new SetBucketLifecycleRequest(bucketName, lifecycleConfig));
    }
    
    @Deprecated
    public HeaderResponse deleteBucketLifecycleConfiguration(final String bucketName) throws ObsException {
        return this.deleteBucketLifecycle(new BaseBucketRequest(bucketName));
    }
    
    @Deprecated
    public WebsiteConfiguration getBucketWebsiteConfiguration(final String bucketName) throws ObsException {
        return this.getBucketWebsite(new BaseBucketRequest(bucketName));
    }
    
    @Deprecated
    public HeaderResponse setBucketWebsiteConfiguration(final String bucketName,
            final WebsiteConfiguration websiteConfig) throws ObsException {
        return setBucketWebsite(new SetBucketWebsiteRequest(bucketName, websiteConfig));
    }
    
    @Deprecated
    public HeaderResponse deleteBucketWebsiteConfiguration(final String bucketName) throws ObsException {
        return this.deleteBucketWebsite(new BaseBucketRequest(bucketName));
    }
    
    @Deprecated
    public HeaderResponse setBucketReplicationConfiguration(final String bucketName,
            final ReplicationConfiguration replicationConfiguration) throws ObsException {
        return setBucketReplication(new SetBucketReplicationRequest(bucketName, replicationConfiguration));
    }
    
    @Deprecated
    public ReplicationConfiguration getBucketReplicationConfiguration(final String bucketName) throws ObsException {
        return getBucketReplication(new BaseBucketRequest(bucketName));
    }
    
    @Deprecated
    public HeaderResponse deleteBucketReplicationConfiguration(final String bucketName) throws ObsException {
        return deleteBucketReplication(new BaseBucketRequest(bucketName));
    }
}

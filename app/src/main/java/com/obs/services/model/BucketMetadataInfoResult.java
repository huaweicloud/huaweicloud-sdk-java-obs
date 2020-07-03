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

package com.obs.services.model;

import java.util.List;

/**
 * 获取桶元数据信息的响应结果buc
 *
 */
public class BucketMetadataInfoResult extends OptionsInfoResult {

    private StorageClassEnum storageClass;

    private String location;

    private String obsVersion;

    private AvailableZoneEnum availableZone;

    private String epid;

    private BucketTypeEnum bucketType = BucketTypeEnum.OBJECT;

    public BucketMetadataInfoResult(String allowOrigin, List<String> allowHeaders, int maxAge,
            List<String> allowMethods, List<String> exposeHeaders, StorageClassEnum storageClass, String location,
            String obsVersion) {
        super(allowOrigin, allowHeaders, maxAge, allowMethods, exposeHeaders);
        this.storageClass = storageClass;
        this.location = location;
        this.obsVersion = obsVersion;
    }

    public BucketMetadataInfoResult(String allowOrigin, List<String> allowHeaders, int maxAge,
            List<String> allowMethods, List<String> exposeHeaders, StorageClassEnum storageClass, String location,
            String obsVersion, AvailableZoneEnum availableZone) {
        this(allowOrigin, allowHeaders, maxAge, allowMethods, exposeHeaders, storageClass, location, obsVersion);
        this.availableZone = availableZone;
    }

    public BucketMetadataInfoResult(String allowOrigin, List<String> allowHeaders, int maxAge,
            List<String> allowMethods, List<String> exposeHeaders, StorageClassEnum storageClass, String location,
            String obsVersion, AvailableZoneEnum availableZone, String epid, BucketTypeEnum bucketType) {
        this(allowOrigin, allowHeaders, maxAge, allowMethods, exposeHeaders, storageClass, location, obsVersion);
        this.availableZone = availableZone;
        this.epid = epid;
        this.bucketType = bucketType;
    }

    /**
     * 获取桶的存储类型
     * 
     * @return 桶的存储类型
     */
    @Deprecated
    public String getDefaultStorageClass() {
        return this.storageClass == null ? null : storageClass.getCode();
    }

    /**
     * 获取桶的存储类型
     * 
     * @return 桶的存储类型
     */
    public StorageClassEnum getBucketStorageClass() {
        return this.storageClass;
    }

    /**
     * 获取桶的区域位置
     * 
     * @return 桶的区域位置
     */
    public String getLocation() {
        return location;
    }

    /**
     * 获取OBS服务的版本
     * 
     * @return OBS服务的版本
     */
    public String getObsVersion() {
        return obsVersion;
    }

    /**
     * 获取桶的企业ID
     * 
     * @return 企业ID
     */
    public String getEpid() {
        return epid;
    }

    /**
     * 获取桶的集群类型
     * 
     * @return 桶的集群类型
     */
    public AvailableZoneEnum getAvailableZone() {
        return this.availableZone;
    }

    @Override
    public String toString() {
        return "BucketMetadataInfoResult [storageClass=" + storageClass + ", location=" + location + ", obsVersion="
                + obsVersion + ", bucketType=" + bucketType.name() + "]";
    }

    /**
     * 获取桶的类型
     * 
     * @return 桶的类型
     */
    public BucketTypeEnum getBucketType() {
        return bucketType;
    }
}

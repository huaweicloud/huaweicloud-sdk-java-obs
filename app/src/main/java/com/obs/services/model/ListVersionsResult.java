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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 列举桶内多版本对象的响应结果
 */
public class ListVersionsResult extends HeaderResponse {
    private String bucketName;

    private String prefix;

    private String keyMarker;

    private String nextKeyMarker;

    private String versionIdMarker;

    private String nextVersionIdMarker;

    private String maxKeys;

    private boolean isTruncated;

    private VersionOrDeleteMarker[] versions;

    private List<String> commonPrefixes;

    private String location;

    private String delimiter;

    @Deprecated
    //CHECKSTYLE:OFF
    public ListVersionsResult(String bucketName, String prefix, String keyMarker, String nextKeyMarker,
            String versionIdMarker, String nextVersionIdMarker, String maxKeys, boolean isTruncated,
            VersionOrDeleteMarker[] versions, List<String> commonPrefixes, String location, String delimiter) {
        super();
        this.bucketName = bucketName;
        this.prefix = prefix;
        this.keyMarker = keyMarker;
        this.nextKeyMarker = nextKeyMarker;
        this.versionIdMarker = versionIdMarker;
        this.nextVersionIdMarker = nextVersionIdMarker;
        this.maxKeys = maxKeys;
        this.isTruncated = isTruncated;
        if (null != versions) {
            this.versions = versions.clone();
        } else {
            this.versions = null;
        }
        this.commonPrefixes = commonPrefixes;
        this.location = location;
        this.delimiter = delimiter;
    }

    private ListVersionsResult(Builder builder) {
        super();
        this.bucketName = builder.bucketName;
        this.prefix = builder.prefix;
        this.keyMarker = builder.keyMarker;
        this.nextKeyMarker = builder.nextKeyMarker;
        this.versionIdMarker = builder.versionIdMarker;
        this.nextVersionIdMarker = builder.nextVersionIdMarker;
        this.maxKeys = builder.maxKeys;
        this.isTruncated = builder.isTruncated;
        if (null != builder.versions) {
            this.versions = builder.versions.clone();
        } else {
            this.versions = null;
        }
        this.commonPrefixes = builder.commonPrefixes;
        this.location = builder.location;
        this.delimiter = builder.delimiter;
    }
    
    public static final class Builder {
        private String bucketName;
        private String prefix;
        private String keyMarker;
        private String nextKeyMarker;
        private String versionIdMarker;
        private String nextVersionIdMarker;
        private String maxKeys;
        private boolean isTruncated;
        private VersionOrDeleteMarker[] versions;
        private List<String> commonPrefixes;
        private String location;
        private String delimiter;
        
        public Builder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }
        
        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }
        
        public Builder keyMarker(String keyMarker) {
            this.keyMarker = keyMarker;
            return this;
        }
        
        public Builder nextKeyMarker(String nextKeyMarker) {
            this.nextKeyMarker = nextKeyMarker;
            return this;
        }
        
        public Builder versionIdMarker(String versionIdMarker) {
            this.versionIdMarker = versionIdMarker;
            return this;
        }
        
        public Builder nextVersionIdMarker(String nextVersionIdMarker) {
            this.nextVersionIdMarker = nextVersionIdMarker;
            return this;
        }
        
        public Builder maxKeys(String maxKeys) {
            this.maxKeys = maxKeys;
            return this;
        }
        
        public Builder isTruncated(boolean isTruncated) {
            this.isTruncated = isTruncated;
            return this;
        }
        
        public Builder versions(VersionOrDeleteMarker[] versions) {
            if (null != versions) {
                this.versions = versions.clone();
            } else {
                this.versions = null;
            }
            return this;
        }
        
        public Builder commonPrefixes(List<String> commonPrefixes) {
            this.commonPrefixes = commonPrefixes;
            return this;
        }
        
        public Builder location(String location) {
            this.location = location;
            return this;
        }
        
        public Builder delimiter(String delimiter) {
            this.delimiter = delimiter;
            return this;
        }
        
        public ListVersionsResult builder() {
            return new ListVersionsResult(this);
        }
    }
    
    /**
     * 获取桶名
     * 
     * @return 桶名
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 获取列举多版本对象请求中的对象名前缀
     * 
     * @return 请求中的对象名前缀
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 获取列举多版本对象请求中的起始位置（按对象名排序）
     * 
     * @return 请求中的起始位置标识
     */
    public String getKeyMarker() {
        return keyMarker;
    }

    /**
     * 获取列举多版本对象请求中的起始位置（按对象版本号排序）
     * 
     * @return 请求中的起始位置标识
     */
    public String getVersionIdMarker() {
        return versionIdMarker;
    }

    /**
     * 获取列举多版本对象的最大条目数
     * 
     * @return 列举多版本对象的最大条目数
     */
    public String getMaxKeys() {
        return maxKeys;
    }

    /**
     * 判断查询结果列表是否被截断。true表示截断，本次没有返回全部结果；false表示未截断，本次已经返回了全部结果。
     * 
     * @return 截断标识
     */
    public boolean isTruncated() {
        return isTruncated;
    }

    /**
     * 获取桶内的多版本对象数组
     * 
     * @return 多版本对象数组，细描述见{@link VersionOrDeleteMarker}
     */
    public VersionOrDeleteMarker[] getVersions() {
        if (null != versions) {
            return versions.clone(); 
        }
        return new VersionOrDeleteMarker[0];
    }

    /**
     * 获取下次请求的起始位置（按对象名排序）
     * 
     * @return 下次请求的起始位置标识
     */
    public String getNextKeyMarker() {
        return nextKeyMarker;
    }

    /**
     * 获取下次请求的起始位置（按对象版本号排序）
     * 
     * @return 下次请求的起始位置标识
     */
    public String getNextVersionIdMarker() {
        return nextVersionIdMarker;
    }

    /**
     * 获取分组后的对象名前缀列表
     * 
     * @return 分组后的对象名前缀列表
     */
    public List<String> getCommonPrefixes() {
        if (commonPrefixes == null) {
            commonPrefixes = new ArrayList<String>();
        }
        return commonPrefixes;
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
     * 获取列举对象时请求中的分组字符
     * 
     * @return 分组字符
     */
    public String getDelimiter() {
        return delimiter;
    }

    @Override
    public String toString() {
        return "ListVersionsResult [bucketName=" + bucketName + ", prefix=" + prefix + ", keyMarker=" + keyMarker
                + ", nextKeyMarker=" + nextKeyMarker + ", versionIdMarker=" + versionIdMarker + ", nextVersionIdMarker="
                + nextVersionIdMarker + ", maxKeys=" + maxKeys + ", isTruncated=" + isTruncated + ", versions="
                + Arrays.toString(versions) + ", commonPrefixes=" + commonPrefixes + ", location=" + location
                + ", delimiter=" + delimiter + "]";
    }

}

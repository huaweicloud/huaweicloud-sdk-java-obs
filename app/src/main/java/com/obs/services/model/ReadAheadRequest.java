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

/**
 * 预读对象的请求参数
 */
public class ReadAheadRequest {
    private String bucketName;

    private String prefix;

    private CacheOptionEnum cacheOption;

    private long ttl = 60 * 60 * 24L;

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param prefix
     *            待预读对象的对象名前缀
     */
    public ReadAheadRequest(String bucketName, String prefix) {
        this.setBucketName(bucketName);
        this.setPrefix(prefix);
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param prefix
     *            待预读对象的对象名前缀
     * @param cacheOption
     *            预读缓存的控制选项
     * @param ttl
     *            缓存数据过期时间，单位：秒，取值范围：0~259200（3天）
     */
    public ReadAheadRequest(String bucketName, String prefix, CacheOptionEnum cacheOption, long ttl) {
        this.setBucketName(bucketName);
        this.setPrefix(prefix);
        this.setCacheOption(cacheOption);
        this.setTtl(ttl);
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
     * 设置桶名
     * 
     * @param bucketName
     *            桶名
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取待预读对象的对象名前缀
     * 
     * @return 待预读对象的对象名前缀
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置待预读对象的对象名前缀
     * 
     * @param prefix
     *            待预读对象的对象名前缀
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 获取预读缓存的控制选项
     * 
     * @return 预读缓存的控制选项
     */
    public CacheOptionEnum getCacheOption() {
        return cacheOption;
    }

    /**
     * 设置预读缓存的控制选项
     * 
     * @param cacheOption
     *            预读缓存的控制选项
     */
    public void setCacheOption(CacheOptionEnum cacheOption) {
        this.cacheOption = cacheOption;
    }

    /**
     * 获取缓存数据过期时间
     * 
     * @return 缓存数据过期时间 单位：秒
     */
    public long getTtl() {
        return ttl;
    }

    /**
     * 设置缓存数据过期时间
     * 
     * @param ttl
     *            缓存数据过期时间 单位：秒，取值范围：0~259200（72小时）， 默认值：24小时
     */
    public void setTtl(long ttl) {
        if (ttl < 0 || ttl > 259200) {
            return;
        }
        this.ttl = ttl;
    }
}

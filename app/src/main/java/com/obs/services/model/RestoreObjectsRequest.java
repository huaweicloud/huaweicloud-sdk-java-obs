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
 * 批量取回归档存储对象请求参数
 * 在一个请求中不能同时设置prefix和keyAndVersions参数
 * 如果两个参数都为空，则取回桶中的所有归档存储对象
 */
public class RestoreObjectsRequest extends AbstractBulkRequest{
    
    private int days;

    private RestoreTierEnum tier;

    private String prefix;
    
    private boolean versionRestored;

    private List<KeyAndVersion> keyAndVersions;

    private TaskCallback<RestoreObjectResult, RestoreObjectRequest> callback;

    public RestoreObjectsRequest() {

    }

    /**
     * 构造函数
     * 
     * @param bucketName 桶名
     */
    public RestoreObjectsRequest(String bucketName) {
        super(bucketName);
    }

    /**
     * 构造函数
     * 
     * @param bucketName     桶名
     * @param days           对象取回后保存时间
     * @param tier           取回选项
     */
    public RestoreObjectsRequest(String bucketName, int days, RestoreTierEnum tier) {
        super(bucketName);
        this.days = days;
        this.tier = tier;
    }
    

    /**
     * 获取对象取回后保存时间，单位：天，最小值为1，最大值为30
     * 
     * @return 对象取回后保存时间
     */
    public int getDays() {
        return days;
    }

    /**
     * 设置对象取回后保存时间，单位：天，最小值为1，最大值为30
     * 
     * @param days 对象取回后保存时间
     */
    public void setDays(int days) {
        this.days = days;
    }

    /**
     * 获取取回选项.
     * 
     * @return 取回选项
     */
    public RestoreTierEnum getRestoreTier() {
        return tier;
    }

    /**
     * 设置取回选项.
     * 
     * @param tier 取回选项
     */
    public void setRestoreTier(RestoreTierEnum tier) {
        this.tier = tier;
    }

    /**
     * 设置批量取回的对象名前缀
     * 
     * @param prefix 对象名前缀
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 获取批量取回的对象名前缀
     * 
     * @return 对象名前缀
     */
    public String getPrefix() {
        return prefix;
    }
    
    /**
     * 是否取回多版本归档存储对象
     * 默认为false，只取回最新版本的归档存储对象
     * 
     * @return 多版本标志取回标记
     */
    public boolean isVersionRestored() {
        return versionRestored;
    }

    /**
     * 设置是否取回多版本归档存储对象
     *
     * @param versionRestored 多版本标志取回标记
     */
    public void setVersionRestored(boolean versionRestored) {
        this.versionRestored = versionRestored;
    }

    /**
     * 设置待取回对象列表
     * 
     * @param keyAndVersions 待取回对象列表
     */
    public void setKeyAndVersions(List<KeyAndVersion> keyAndVersions) {
        this.keyAndVersions = keyAndVersions;
    }

    /**
     * 获取待取回对象列表
     * 
     * @return 待取回对象列表
     */
    public List<KeyAndVersion> getKeyAndVersions() {
        return this.keyAndVersions;
    }

    /**
     * 新增待取回的对象
     * 
     * @param objectKey 对象名
     * @param versionId 对象版本号
     * @return 新增的待取回对象
     */
    public KeyAndVersion addKeyAndVersion(String objectKey, String versionId) {
        KeyAndVersion kv = new KeyAndVersion(objectKey, versionId);
        this.getKeyAndVersions().add(kv);
        return kv;
    }

    /**
     * 新增待取回的对象
     * 
     * @param objectKey 对象名
     * @return 新增的待取回对象
     */
    public KeyAndVersion addKeyAndVersion(String objectKey) {
        return this.addKeyAndVersion(objectKey, null);
    }

    /**
     * 获取批量任务的回调对象
     * 
     * @return 回调对象
     */
    public TaskCallback<RestoreObjectResult, RestoreObjectRequest> getCallback() {
        return callback;
    }

    /**
     * 设置批量任务的回调对象
     * 
     * @param callback 回调对象
     */
    public void setCallback(TaskCallback<RestoreObjectResult, RestoreObjectRequest> callback) {
        this.callback = callback;
    }


    @Override
    public String toString() {
        return "RestoreObjectsRequest [bucketName=" + bucketName + ", days=" + days + ", tier=" + tier + "]";
    }

}

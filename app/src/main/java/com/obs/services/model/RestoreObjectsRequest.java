/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.obs.services.model;

import java.util.List;

import com.obs.services.internal.ObsConstraint;

/**
 * 批量取回归档存储对象请求参数
 * 在一个请求中不能同时设置prefix和keyAndVersions参数
 * 如果两个参数都为空，则取回桶中的所有归档存储对象
 */
public class RestoreObjectsRequest {
    
    private String bucketName;

    private int days;

    private RestoreTierEnum tier;

    private String prefix;
    
    private boolean versionRestored;

    private List<KeyAndVersion> keyAndVersions;

    private TaskCallback<RestoreObjectResult, RestoreObjectRequest> callback;

    private TaskProgressListener listener;
     
    private int taskThreadNum = ObsConstraint.DEFAULT_TASK_THREAD_NUM;
    
    private int taskQueueNum = ObsConstraint.DEFAULT_WORK_QUEUE_NUM;

    private int taskProgressInterval = ObsConstraint.DEFAULT_TASK_PROGRESS_INTERVAL;
    
    public RestoreObjectsRequest() {

    }

    /**
     * 构造函数
     * 
     * @param bucketName 桶名
     */
    public RestoreObjectsRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 构造函数
     * 
     * @param bucketName     桶名
     * @param days           对象取回后保存时间
     * @param tier           取回选项
     */
    public RestoreObjectsRequest(String bucketName, int days, RestoreTierEnum tier) {
        this.bucketName = bucketName;
        this.days = days;
        this.tier = tier;
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
     * @param bucketName 桶名
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
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
     * @return versionRestored 多版本标志取回标记
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

    /**
     * 获取批量任务的进度监听器
     * 
     * @return 进度监听器
     */
    public TaskProgressListener getProgressListener() {
        return listener;
    }

    /**
     * 设置批量任务的进度监听器
     * 
     * @param listener 进度监听器
     */
    public void setProgressListener(TaskProgressListener listener) {
        this.listener = listener;
    }
    
    /**
     * 获取批量任务的最大并发数，默认为10
     * @return 最大线程数
     */
    public int getTaskThreadNum() {
        return taskThreadNum;
    }

    /**
     * 设置批量任务的最大并发数，默认为10
     * @param taskThreadNum 最大线程数
     */
    public void setTaskThreadNum(int taskThreadNum) {
        this.taskThreadNum = taskThreadNum;
    }
    
    /**
     * 获取批量任务的队列长度，默认为20000
     * @return 工作队列长度
     */
    public int getTaskQueueNum() {
        return taskQueueNum;
    }

    /**
     * 设置批量任务中线程池的工作队列长度，默认为20000
     * @param taskQueueNum 工作队列长度
     */
    public void setTaskQueueNum(int taskQueueNum) {
        this.taskQueueNum = taskQueueNum;
    }

    /**
     * 获取任务进度监听器回调的阈值，默认为50
     * @return 进度监听器回调的阈值
     */
    public int getProgressInterval() {
        return taskProgressInterval;
    }

    /**
     *  设置任务进度监听器回调的阈值，默认为50
     * @param taskProgressInterval 进度监听器回调的阈值
     */
    public void setProgressInterval(int taskProgressInterval) {
        this.taskProgressInterval = taskProgressInterval;
    }

    @Override
    public String toString() {
        return "RestoreObjectsRequest [bucketName=" + bucketName + ", days=" + days + ", tier=" + tier + "]";
    }

}

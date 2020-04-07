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

import com.obs.services.internal.ObsConstraint;

/**
 * 批量任务请求参数抽象类
 *
 */
public abstract class AbstractBulkRequest extends GenericRequest {

    protected String bucketName;

    protected TaskProgressListener listener;

    protected int taskThreadNum = ObsConstraint.DEFAULT_TASK_THREAD_NUM;

    protected int taskQueueNum = ObsConstraint.DEFAULT_WORK_QUEUE_NUM;

    protected int taskProgressInterval = ObsConstraint.DEFAULT_TASK_PROGRESS_INTERVAL;

    public AbstractBulkRequest() {
    }

    public AbstractBulkRequest(String bucketName) {
        this.bucketName = bucketName;
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
     * @param listener
     *            进度监听器
     */
    public void setProgressListener(TaskProgressListener listener) {
        this.listener = listener;
    }

    /**
     * 获取批量任务的最大并发数，默认为10
     * 
     * @return 最大线程数
     */
    public int getTaskThreadNum() {
        return taskThreadNum;
    }

    /**
     * 设置批量任务的最大并发数，默认为10
     * 
     * @param taskThreadNum
     *            最大线程数
     */
    public void setTaskThreadNum(int taskThreadNum) {
        this.taskThreadNum = taskThreadNum;
    }

    /**
     * 获取批量任务的队列长度，默认为20000
     * 
     * @return 工作队列长度
     */
    public int getTaskQueueNum() {
        return taskQueueNum;
    }

    /**
     * 设置批量任务中线程池的工作队列长度，默认为20000
     * 
     * @param taskQueueNum
     *            工作队列长度
     */
    public void setTaskQueueNum(int taskQueueNum) {
        this.taskQueueNum = taskQueueNum;
    }

    /**
     * 获取任务进度监听器回调的阈值，默认为50
     * 
     * @return 进度监听器回调的阈值
     */
    public int getProgressInterval() {
        return taskProgressInterval;
    }

    /**
     * 设置任务进度监听器回调的阈值，默认为50
     * 
     * @param taskProgressInterval
     *            进度监听器回调的阈值
     */
    public void setProgressInterval(int taskProgressInterval) {
        if (taskProgressInterval <= 0) {
            throw new IllegalArgumentException("ProgressInterval should be greater than 0.");
        }
        this.taskProgressInterval = taskProgressInterval;
    }
}

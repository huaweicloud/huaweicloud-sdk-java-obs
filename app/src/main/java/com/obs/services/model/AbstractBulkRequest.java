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
**/
package com.obs.services.model;

import com.obs.services.internal.ObsConstraint;


public abstract class AbstractBulkRequest {
   
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
    


    public String getBucketName() {
        return bucketName;
    }


    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }


    public TaskProgressListener getProgressListener() {
        return listener;
    }


    public void setProgressListener(TaskProgressListener listener) {
        this.listener = listener;
    }


    public int getTaskThreadNum() {
        return taskThreadNum;
    }


    public void setTaskThreadNum(int taskThreadNum) {
        this.taskThreadNum = taskThreadNum;
    }


    public int getTaskQueueNum() {
        return taskQueueNum;
    }


    public void setTaskQueueNum(int taskQueueNum) {
        this.taskQueueNum = taskQueueNum;
    }


    public int getProgressInterval() {
        return taskProgressInterval;
    }


    public void setProgressInterval(int taskProgressInterval) {
        if (taskProgressInterval <= 0) {
            throw new IllegalArgumentException("ProgressInterval should be greater than 0.");
        }
        this.taskProgressInterval = taskProgressInterval;
    }
}

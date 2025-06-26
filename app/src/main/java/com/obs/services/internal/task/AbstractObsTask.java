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

package com.obs.services.internal.task;

import com.obs.services.ObsClient;
import com.obs.services.model.TaskProgressListener;

public abstract class AbstractObsTask implements Runnable {

    protected ObsClient obsClient;
    protected String bucketName;
    protected DefaultTaskProgressStatus progressStatus;
    protected TaskProgressListener progressListener;
    protected int taskProgressInterval;

    public AbstractObsTask(ObsClient obsClient, String bucketName) {
        this.obsClient = obsClient;
        this.bucketName = bucketName;
    }

    public AbstractObsTask(ObsClient obsClient, String bucketName, DefaultTaskProgressStatus progressStatus,
            TaskProgressListener progressListener, int taskProgressInterval) {
        this.obsClient = obsClient;
        this.bucketName = bucketName;
        this.progressStatus = progressStatus;
        this.progressListener = progressListener;
        this.taskProgressInterval = taskProgressInterval;
    }

    public ObsClient getObsClient() {
        return obsClient;
    }

    public void setObsClient(ObsClient obsClient) {
        this.obsClient = obsClient;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public DefaultTaskProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(DefaultTaskProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }

    public TaskProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(TaskProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public int getTaskProgressInterval() {
        return taskProgressInterval;
    }

    public void setTaskProgressInterval(int taskProgressInterval) {
        this.taskProgressInterval = taskProgressInterval;
    }
}

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
import com.obs.services.exception.ObsException;
import com.obs.services.model.RestoreObjectRequest;
import com.obs.services.model.RestoreObjectResult;
import com.obs.services.model.TaskCallback;
import com.obs.services.model.TaskProgressListener;

public class RestoreObjectTask extends AbstractObsTask {

    private RestoreObjectRequest taskRequest;

    protected TaskCallback<RestoreObjectResult, RestoreObjectRequest> callback;

    public RestoreObjectTask(ObsClient obsClient, String bucketName) {
        super(obsClient, bucketName);
    }

    public RestoreObjectTask(ObsClient obsClient, String bucketName, RestoreObjectRequest taskRequest,
            TaskCallback<RestoreObjectResult, RestoreObjectRequest> callback) {
        super(obsClient, bucketName);
        this.taskRequest = taskRequest;
        this.callback = callback;
    }

    public RestoreObjectTask(ObsClient obsClient, String bucketName, RestoreObjectRequest taskRequest,
            TaskCallback<RestoreObjectResult, RestoreObjectRequest> callback, TaskProgressListener listener,
            DefaultTaskProgressStatus progressStatus, int taskProgressInterval) {
        super(obsClient, bucketName, progressStatus, listener, taskProgressInterval);
        this.taskRequest = taskRequest;
        this.callback = callback;
    }

    public RestoreObjectRequest getTaskRequest() {
        return taskRequest;
    }

    public void setTaskRequest(RestoreObjectRequest taskRequest) {
        this.taskRequest = taskRequest;
    }

    public TaskCallback<RestoreObjectResult, RestoreObjectRequest> getCallback() {
        return callback;
    }

    public void setCallback(TaskCallback<RestoreObjectResult, RestoreObjectRequest> callback) {
        this.callback = callback;
    }

    private void restoreObjects() {
        try {
            RestoreObjectResult result = obsClient.restoreObjectV2(taskRequest);
            progressStatus.succeedTaskIncrement();
            callback.onSuccess(result);
        } catch (ObsException e) {
            progressStatus.failTaskIncrement();
            callback.onException(e, taskRequest);
        }
        progressStatus.execTaskIncrement();
        if (progressListener != null) {
            if (progressStatus.getExecTaskNum() % this.taskProgressInterval == 0) {
                progressListener.progressChanged(progressStatus);
            }
            if (progressStatus.getExecTaskNum() == progressStatus.getTotalTaskNum()) {
                progressListener.progressChanged(progressStatus);
            }
        }
    }

    @Override
    public void run() {
        restoreObjects();
    }

}

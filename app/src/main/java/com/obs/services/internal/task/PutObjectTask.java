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
import com.obs.services.model.ProgressStatus;
import com.obs.services.model.PutObjectBasicRequest;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import com.obs.services.model.TaskCallback;
import com.obs.services.model.UploadObjectsProgressListener;

public class PutObjectTask implements Runnable {
	
    protected ObsClient obsClient;
	
    protected String bucketName;
    
    private UploadObjectsProgressListener progressListener;
	
	private int taskProgressInterval;
	
	private PutObjectRequest taskRequest;
	
	private TaskCallback<PutObjectResult, PutObjectBasicRequest> callback;
	
	private UploadTaskProgressStatus taskStatus;

    public PutObjectTask(ObsClient obsClient, String bucketName, PutObjectRequest taskRequest,
            TaskCallback<PutObjectResult, PutObjectBasicRequest> callback, UploadObjectsProgressListener progressListener,
            UploadTaskProgressStatus progressStatus, int taskProgressInterval) {
    	this.obsClient = obsClient;
        this.bucketName = bucketName;
        this.taskRequest = taskRequest;
        this.callback = callback;
        this.progressListener = progressListener;
        this.taskStatus = progressStatus;
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
    
    public UploadObjectsProgressListener getUploadObjectsProgressListener() {
        return progressListener;
    }

    public void setUploadObjectsProgressListener(UploadObjectsProgressListener progressListener) {
        this.progressListener = progressListener;
    }
    
    public int getTaskProgressInterval() {
        return taskProgressInterval;
    }

    public void setTaskProgressInterval(int taskProgressInterval) {
        this.taskProgressInterval = taskProgressInterval;
    }
	
	public PutObjectRequest getTaskRequest() {
		return taskRequest;
	}

	public void setTaskRequest(PutObjectRequest taskRequest) {
		this.taskRequest = taskRequest;
	}

	public TaskCallback<PutObjectResult, PutObjectBasicRequest> getCallback() {
		return callback;
	}

	public void setCallback(TaskCallback<PutObjectResult, PutObjectBasicRequest> callback) {
		this.callback = callback;
	}

	public UploadTaskProgressStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(UploadTaskProgressStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	private void putObjects() {
		try {
			PutObjectResult result = obsClient.putObject(taskRequest);
			taskStatus.succeedTaskIncrement();
			PutObjectResult ret = new PutObjectResult(result.getBucketName(), result.getObjectKey()
					, result.getEtag(), result.getVersionId(), result.getObjectUrl(), result.getResponseHeaders(), result.getStatusCode());
            callback.onSuccess(ret);
        } catch (ObsException e) {
        	taskStatus.failTaskIncrement();
            callback.onException(e, taskRequest);
        } finally {
        	taskStatus.execTaskIncrement();
            if (progressListener != null) {
                if (taskStatus.getExecTaskNum() % this.taskProgressInterval == 0) {
                    progressListener.progressChanged(taskStatus);
                }
                if (taskStatus.getExecTaskNum() == taskStatus.getTotalTaskNum()) {
                    progressListener.progressChanged(taskStatus);
                }
            }

        	final String key = taskRequest.getObjectKey();
        	ProgressStatus status = taskStatus.getTaskStatus(key);
        	if(status != null) {
        		taskStatus.addEndingTaskSize(status.getTransferredBytes());
        	}
        	taskStatus.removeTaskTable(key);
        }
	}

	@Override
    public void run() {
        putObjects();
    }
}

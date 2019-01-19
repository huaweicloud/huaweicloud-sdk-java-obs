package com.obs.services.internal.task;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.RestoreObjectRequest;
import com.obs.services.model.RestoreObjectResult;
import com.obs.services.model.TaskCallback;
import com.obs.services.model.TaskProgressListener;

public class RestoreObjectTask extends AbstractObsTask {

    private RestoreObjectRequest taskRequest;
    private DefaultTaskProgressStatus progressStatus;
    private int taskProgressInterval;
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
        super(obsClient, bucketName);
        this.taskRequest = taskRequest;
        this.callback = callback;
        this.progressListener = listener;
        this.progressStatus = progressStatus;
        this.taskProgressInterval = taskProgressInterval;
    }

    public RestoreObjectRequest getTaskRequest() {
        return taskRequest;
    }

    public void setTaskRequest(RestoreObjectRequest taskRequest) {
        this.taskRequest = taskRequest;
    }

    public DefaultTaskProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(DefaultTaskProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }

    public TaskCallback<RestoreObjectResult, RestoreObjectRequest> getCallback() {
        return callback;
    }

    public void setCallback(TaskCallback<RestoreObjectResult, RestoreObjectRequest> callback) {
        this.callback = callback;
    }

    public int getTaskProgressInterval() {
        return taskProgressInterval;
    }

    public void setTaskProgressInterval(int taskProgressInterval) {
        this.taskProgressInterval = taskProgressInterval;
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

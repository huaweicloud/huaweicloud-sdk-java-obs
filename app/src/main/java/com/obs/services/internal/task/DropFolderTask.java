package com.obs.services.internal.task;

import com.obs.services.ObsClient;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.TaskCallback;
import com.obs.services.model.TaskProgressListener;

public class DropFolderTask extends AbstractObsTask {

    private String objectKey;

    private TaskCallback<DeleteObjectResult, String> callback;

    public DropFolderTask(ObsClient obsClient, String bucketName) {
        super(obsClient, bucketName);
    }

    public DropFolderTask(ObsClient obsClient, String bucketName, String objectKey,
            DefaultTaskProgressStatus progressStatus, TaskProgressListener progressListener, int taskProgressInterval,
            TaskCallback<DeleteObjectResult, String> callback) {
        super(obsClient, bucketName, progressStatus, progressListener, taskProgressInterval);
        this.objectKey = objectKey;
        this.callback = callback;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjeceKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public TaskCallback<DeleteObjectResult, String> getCallback() {
        return callback;
    }

    public void setCallback(TaskCallback<DeleteObjectResult, String> callback) {
        this.callback = callback;
    }

    private void dropFolder() {
        DeleteObjectResult result = obsClient.deleteObject(bucketName, objectKey);
        progressStatus.succeedTaskIncrement();
        callback.onSuccess(result);
    }

    @Override
    public void run() {
        dropFolder();
    }

}

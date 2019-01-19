package com.obs.services.internal.task;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.TaskCallback;
import com.obs.services.model.TaskProgressListener;

public abstract class AbstractObsTask implements Runnable {

    protected ObsClient obsClient;
    protected String bucketName;
    protected TaskProgressListener progressListener;

    public AbstractObsTask(ObsClient obsClient, String bucketName) {
        this.obsClient = obsClient;
        this.bucketName = bucketName;
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

    public TaskProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(TaskProgressListener progressListener) {
        this.progressListener = progressListener;
    } 
}

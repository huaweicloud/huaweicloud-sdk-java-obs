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

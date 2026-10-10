/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.realtimelog;

/**
 * Configuration for bucket real-time log analysis.
 * When enabled, OBS will push access logs to Log Tank Service (LTS) in real time.
 */
public class BucketRealTimeLogConfiguration {
    private String logGroupId;

    private String logStreamId;

    private String projectId;

    /**
     * Constructor with all required fields.
     *
     * @param logGroupId
     *            the log group ID specifying the target log group for log push
     * @param logStreamId
     *            the log stream ID specifying the target log stream for log push
     * @param projectId
     *            the project ID to which the log group and log stream belong
     */
    public BucketRealTimeLogConfiguration(String logGroupId, String logStreamId, String projectId) {
        this.logGroupId = logGroupId;
        this.logStreamId = logStreamId;
        this.projectId = projectId;
    }

    /**
     * Get the log group ID.
     *
     * @return the log group ID
     */
    public String getLogGroupId() {
        return logGroupId;
    }

    /**
     * Set the log group ID.
     *
     * @param logGroupId
     *            the log group ID specifying the target log group for log push
     */
    public void setLogGroupId(String logGroupId) {
        this.logGroupId = logGroupId;
    }

    /**
     * Get the log stream ID.
     *
     * @return the log stream ID
     */
    public String getLogStreamId() {
        return logStreamId;
    }

    /**
     * Set the log stream ID.
     *
     * @param logStreamId
     *            the log stream ID specifying the target log stream for log push
     */
    public void setLogStreamId(String logStreamId) {
        this.logStreamId = logStreamId;
    }

    /**
     * Get the project ID.
     *
     * @return the project ID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Set the project ID.
     *
     * @param projectId
     *            the project ID to which the log group and log stream belong
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}

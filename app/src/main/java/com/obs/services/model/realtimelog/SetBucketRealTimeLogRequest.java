/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.realtimelog;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

/**
 * Request to set the real-time log configuration of a bucket.
 */
public class SetBucketRealTimeLogRequest extends BaseBucketRequest {
    {
        httpMethod = HttpMethodEnum.PUT;
    }
    private BucketRealTimeLogConfiguration realTimeLogConfiguration;

    /**
     * Constructor with bucket name and configuration.
     *
     * @param bucketName
     *            the bucket name
     * @param realTimeLogConfiguration
     *            the real-time log configuration to set
     */
    public SetBucketRealTimeLogRequest(String bucketName, BucketRealTimeLogConfiguration realTimeLogConfiguration) {
        super(bucketName);
        this.realTimeLogConfiguration = realTimeLogConfiguration;
    }

    /**
     * Get the real-time log configuration.
     *
     * @return the real-time log configuration
     */
    public BucketRealTimeLogConfiguration getRealTimeLogConfiguration() {
        return realTimeLogConfiguration;
    }

    /**
     * Set the real-time log configuration.
     *
     * @param realTimeLogConfiguration
     *            the real-time log configuration to set
     */
    public void setRealTimeLogConfiguration(BucketRealTimeLogConfiguration realTimeLogConfiguration) {
        this.realTimeLogConfiguration = realTimeLogConfiguration;
    }
}

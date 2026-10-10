/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.realtimelog;

import com.obs.services.model.HeaderResponse;

/**
 * Result of getting the real-time log configuration of a bucket.
 */
public class GetBucketRealTimeLogResult extends HeaderResponse {
    private BucketRealTimeLogConfiguration realTimeLogConfiguration;

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
     *            the real-time log configuration
     */
    public void setRealTimeLogConfiguration(BucketRealTimeLogConfiguration realTimeLogConfiguration) {
        this.realTimeLogConfiguration = realTimeLogConfiguration;
    }
}

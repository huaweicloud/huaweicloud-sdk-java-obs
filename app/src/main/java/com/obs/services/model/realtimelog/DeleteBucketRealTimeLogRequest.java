/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.realtimelog;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

/**
 * Request to delete the real-time log configuration of a bucket.
 */
public class DeleteBucketRealTimeLogRequest extends BaseBucketRequest {
    {
        httpMethod = HttpMethodEnum.DELETE;
    }

    /**
     * Default constructor.
     */
    public DeleteBucketRealTimeLogRequest() {
        super();
    }

    /**
     * Constructor with bucket name.
     *
     * @param bucketName
     *            the bucket name
     */
    public DeleteBucketRealTimeLogRequest(String bucketName) {
        super(bucketName);
    }
}

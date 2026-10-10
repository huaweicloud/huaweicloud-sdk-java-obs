/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.realtimelog;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

/**
 * Request to get the real-time log configuration of a bucket.
 */
public class GetBucketRealTimeLogRequest extends BaseBucketRequest {
    {
        httpMethod = HttpMethodEnum.GET;
    }

    /**
     * Default constructor.
     */
    public GetBucketRealTimeLogRequest() {
        super();
    }

    /**
     * Constructor with bucket name.
     *
     * @param bucketName
     *            the bucket name
     */
    public GetBucketRealTimeLogRequest(String bucketName) {
        super(bucketName);
    }
}

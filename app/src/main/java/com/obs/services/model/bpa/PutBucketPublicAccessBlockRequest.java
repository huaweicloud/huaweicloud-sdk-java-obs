/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.bpa;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

public class PutBucketPublicAccessBlockRequest  extends BaseBucketRequest {
    {
        httpMethod = HttpMethodEnum.PUT;
    }
    private BucketPublicAccessBlock bucketPublicAccessBlock;

    public PutBucketPublicAccessBlockRequest(String bucketName, BucketPublicAccessBlock bucketPublicAccessBlock) {
        super(bucketName);
        this.bucketPublicAccessBlock = bucketPublicAccessBlock;
    }

    public BucketPublicAccessBlock getBucketPublicAccessBlock() {
        if (bucketPublicAccessBlock == null) {
            bucketPublicAccessBlock = new BucketPublicAccessBlock();
        }
        return bucketPublicAccessBlock;
    }

    public void setBucketPublicAccessBlock(BucketPublicAccessBlock bucketPublicAccessBlock) {
        this.bucketPublicAccessBlock = bucketPublicAccessBlock;
    }
}

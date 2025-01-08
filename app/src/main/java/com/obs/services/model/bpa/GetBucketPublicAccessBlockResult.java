/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.bpa;

import com.obs.services.model.HeaderResponse;

public class GetBucketPublicAccessBlockResult extends HeaderResponse {
    private BucketPublicAccessBlock bucketPublicAccessBlock;

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

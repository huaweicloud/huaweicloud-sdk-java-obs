/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.bpa;

import com.obs.services.model.HeaderResponse;

public class GetBucketPublicStatusResult extends HeaderResponse {
    public BucketPublicStatus getBucketPublicStatus() {
        return bucketPublicStatus;
    }

    public void setBucketPublicStatus(BucketPublicStatus bucketPublicStatus) {
        this.bucketPublicStatus = bucketPublicStatus;
    }

    private BucketPublicStatus bucketPublicStatus;
}

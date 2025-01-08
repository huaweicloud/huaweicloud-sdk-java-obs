/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.bpa;

import com.obs.services.model.HeaderResponse;

public class GetBucketPolicyPublicStatusResult extends HeaderResponse {
    private BucketPolicyStatus bucketPolicyStatus;

    public BucketPolicyStatus getPolicyStatus() {
        return bucketPolicyStatus;
    }

    public void setPolicyStatus(BucketPolicyStatus bucketPolicyStatus) {
        this.bucketPolicyStatus = bucketPolicyStatus;
    }
}

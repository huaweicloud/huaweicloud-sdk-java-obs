/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.trash;

import com.obs.services.model.HeaderResponse;

public class GetBucketTrashResult extends HeaderResponse {
    private BucketTrashConfiguration bucketTrashConfiguration;

    public BucketTrashConfiguration getTrashConfiguration() {
        return bucketTrashConfiguration;
    }

    public void setTrashConfiguration(BucketTrashConfiguration bucketTrashConfiguration) {
        this.bucketTrashConfiguration = bucketTrashConfiguration;
    }
}

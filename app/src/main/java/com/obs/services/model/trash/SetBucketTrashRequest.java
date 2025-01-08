/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.trash;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

public class SetBucketTrashRequest  extends BaseBucketRequest {
    {
        httpMethod = HttpMethodEnum.PUT;
    }
    private BucketTrashConfiguration bucketTrashConfiguration;

    public SetBucketTrashRequest(String bucketName, BucketTrashConfiguration bucketTrashConfiguration) {
        super(bucketName);
        this.bucketTrashConfiguration = bucketTrashConfiguration;
    }

    public BucketTrashConfiguration getTrashConfiguration() {
        return bucketTrashConfiguration;
    }

    public void setTrashConfiguration(BucketTrashConfiguration bucketTrashConfiguration) {
        this.bucketTrashConfiguration = bucketTrashConfiguration;
    }
}

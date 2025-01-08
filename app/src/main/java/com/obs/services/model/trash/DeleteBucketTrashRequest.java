/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.trash;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

public class DeleteBucketTrashRequest  extends BaseBucketRequest {
    {
        httpMethod = HttpMethodEnum.DELETE;
    }
}

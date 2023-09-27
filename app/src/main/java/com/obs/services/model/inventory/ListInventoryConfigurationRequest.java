package com.obs.services.model.inventory;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

public class ListInventoryConfigurationRequest extends BaseBucketRequest {

    {
        httpMethod = HttpMethodEnum.GET;
    }

    public ListInventoryConfigurationRequest(String bucketName) {
        super(bucketName);
    }
}

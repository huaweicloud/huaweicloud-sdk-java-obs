package com.obs.services.model.Qos;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

public class DeleteBucketQosRequest extends BaseBucketRequest {
    {
        httpMethod = HttpMethodEnum.DELETE;
    }

    public DeleteBucketQosRequest(String bucketName){
        super(bucketName);
    }
}



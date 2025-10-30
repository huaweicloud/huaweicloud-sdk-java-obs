package com.obs.services.model.Qos;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

public class GetBucketQoSRequest extends BaseBucketRequest {
    {
        httpMethod = HttpMethodEnum.GET;
    }

    public GetBucketQoSRequest(String bucketName){
        super(bucketName);
    }
}

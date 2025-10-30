package com.obs.services.model.Qos;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

public class SetBucketQosRequest extends BaseBucketRequest {
    {
        httpMethod = HttpMethodEnum.PUT;
    }
    private QosConfiguration qosConfig;
    public SetBucketQosRequest(String bucketName,QosConfiguration qosConfig){
        super(bucketName);
        this.qosConfig = qosConfig;
    }

    public QosConfiguration getQosConfig() {
        return qosConfig;
    }

    public void setQosConfiguration(QosConfiguration qosConfig){
        this.qosConfig = qosConfig;
    }
}

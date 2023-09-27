package com.obs.services.model.inventory;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

public class GetInventoryConfigurationRequest extends BaseBucketRequest {

    {
        httpMethod = HttpMethodEnum.GET;
    }

    public String getConfigurationId() {
        if(configurationId == null) {
            configurationId = "";
        }
        return configurationId;
    }

    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }

    public GetInventoryConfigurationRequest(String bucketName, String configurationId) {
        super(bucketName);
        this.configurationId = configurationId;
    }

    protected String configurationId;
}

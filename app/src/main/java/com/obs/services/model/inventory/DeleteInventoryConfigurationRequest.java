package com.obs.services.model.inventory;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

public class DeleteInventoryConfigurationRequest extends BaseBucketRequest {
    {
        httpMethod = HttpMethodEnum.DELETE;
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

    public DeleteInventoryConfigurationRequest(String bucketName, String configurationId) {
        super(bucketName);
        this.configurationId = configurationId;
    }

    protected String configurationId;
}

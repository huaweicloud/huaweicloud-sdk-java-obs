package com.obs.services.model.inventory;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

import java.util.ArrayList;

public class SetInventoryConfigurationRequest extends BaseBucketRequest {

    {
        httpMethod = HttpMethodEnum.PUT;
    }

    protected InventoryConfiguration inventoryConfiguration;

    public InventoryConfiguration getInventoryConfiguration() {
        if(inventoryConfiguration == null) {
            inventoryConfiguration = new InventoryConfiguration();
        }
        return inventoryConfiguration;
    }

    public void setInventoryConfiguration(InventoryConfiguration inventoryConfiguration) {
        this.inventoryConfiguration = inventoryConfiguration;
    }

    public SetInventoryConfigurationRequest(String bucketName, InventoryConfiguration inventoryConfiguration) {
        super(bucketName);
        this.inventoryConfiguration = inventoryConfiguration;
    }
}

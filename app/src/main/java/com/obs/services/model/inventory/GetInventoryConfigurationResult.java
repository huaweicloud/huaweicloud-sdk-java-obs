package com.obs.services.model.inventory;

import com.obs.services.model.HeaderResponse;

public class GetInventoryConfigurationResult extends HeaderResponse {
    protected InventoryConfiguration inventoryConfiguration;

    public InventoryConfiguration getInventoryConfiguration() {
        if (inventoryConfiguration == null) {
            this.inventoryConfiguration = new InventoryConfiguration();
        }
        return inventoryConfiguration;
    }

    public void setInventoryConfiguration(InventoryConfiguration inventoryConfiguration) {
        this.inventoryConfiguration = inventoryConfiguration;
    }
}

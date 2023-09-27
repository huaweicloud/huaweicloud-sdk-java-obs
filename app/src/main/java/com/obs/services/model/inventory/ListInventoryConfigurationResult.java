package com.obs.services.model.inventory;

import com.obs.services.model.HeaderResponse;

import java.util.List;
import java.util.ArrayList;

public class ListInventoryConfigurationResult extends HeaderResponse {
    protected List<InventoryConfiguration> inventoryConfigurations;

    public List<InventoryConfiguration> getInventoryConfigurations() {
        if(inventoryConfigurations == null) {
            inventoryConfigurations = new ArrayList<>();
        }
        return inventoryConfigurations;
    }

    public void setInventoryConfigurations(List<InventoryConfiguration> inventoryConfigurations) {
        this.inventoryConfigurations = inventoryConfigurations;
    }
}

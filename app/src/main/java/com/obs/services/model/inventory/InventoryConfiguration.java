package com.obs.services.model.inventory;

import java.util.ArrayList;
import java.util.Objects;

public class InventoryConfiguration {
    public String getConfigurationId() {
        if(configurationId == null) {
            configurationId = "";
        }
        return configurationId;
    }

    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }

    public Boolean getEnabled() {
        if(isEnabled == null) {
            isEnabled = true;
        }
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public String getObjectPrefix() {
        if(objectPrefix == null) {
            objectPrefix = "";
        }
        return objectPrefix;
    }

    public void setObjectPrefix(String objectPrefix) {
        this.objectPrefix = objectPrefix;
    }

    public String getFrequency() {
        if(frequency == null) {
            frequency = "";
        }
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getInventoryFormat() {
        if(inventoryFormat == null) {
            inventoryFormat = "";
        }
        return inventoryFormat;
    }

    public void setInventoryFormat(String inventoryFormat) {
        this.inventoryFormat = inventoryFormat;
    }

    public String getDestinationBucket() {
        if(destinationBucket == null) {
            destinationBucket = "";
        }
        return destinationBucket;
    }

    public void setDestinationBucket(String destinationBucket) {
        this.destinationBucket = destinationBucket;
    }

    public String getInventoryPrefix() {
        if(inventoryPrefix == null) {
            inventoryPrefix = "";
        }
        return inventoryPrefix;
    }

    public void setInventoryPrefix(String inventoryPrefix) {
        this.inventoryPrefix = inventoryPrefix;
    }

    public String getIncludedObjectVersions() {
        if(includedObjectVersions == null) {
            includedObjectVersions = "";
        }
        return includedObjectVersions;
    }

    public void setIncludedObjectVersions(String includedObjectVersions) {
        this.includedObjectVersions = includedObjectVersions;
    }

    public ArrayList<String> getOptionalFields() {
        if(optionalFields == null) {
            optionalFields = new ArrayList<>();
        }
        return optionalFields;
    }

    public void setOptionalFields(ArrayList<String> optionalFields) {
        this.optionalFields = optionalFields;
    }

    public InventoryConfiguration() {}
    public InventoryConfiguration(String configurationId, Boolean isEnabled, String frequency, String inventoryFormat, String destinationBucket, String includedObjectVersions) {
        this.configurationId = configurationId;
        this.isEnabled = isEnabled;
        this.frequency = frequency;
        this.inventoryFormat = inventoryFormat;
        this.destinationBucket = destinationBucket;
        this.includedObjectVersions = includedObjectVersions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(configurationId, isEnabled, objectPrefix, frequency, inventoryFormat, destinationBucket, inventoryPrefix, includedObjectVersions, optionalFields);
    }

    @Override
    public boolean equals(Object that) {
        if(this == that) {
            return true;
        }else if(that instanceof InventoryConfiguration) {
            InventoryConfiguration thatConfig = (InventoryConfiguration)that;
            return configurationId.equals(thatConfig.getConfigurationId())
                    && isEnabled.equals(thatConfig.getEnabled())
                    && objectPrefix.equals(thatConfig.getObjectPrefix())
                    && frequency.equals(thatConfig.getFrequency())
                    && inventoryFormat.equals(thatConfig.getInventoryFormat())
                    && destinationBucket.equals(thatConfig.getDestinationBucket())
                    && inventoryPrefix.equals(thatConfig.getInventoryPrefix())
                    && includedObjectVersions.equals(thatConfig.getIncludedObjectVersions())
                    && optionalFields.equals(thatConfig.getOptionalFields());

        }else {
            return false;
        }
    }

    protected String configurationId;
    protected Boolean isEnabled;
    protected String objectPrefix;
    protected String frequency;
    protected String inventoryFormat;
    protected String destinationBucket;
    protected String inventoryPrefix;
    protected String includedObjectVersions;
    protected ArrayList<String> optionalFields;

    public static class FrequencyOptions {
        public static final String DAILY = "Daily";
        public static final String WEEKLY = "Weekly";
    }

    public static class InventoryFormatOptions {
        public static final String CSV = "CSV";
    }

    public static class IncludedObjectVersionsOptions {
        public static final String ALL = "All";
        public static final String CURRENT = "Current";
    }

    public static class OptionalFieldOptions {
        public static final String SIZE = "Size";
        public static final String LAST_MODIFIED_DATE = "LastModifiedDate";
        public static final String STORAGE_CLASS = "StorageClass";
        public static final String ETAG = "ETag";
        public static final String IS_MULTIPART_UPLOADED = "IsMultipartUploaded";
        public static final String REPLICATION_STATUS = "ReplicationStatus";
        public static final String ENCRYPTION_STATUS = "EncryptionStatus";
    }
}

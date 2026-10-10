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

    /**
     * Gets the inventory filter.
     * @return the inventory filter, or null if unset
     */
    public InventoryFilter getFilter() {
        return filter;
    }

    /**
     * Sets the inventory filter.
     * @param filter the inventory filter to set
     */
    public void setFilter(InventoryFilter filter) {
        this.filter = filter;
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
        return Objects.hash(configurationId, isEnabled, objectPrefix, frequency, inventoryFormat, destinationBucket, inventoryPrefix, includedObjectVersions, optionalFields, filter);
    }

    @Override
    public boolean equals(Object that) {
        if(this == that) {
            return true;
        }else if(that instanceof InventoryConfiguration) {
            InventoryConfiguration thatConfig = (InventoryConfiguration)that;
            return Objects.equals(configurationId, thatConfig.configurationId)
                    && Objects.equals(isEnabled, thatConfig.isEnabled)
                    && Objects.equals(objectPrefix, thatConfig.objectPrefix)
                    && Objects.equals(frequency, thatConfig.frequency)
                    && Objects.equals(inventoryFormat, thatConfig.inventoryFormat)
                    && Objects.equals(destinationBucket, thatConfig.destinationBucket)
                    && Objects.equals(inventoryPrefix, thatConfig.inventoryPrefix)
                    && Objects.equals(includedObjectVersions, thatConfig.includedObjectVersions)
                    && Objects.equals(optionalFields, thatConfig.optionalFields)
                    && Objects.equals(filter, thatConfig.filter);

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
    protected InventoryFilter filter;

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
        public static final String INTELLIGENT_TIERING_ACCESS_TIER = "IntelligentTieringAccessTier";
        public static final String BUCKET_KEY_STATUS = "BucketKeyStatus";
        public static final String USER_META = "UserMeta";
        public static final String CRC64 = "CRC64";
        public static final String OBJECT_TYPE = "ObjectType";
        public static final String LAST_ACCESS_TIME = "LastAccessTime";
    }

    /**
     * Represents the And operator within an inventory filter, combining multiple filter conditions.
     */
    public static class FilterAndOperator {
        protected String prefix;
        protected Boolean isLatest;
        protected Boolean deleteMarker;

        /**
         * Gets the prefix for the And operator.
         * @return the prefix, never null (returns empty string if unset)
         */
        public String getPrefix() {
            if (prefix == null) {
                prefix = "";
            }
            return prefix;
        }

        /**
         * Sets the prefix for the And operator.
         * @param prefix the prefix to set
         */
        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        /**
         * Gets whether to filter for the latest version of objects.
         * @return the isLatest flag, or null if unset
         */
        public Boolean getIsLatest() {
            return isLatest;
        }

        /**
         * Sets whether to filter for the latest version of objects.
         * @param isLatest the isLatest flag to set
         */
        public void setIsLatest(Boolean isLatest) {
            this.isLatest = isLatest;
        }

        /**
         * Gets whether to filter for delete markers.
         * @return the deleteMarker flag, or null if unset
         */
        public Boolean getDeleteMarker() {
            return deleteMarker;
        }

        /**
         * Sets whether to filter for delete markers.
         * @param deleteMarker the deleteMarker flag to set
         */
        public void setDeleteMarker(Boolean deleteMarker) {
            this.deleteMarker = deleteMarker;
        }

        @Override
        public int hashCode() {
            return Objects.hash(prefix, isLatest, deleteMarker);
        }

        @Override
        public boolean equals(Object that) {
            if (this == that) {
                return true;
            } else if (that instanceof FilterAndOperator) {
                FilterAndOperator thatOp = (FilterAndOperator) that;
                return Objects.equals(prefix, thatOp.prefix)
                        && Objects.equals(isLatest, thatOp.isLatest)
                        && Objects.equals(deleteMarker, thatOp.deleteMarker);
            } else {
                return false;
            }
        }
    }

    /**
     * Represents the filter criteria for an inventory configuration, supporting
     * prefix, IsLatest, DeleteMarker, and And operator sub-elements.
     */
    public static class InventoryFilter {
        protected String prefix;
        protected Boolean isLatest;
        protected Boolean deleteMarker;
        protected FilterAndOperator andOperator;

        /**
         * Gets the prefix filter.
         * @return the prefix, never null (returns empty string if unset)
         */
        public String getPrefix() {
            if (prefix == null) {
                prefix = "";
            }
            return prefix;
        }

        /**
         * Sets the prefix filter.
         * @param prefix the prefix to set
         */
        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        /**
         * Gets whether to filter for the latest version of objects.
         * @return the isLatest flag, or null if unset
         */
        public Boolean getIsLatest() {
            return isLatest;
        }

        /**
         * Sets whether to filter for the latest version of objects.
         * @param isLatest the isLatest flag to set
         */
        public void setIsLatest(Boolean isLatest) {
            this.isLatest = isLatest;
        }

        /**
         * Gets whether to filter for delete markers.
         * @return the deleteMarker flag, or null if unset
         */
        public Boolean getDeleteMarker() {
            return deleteMarker;
        }

        /**
         * Sets whether to filter for delete markers.
         * @param deleteMarker the deleteMarker flag to set
         */
        public void setDeleteMarker(Boolean deleteMarker) {
            this.deleteMarker = deleteMarker;
        }

        /**
         * Gets the And operator for multi-condition filtering.
         * @return the And operator, or null if unset
         */
        public FilterAndOperator getAndOperator() {
            return andOperator;
        }

        /**
         * Sets the And operator for multi-condition filtering.
         * @param andOperator the And operator to set
         */
        public void setAndOperator(FilterAndOperator andOperator) {
            this.andOperator = andOperator;
        }

        @Override
        public int hashCode() {
            return Objects.hash(prefix, isLatest, deleteMarker, andOperator);
        }

        @Override
        public boolean equals(Object that) {
            if (this == that) {
                return true;
            } else if (that instanceof InventoryFilter) {
                InventoryFilter thatFilter = (InventoryFilter) that;
                return Objects.equals(prefix, thatFilter.prefix)
                        && Objects.equals(isLatest, thatFilter.isLatest)
                        && Objects.equals(deleteMarker, thatFilter.deleteMarker)
                        && Objects.equals(andOperator, thatFilter.andOperator);
            } else {
                return false;
            }
        }
    }
}

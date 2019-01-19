package com.obs.services.internal;

import java.io.Serializable;
import java.util.Properties;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;

public class ObsProperties implements Serializable {

    private static final long serialVersionUID = -822234326095333142L;

    private static final ILogger log = LoggerBuilder.getLogger(ObsProperties.class);

    private final Properties properties = new Properties();


    public void setProperty(String propertyName, String propertyValue) {
        if (propertyValue == null) {
            this.clearProperty(propertyName);
        } else {
            this.properties.put(propertyName, trim(propertyValue));
        }
    }

    public void clearProperty(String propertyName) {
        this.properties.remove(propertyName);
    }

    public void clearAllProperties() {
        this.properties.clear();
    }

    public String getStringProperty(String propertyName, String defaultValue) {
        String stringValue = trim(properties.getProperty(propertyName, defaultValue));
        if (log.isDebugEnabled()) {
            log.debug(propertyName + "=" + stringValue);
        }
        return stringValue;
    }

    public long getLongProperty(String propertyName, long defaultValue)
        throws NumberFormatException
    {
        String longValue = trim(properties.getProperty(propertyName, String.valueOf(defaultValue)));
        if (log.isDebugEnabled()) {
            log.debug(propertyName + "=" + longValue);
        }
        return Long.parseLong(longValue);
    }

    public int getIntProperty(String propertyName, int defaultValue)
        throws NumberFormatException
    {
        String intValue = trim(properties.getProperty(propertyName, String.valueOf(defaultValue)));
        if (log.isDebugEnabled()) {
            log.debug(propertyName + "=" + intValue);
        }
        return Integer.parseInt(intValue);
    }

    public boolean getBoolProperty(String propertyName, boolean defaultValue)
        throws IllegalArgumentException
    {
        String boolValue = trim(properties.getProperty(propertyName, String.valueOf(defaultValue)));
        if (log.isDebugEnabled()) {
            log.debug(propertyName + "=" + boolValue);
        }
        if ("true".equalsIgnoreCase(boolValue)) {
            return true;
        } else if ("false".equalsIgnoreCase(boolValue)) {
            return false;
        } else {
            throw new IllegalArgumentException("Boolean value '" + boolValue + "' for obs property '"
                + propertyName + "' must be 'true' or 'false' (case-insensitive)");
        }
    }

    public boolean containsKey(String propertyName) {
        return properties.containsKey(propertyName);
    }

    private static String trim(String str) {
        if (str != null) {
            return str.trim();
        } else {
            return null;
        }
    }

}

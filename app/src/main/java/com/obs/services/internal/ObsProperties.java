/**
 * 
 * JetS3t : Java S3 Toolkit
 * Project hosted at http://bitbucket.org/jmurty/jets3t/
 *
 * Copyright 2006-2010 James Murty
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
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

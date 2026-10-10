/**
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

package com.obs.services.model.accessmonitor;

/**
 * Access monitor configuration for a bucket.
 */
public class AccessMonitorConfiguration {

    /**
     * Root element name of the access monitor configuration XML.
     */
    public static final String ACCESS_MONITOR_CONFIGURATION = "AccessMonitorConfiguration";

    /**
     * Element name for the Status field.
     */
    public static final String STATUS = "Status";

    private AccessMonitorStatusEnum status;

    /**
     * Constructor
     */
    public AccessMonitorConfiguration() {
    }

    /**
     * Constructor
     *
     * @param status
     *            Access monitor status
     */
    public AccessMonitorConfiguration(AccessMonitorStatusEnum status) {
        this.status = status;
    }

    /**
     * Obtain the access monitor status.
     *
     * @return Access monitor status, or null if not set
     */
    public AccessMonitorStatusEnum getAccessMonitorStatus() {
        return status;
    }

    /**
     * Set the access monitor status.
     *
     * @param status
     *            Access monitor status
     */
    public void setAccessMonitorStatus(AccessMonitorStatusEnum status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AccessMonitorConfiguration{Status=" + (status != null ? status.getCode() : null) + '}';
    }
}

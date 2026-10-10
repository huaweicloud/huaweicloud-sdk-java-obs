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
 * Access monitor status enum.
 */
public enum AccessMonitorStatusEnum {

    /**
     * Access monitor is enabled.
     */
    ENABLED("Enabled"),

    /**
     * Access monitor is disabled.
     */
    DISABLED("Disabled");

    private String code;

    private AccessMonitorStatusEnum(String code) {
        this.code = code;
    }

    /**
     * Get the code of the access monitor status.
     *
     * @return Status code
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the AccessMonitorStatusEnum from the code string.
     *
     * @param code
     *            Status code string. Valid values: Enabled, Disabled
     * @return AccessMonitorStatusEnum, or null if the code does not match any status
     */
    public static AccessMonitorStatusEnum getValueFromCode(String code) {
        for (AccessMonitorStatusEnum val : AccessMonitorStatusEnum.values()) {
            if (val.code.equals(code)) {
                return val;
            }
        }
        return null;
    }
}

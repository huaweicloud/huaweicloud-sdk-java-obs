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

package com.obs.services.model;

import java.util.Arrays;

/**
 *
 * Keyword for replicating DeleteData. If the value is Enabled,
 * DeleteData that meet the rule are replicated.
 *
 */
public enum DeleteDataEnum {
    /**
     * Enable the rule.
     */
    ENABLED("Enabled"),

    /**
     * Disable the rule.
     */
    DISABLED("Disabled");

    private String code;

    private DeleteDataEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    public static DeleteDataEnum getValueFromCode(String code) {
        return Arrays.stream(DeleteDataEnum.values())
                .filter(val -> val.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}

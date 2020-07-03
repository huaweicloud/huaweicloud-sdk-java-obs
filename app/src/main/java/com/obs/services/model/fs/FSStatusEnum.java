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

package com.obs.services.model.fs;

/**
 * 桶的文件网关特性状态
 *
 */
public enum FSStatusEnum {

    /**
     * 启用文件网关特性
     */
    ENABLED("Enabled"),

    /**
     * 禁用文件网关特性
     */
    DISABLED("Disabled");

    private String code;

    private FSStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static FSStatusEnum getValueFromCode(String code) {
        for (FSStatusEnum val : FSStatusEnum.values()) {
            if (val.code.equals(code)) {
                return val;
            }
        }
        return null;
    }
}

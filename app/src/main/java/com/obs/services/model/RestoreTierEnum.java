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

/**
 * 取回选项
 *
 */
public enum RestoreTierEnum {
    /**
     * 快速取回，取回耗时1~5分钟
     */
    EXPEDITED("Expedited"), /**
                             * 标准取回，取回耗时3~5小时
                             */
    STANDARD("Standard"), /**
                           * 批量取回，取回耗时5~12小时
                           */
    @Deprecated BULK("Bulk");

    private String code;

    private RestoreTierEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RestoreTierEnum getValueFromCode(String code) {
        for (RestoreTierEnum val : RestoreTierEnum.values()) {
            if (val.code.equals(code)) {
                return val;
            }
        }
        return null;
    }
}

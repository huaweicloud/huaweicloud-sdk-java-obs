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
 * 桶的请求者付费状态
 *
 * @since 3.20.3
 */
public enum RequestPaymentEnum {

    /**
     * 由桶拥有者付费
     */
    BUCKET_OWNER("BucketOwner"),

    /**
     * 由请求者付费
     */
    REQUESTER("Requester");

    private String code;

    private RequestPaymentEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RequestPaymentEnum getValueFromCode(String code) {
        for (RequestPaymentEnum val : RequestPaymentEnum.values()) {
            if (val.code.equals(code)) {
                return val;
            }
        }
        return null;
    }

}

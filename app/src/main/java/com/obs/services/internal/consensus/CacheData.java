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
package com.obs.services.internal.consensus;

import com.obs.services.model.AuthTypeEnum;

import java.util.Date;

/**
 * 协议协商的缓存数据
 */
public class CacheData {
    private static final long VALID_PERIOD = (15 + (int)(5 * Math.random())) * 60 * 1000;

    private AuthTypeEnum apiVersion;
    private long expirationTime;

    public CacheData(AuthTypeEnum apiVersion) {
        this.apiVersion = apiVersion;
        this.expirationTime = new Date().getTime() + VALID_PERIOD;
    }

    public AuthTypeEnum getApiVersion() {
        return apiVersion;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

}

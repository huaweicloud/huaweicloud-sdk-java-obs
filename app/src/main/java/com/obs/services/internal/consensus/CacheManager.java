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
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存管理器
 */
public class CacheManager {
    private ConcurrentHashMap<String, CacheData> apiVersionCache = new ConcurrentHashMap<String, CacheData>();

    public CacheManager() {}

    private static class SingletonHolder {
        private static final CacheManager INSTANCE = new CacheManager();
    }

    public static CacheManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void addApiVersion(String bucketName, AuthTypeEnum apiVersion) {
        apiVersionCache.put(bucketName, new CacheData(apiVersion));
    }

    public AuthTypeEnum getApiVersionInCache(String bucketName) {
        CacheData authTypeCache = apiVersionCache.get(bucketName);
        return isValid(authTypeCache) ? authTypeCache.getApiVersion() : null;
    }

    public void clear() {
        if (apiVersionCache != null) {
            apiVersionCache.clear();
        }
    }

    public boolean isValid(CacheData authTypeCache) {
        return authTypeCache != null && authTypeCache.getExpirationTime() >= new Date().getTime();
    }
}

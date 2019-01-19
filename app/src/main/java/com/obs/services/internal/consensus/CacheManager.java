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

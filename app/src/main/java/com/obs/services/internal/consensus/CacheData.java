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

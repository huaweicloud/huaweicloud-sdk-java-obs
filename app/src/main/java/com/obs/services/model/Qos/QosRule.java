package com.obs.services.model.Qos;

public class QosRule {
    private NetworkType networkType;
    private long concurrentRequestLimit;
    private QpsLimitConfiguration qpsLimit;
    private BpsLimitConfiguration bpsLimit;
    private static void validateNonNegative(long value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " value cannot be negative: " + value);
        }
    }


    public QosRule(NetworkType networkType, long concurrentRequestLimitValue, QpsLimitConfiguration qpsLimit, BpsLimitConfiguration bpsLimit) {
        validateNonNegative(concurrentRequestLimitValue, "concurrentRequestLimit");

        this.networkType = networkType;
        this.concurrentRequestLimit = concurrentRequestLimitValue;
        this.qpsLimit = qpsLimit;
        this.bpsLimit = bpsLimit;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public long getConcurrentRequestLimit() {
        return concurrentRequestLimit;
    }

    public void setConcurrentRequestLimit(long concurrentRequestLimitValue) {
        validateNonNegative(concurrentRequestLimitValue, "concurrentRequestLimit");
        this.concurrentRequestLimit = concurrentRequestLimitValue;
    }

    public QpsLimitConfiguration getQpsLimit() {
        return qpsLimit;
    }

    public void setQpsLimit(QpsLimitConfiguration qpsLimit) {
        this.qpsLimit = qpsLimit;
    }

    public BpsLimitConfiguration getBpsLimit() {
        return bpsLimit;
    }

    public void setBpsLimit(BpsLimitConfiguration bpsLimit) {
        this.bpsLimit = bpsLimit;
    }
}

package com.obs.services.model.Qos;

public class BpsLimitConfiguration {
    private long get;
    private long putPost;
    private long total;

    // 构造方法
    public BpsLimitConfiguration(long getValue, long putPostValue, long totalValue) {
        validateNonNegative(getValue, "get");
        validateNonNegative(putPostValue, "putPost");
        validateNonNegative(totalValue, "total");

        this.get = getValue;
        this.putPost = putPostValue;
        this.total = totalValue;
    }

    private static void validateNonNegative(long value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " value cannot be negative: " + value);
        }
    }

    // Getter 和 Setter 方法

    public long getBpsGetLimit() {
        return get;
    }

    public void setBpsGetLimit(long getValue) {
        validateNonNegative(getValue, "get");
        this.get = getValue;
    }

    public long getBpsPutPostLimit() {
        return putPost;
    }

    public void setBpsPutPostLimit(long putPostValue) {
        validateNonNegative(putPostValue, "putPost");
        this.putPost = putPostValue;
    }

    public long getBpsTotalLimit() {
        return total;
    }

    public void setBpsTotalLimit(long totalValue) {
        validateNonNegative(totalValue, "total");
        this.total = totalValue;
    }
}
package com.obs.services.model.Qos;

public class QpsLimitConfiguration {
    private long get;
    private long putPostDelete;
    private long list;
    private long total;

    private static void validateNonNegative(long value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " value cannot be negative: " + value);
        }
    }

    public QpsLimitConfiguration(long getValue, long putPostDeleteValue, long listValue, long totalValue) {
        validateNonNegative(getValue, "get");
        validateNonNegative(putPostDeleteValue, "putPostDelete");
        validateNonNegative(listValue, "list");
        validateNonNegative(totalValue, "total");

        this.get = getValue;
        this.putPostDelete = putPostDeleteValue;
        this.list = listValue;
        this.total = totalValue;
    }


    public long getQpsGetLimit() {
        return get;
    }

    public void setQpsGetLimit(long getValue) {
        validateNonNegative(getValue, "get");
        this.get = getValue;
    }

    public long getQpsPutPostDeleteLimit() {
        return putPostDelete;
    }

    public void setQpsPutPostDeleteLimit(long putPostDeleteValue) {
        validateNonNegative(putPostDeleteValue, "putPostDelete");
        this.putPostDelete = putPostDeleteValue;
    }

    public long getQpsListLimit() {
        return list;
    }

    public void setQpsListLimit(long listValue) {
        validateNonNegative(listValue, "list");
        this.list = listValue;
    }

    public long getQpsTotalLimit() {
        return total;
    }

    public void setQpsTotalLimit(long totalValue) {
        validateNonNegative(totalValue, "total");
        this.total = totalValue;
    }
}
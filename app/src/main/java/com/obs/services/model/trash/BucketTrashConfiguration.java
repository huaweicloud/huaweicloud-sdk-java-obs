/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.trash;

public class BucketTrashConfiguration {
    private int reservedDays;

    public BucketTrashConfiguration(int reservedDays) {
        this.reservedDays = reservedDays;
    }

    public int getReservedDays() {
        return reservedDays;
    }

    public void setReservedDays(int reservedDays) {
        this.reservedDays = reservedDays;
    }
}

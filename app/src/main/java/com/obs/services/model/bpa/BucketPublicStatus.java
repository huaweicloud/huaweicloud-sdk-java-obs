/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.bpa;

public class BucketPublicStatus {
    private Boolean isPublic;

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    public final static String BUCKET_STATUS = "BucketStatus";
    public final static String IS_PUBLIC = "IsPublic";
}

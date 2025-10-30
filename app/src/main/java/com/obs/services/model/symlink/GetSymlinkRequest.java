/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package com.obs.services.model.symlink;

import com.obs.services.model.BaseObjectRequest;
import com.obs.services.model.HttpMethodEnum;

public class GetSymlinkRequest extends BaseObjectRequest {
    {
        httpMethod = HttpMethodEnum.GET;
    }

    private String versionId;

    public GetSymlinkRequest(String bucketName, String objectKey, String versionId) {
        super(bucketName, objectKey);
        this.versionId = versionId;
    }

    public GetSymlinkRequest(String bucketName, String objectKey){
        super(bucketName, objectKey);
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }
}

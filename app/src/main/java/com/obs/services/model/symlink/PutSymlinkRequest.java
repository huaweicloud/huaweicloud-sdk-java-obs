/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package com.obs.services.model.symlink;

import com.obs.services.model.AccessControlList;
import com.obs.services.model.BaseObjectRequest;
import com.obs.services.model.HttpMethodEnum;
import com.obs.services.model.ObjectMetadata;

public class PutSymlinkRequest extends BaseObjectRequest {
    {
        httpMethod = HttpMethodEnum.PUT;
    }
    private String symlinkTarget;
    private AccessControlList acl;
    private ObjectMetadata objectMetadata;

    public PutSymlinkRequest(String bucketName, String objectKey, String symlinkTarget, AccessControlList acl,
        ObjectMetadata objectMetadata) {
        super(bucketName, objectKey);
        this.symlinkTarget = symlinkTarget;
        this.acl = acl;
        this.objectMetadata = objectMetadata;
    }

    public PutSymlinkRequest(String bucketName, String objectKey, String symlinkTarget){
        super(bucketName,objectKey);
        this.symlinkTarget = symlinkTarget;
    }

    public String getSymlinkTarget() {
        return symlinkTarget;
    }

    public void setSymlinkTarget(String symlinkTarget) {
        this.symlinkTarget = symlinkTarget;
    }

    public AccessControlList getAcl() {
        return acl;
    }

    public void setAcl(AccessControlList acl) {
        this.acl = acl;
    }

    public ObjectMetadata getObjectMetadata() {
        return objectMetadata;
    }

    public void setObjectMetadata(ObjectMetadata objectMetadata) {
        this.objectMetadata = objectMetadata;
    }
}

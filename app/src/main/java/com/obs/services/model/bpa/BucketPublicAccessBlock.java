/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.model.bpa;

public class BucketPublicAccessBlock {
    private Boolean blockPublicACLs;
    private Boolean ignorePublicACLs;
    private Boolean blockPublicPolicy;
    private Boolean restrictPublicBuckets;

    @Override
    public String toString() {
        return "BucketPublicAccessBlock{" + "BlockPublicACLs=" + blockPublicACLs + ", IgnorePublicACLs="
            + ignorePublicACLs + ", BlockPublicPolicy=" + blockPublicPolicy + ", RestrictPublicBuckets="
            + restrictPublicBuckets + '}';
    }

    public Boolean getBlockPublicACLs() {
        return blockPublicACLs;
    }

    public void setBlockPublicACLs(Boolean blockPublicACLs) {
        this.blockPublicACLs = blockPublicACLs;
    }

    public Boolean getIgnorePublicACLs() {
        return ignorePublicACLs;
    }

    public void setIgnorePublicACLs(Boolean ignorePublicACLs) {
        this.ignorePublicACLs = ignorePublicACLs;
    }

    public Boolean getBlockPublicPolicy() {
        return blockPublicPolicy;
    }

    public void setBlockPublicPolicy(Boolean blockPublicPolicy) {
        this.blockPublicPolicy = blockPublicPolicy;
    }

    public Boolean getRestrictPublicBuckets() {
        return restrictPublicBuckets;
    }

    public void setRestrictPublicBuckets(Boolean restrictPublicBuckets) {
        this.restrictPublicBuckets = restrictPublicBuckets;
    }
    public final static String PUBLIC_ACCESS_BLOCK_CONFIGURATION = "PublicAccessBlockConfiguration";
    public final static String BLOCK_PUBLIC_ACLS = "BlockPublicAcls";
    public final static String IGNORE_PUBLIC_ACLS = "IgnorePublicAcls";
    public final static String BLOCK_PUBLIC_POLICY = "BlockPublicPolicy";
    public final static String RESTRICT_PUBLIC_BUCKETS = "RestrictPublicBuckets";
}

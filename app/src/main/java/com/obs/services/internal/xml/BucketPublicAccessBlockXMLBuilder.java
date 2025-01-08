/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.internal.xml;

import static com.obs.services.model.bpa.BucketPublicAccessBlock.BLOCK_PUBLIC_ACLS;
import static com.obs.services.model.bpa.BucketPublicAccessBlock.BLOCK_PUBLIC_POLICY;
import static com.obs.services.model.bpa.BucketPublicAccessBlock.IGNORE_PUBLIC_ACLS;
import static com.obs.services.model.bpa.BucketPublicAccessBlock.PUBLIC_ACCESS_BLOCK_CONFIGURATION;
import static com.obs.services.model.bpa.BucketPublicAccessBlock.RESTRICT_PUBLIC_BUCKETS;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.exception.ObsException;
import com.obs.services.model.bpa.BucketPublicAccessBlock;

public class BucketPublicAccessBlockXMLBuilder extends ObsSimpleXMLBuilder {
    private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.ObsClient");
    public String buildXML(BucketPublicAccessBlock bucketPublicAccessBlock) throws ObsException {
        checkBucketPublicAccessBlock(bucketPublicAccessBlock);
        startElement(PUBLIC_ACCESS_BLOCK_CONFIGURATION);
        if (bucketPublicAccessBlock.getBlockPublicACLs() != null) {
            startElement(BLOCK_PUBLIC_ACLS);
            append(bucketPublicAccessBlock.getBlockPublicACLs() ? "true" : "false");
            endElement(BLOCK_PUBLIC_ACLS);
        }
        if (bucketPublicAccessBlock.getIgnorePublicACLs() != null) {
            startElement(IGNORE_PUBLIC_ACLS);
            append(bucketPublicAccessBlock.getIgnorePublicACLs() ? "true" : "false");
            endElement(IGNORE_PUBLIC_ACLS);
        }
        if (bucketPublicAccessBlock.getBlockPublicPolicy() != null) {
            startElement(BLOCK_PUBLIC_POLICY);
            append(bucketPublicAccessBlock.getBlockPublicPolicy() ? "true" : "false");
            endElement(BLOCK_PUBLIC_POLICY);
        }
        if (bucketPublicAccessBlock.getRestrictPublicBuckets() != null) {
            startElement(RESTRICT_PUBLIC_BUCKETS);
            append(bucketPublicAccessBlock.getRestrictPublicBuckets() ? "true" : "false");
            endElement(RESTRICT_PUBLIC_BUCKETS);
        }
        endElement(PUBLIC_ACCESS_BLOCK_CONFIGURATION);
        return getXmlBuilder().toString();
    }
    protected void checkBucketPublicAccessBlock(BucketPublicAccessBlock bucketPublicAccessBlock) {
        if (bucketPublicAccessBlock == null) {
            String errorMessage = "bucketPublicAccessBlock is null, failed to build request XML!";
            log.error(errorMessage);
            throw new ObsException(errorMessage);
        } else if (bucketPublicAccessBlock.getBlockPublicPolicy() == null &&
            bucketPublicAccessBlock.getBlockPublicACLs() == null &&
            bucketPublicAccessBlock.getRestrictPublicBuckets() == null &&
            bucketPublicAccessBlock.getIgnorePublicACLs() == null) {
            String errorMessage = "bucketPublicAccessBlock's members are all null"
                + "(BlockPublicPolicy, BlockPublicACLs, RestrictPublicBuckets, IgnorePublicACLs), failed to build request XML!";
            log.error(errorMessage);
            throw new ObsException(errorMessage);
        }
    }

}

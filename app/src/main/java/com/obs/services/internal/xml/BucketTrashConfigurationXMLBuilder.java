/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.internal.xml;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.exception.ObsException;
import com.obs.services.model.trash.BucketTrashConfiguration;

public class BucketTrashConfigurationXMLBuilder extends ObsSimpleXMLBuilder {
    private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.ObsClient");
    private final static String BUCKET_TRASH_CONFIGURATION = "BucketTrashConfiguration";
    public final static String RESERVED_DAYS = "ReservedDays";
    public String buildXML(BucketTrashConfiguration bucketTrashConfiguration) {
        checkBucketPublicAccessBlock(bucketTrashConfiguration);
        startElement(BUCKET_TRASH_CONFIGURATION);
        startElement(RESERVED_DAYS);
        append(bucketTrashConfiguration.getReservedDays());
        endElement(RESERVED_DAYS);
        endElement(BUCKET_TRASH_CONFIGURATION);
        return getXmlBuilder().toString();
    }
    protected void checkBucketPublicAccessBlock(BucketTrashConfiguration bucketTrashConfiguration) {
        if (bucketTrashConfiguration == null) {
            String errorMessage = "bucketTrashConfiguration is null, failed to build request XML!";
            log.error(errorMessage);
            throw new ObsException(errorMessage);
        }
    }
}

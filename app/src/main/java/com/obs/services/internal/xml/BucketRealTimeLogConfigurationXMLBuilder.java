/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.internal.xml;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.realtimelog.BucketRealTimeLogConfiguration;

public class BucketRealTimeLogConfigurationXMLBuilder extends ObsSimpleXMLBuilder {
    private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.ObsClient");
    private final static String REALTIME_LOG_CONFIGURATION = "RealTimeLogConfiguration";
    public final static String LOG_GROUP_ID = "LogGroupId";
    public final static String LOG_STREAM_ID = "LogStreamId";
    public final static String PROJECT_ID = "ProjectId";

    public String buildXML(BucketRealTimeLogConfiguration configuration) {
        checkConfigurationNotNull(configuration);
        getXmlBuilder().append("<RealTimeLogConfiguration xmlns=\"http://obs.myhuaweicloud.com/doc/2015-06-30/\">");
        startElement(LOG_GROUP_ID);
        append(ServiceUtils.escapeXml11(configuration.getLogGroupId() != null ? configuration.getLogGroupId() : ""));
        endElement(LOG_GROUP_ID);
        startElement(LOG_STREAM_ID);
        append(ServiceUtils.escapeXml11(configuration.getLogStreamId() != null ? configuration.getLogStreamId() : ""));
        endElement(LOG_STREAM_ID);
        startElement(PROJECT_ID);
        append(ServiceUtils.escapeXml11(configuration.getProjectId() != null ? configuration.getProjectId() : ""));
        endElement(PROJECT_ID);
        endElement(REALTIME_LOG_CONFIGURATION);
        return getXmlBuilder().toString();
    }

    protected void checkConfigurationNotNull(BucketRealTimeLogConfiguration configuration) {
        if (configuration == null) {
            String errorMessage = "bucketRealTimeLogConfiguration is null, failed to build request XML!";
            log.error(errorMessage);
            throw new ObsException(errorMessage);
        }
    }
}

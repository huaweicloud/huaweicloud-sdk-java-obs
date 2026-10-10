/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.obs.services.internal.xml;

import static com.obs.services.model.accessmonitor.AccessMonitorConfiguration.ACCESS_MONITOR_CONFIGURATION;
import static com.obs.services.model.accessmonitor.AccessMonitorConfiguration.STATUS;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.accessmonitor.AccessMonitorConfiguration;

public class AccessMonitorXMLBuilder extends ObsSimpleXMLBuilder {
    private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.ObsClient");

    /**
     * Build the XML request body for setting the access monitor configuration.
     *
     * @param accessMonitorConfiguration
     *            Access monitor configuration
     * @return XML string
     * @throws ObsException
     *             If the configuration is null or status is not set
     */
    public String buildXML(AccessMonitorConfiguration accessMonitorConfiguration) throws ObsException {
        checkAccessMonitorConfiguration(accessMonitorConfiguration);
        startElement(ACCESS_MONITOR_CONFIGURATION);
        startElement(STATUS);
        append(ServiceUtils.escapeXml11(accessMonitorConfiguration.getAccessMonitorStatus().getCode()));
        endElement(STATUS);
        endElement(ACCESS_MONITOR_CONFIGURATION);
        return getXmlBuilder().toString();
    }

    private void checkAccessMonitorConfiguration(AccessMonitorConfiguration accessMonitorConfiguration) {
        if (accessMonitorConfiguration == null) {
            String errorMessage = "accessMonitorConfiguration is null, failed to build request XML!";
            log.error(errorMessage);
            throw new ObsException(errorMessage);
        } else if (accessMonitorConfiguration.getAccessMonitorStatus() == null) {
            String errorMessage = "accessMonitorConfiguration's status is null, failed to build request XML!";
            log.error(errorMessage);
            throw new ObsException(errorMessage);
        }
    }
}

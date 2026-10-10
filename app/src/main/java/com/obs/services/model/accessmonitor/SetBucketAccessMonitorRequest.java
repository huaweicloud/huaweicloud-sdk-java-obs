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

package com.obs.services.model.accessmonitor;

import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.HttpMethodEnum;

/**
 * Request parameters for setting the access monitor configuration of a bucket.
 */
public class SetBucketAccessMonitorRequest extends BaseBucketRequest {

    {
        httpMethod = HttpMethodEnum.PUT;
    }

    private AccessMonitorConfiguration accessMonitorConfiguration;

    /**
     * Constructor
     *
     * @param bucketName
     *            Bucket name
     * @param accessMonitorConfiguration
     *            Access monitor configuration
     */
    public SetBucketAccessMonitorRequest(String bucketName, AccessMonitorConfiguration accessMonitorConfiguration) {
        super(bucketName);
        this.accessMonitorConfiguration = accessMonitorConfiguration;
    }

    /**
     * Obtain the access monitor configuration.
     *
     * @return Access monitor configuration
     */
    public AccessMonitorConfiguration getAccessMonitorConfiguration() {
        if (accessMonitorConfiguration == null) {
            accessMonitorConfiguration = new AccessMonitorConfiguration();
        }
        return accessMonitorConfiguration;
    }

    /**
     * Set the access monitor configuration.
     *
     * @param accessMonitorConfiguration
     *            Access monitor configuration
     */
    public void setAccessMonitorConfiguration(AccessMonitorConfiguration accessMonitorConfiguration) {
        this.accessMonitorConfiguration = accessMonitorConfiguration;
    }
}

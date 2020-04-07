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

package com.obs.services.model;

/**
 * Basic class of all requests, which encapsulates common parameters used by all requests.
 * 
 * @since 3.20.3
 */
public class GenericRequest {
    /**
     * If the requester-pays function is enabled, the requester pays for his/her operations on the bucket.
     */
    private boolean isRequesterPays;
    
    /**
     * If the requester is allowed to pay, true is returned. Otherwise, false is returned.
     *
     * <p>
     * If the requester-pays function is enabled for a bucket, this attribute must be set to true when the bucket is requested by a requester other than the bucket owner. Otherwise, status code 403 is returned.
     *
     * <p>
     * After the requester-pays function is enabled, anonymous access to the bucket is not allowed.
     *
     * @return If the requester is allowed to pay, true is returned. Otherwise, false is returned.
     */
    public boolean isRequesterPays() {
        return isRequesterPays;
    }

    /**
     * Used to configure whether to enable the requester-pays function.
     *
     * <p>
     * If the requester-pays function is enabled for a bucket, this attribute must be set to true when the bucket is requested by a requester other than the bucket owner. Otherwise, status code 403 is returned.
     *
     * <p>
     * After the requester-pays function is enabled, anonymous access to the bucket is not allowed.
     *
     * @param isRequesterPays True indicates to enable the requester-pays function. False indicates to disable the requester-pays function.
     */
    public void setRequesterPays(boolean isRequesterPays) {
        this.isRequesterPays = isRequesterPays;
    }

    @Override
    public String toString() {
        return "GenericRequest [isRequesterPays=" + isRequesterPays + "]";
    }
}

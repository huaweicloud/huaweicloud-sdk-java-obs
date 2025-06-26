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

import com.obs.services.internal.utils.CRC64;

/**
 * Response to a file download request
 */
public class DownloadFileResult {
    private CRC64 crc64Combined;

    /**
     * Obtain object properties.
     * 
     * @return Object properties
     */
    public ObjectMetadata getObjectMetadata() {
        return objectMetadata;
    }

    /**
     * Set object properties.
     * 
     * @param objectMetadata
     *            Object properties
     */
    public void setObjectMetadata(ObjectMetadata objectMetadata) {
        this.objectMetadata = objectMetadata;
    }

    private ObjectMetadata objectMetadata;


    public CRC64 getCombinedCRC64() {
        return crc64Combined;
    }
    public void setCombinedCRC64(CRC64 crc64Combined) {
        this.crc64Combined = crc64Combined;
    }

    @Override
    public String toString() {
        return "DownloadFileResult [objectMetadata=" + objectMetadata + ". crc64Combined=" + crc64Combined + "]";
    }

}

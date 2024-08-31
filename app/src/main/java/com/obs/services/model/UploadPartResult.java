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
 * Response to a part upload request
 */
public class UploadPartResult extends HeaderResponse {
    private int partNumber;

    private String etag;

    private CRC64 clientCalculatedCRC64;

    /**
     * Obtain the part number.
     * 
     * @return Part number
     */
    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    /**
     * Obtain the ETag of the part.
     * 
     * @return Part ETag
     */
    public String getEtag() {
        return etag;
    }

    public void setEtag(String objEtag) {
        this.etag = objEtag;
    }


    public CRC64 getClientCalculatedCRC64() {
        return clientCalculatedCRC64;
    }

    public void setClientCalculatedCRC64(CRC64 clientCalculatedCRC64) {
        this.clientCalculatedCRC64 = clientCalculatedCRC64;
    }
    @Override
    public String toString() {
        return "UploadPartResult [partNumber=" + partNumber + ", etag=" + etag +
                ", clientCalculatedCRC64=" + clientCalculatedCRC64 + "]";
    }
}

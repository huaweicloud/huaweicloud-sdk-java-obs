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
**/

package com.obs.services.model;

import java.util.Date;

import com.obs.services.internal.utils.ServiceUtils;

/**
 * Response to a request for copying a part
 */
public class CopyPartResult extends HeaderResponse {
    private int partNumber;

    private String etag;

    private Date lastModified;
    private String crc64;
    private String crc32c;

    public CopyPartResult(int partNumber, String etag, Date lastModified, String crc64, String crc32c) {
        this.partNumber = partNumber;
        this.etag = etag;
        this.lastModified = ServiceUtils.cloneDateIgnoreNull(lastModified);
        this.crc64 = crc64;
        this.crc32c = crc32c;
    }

    /**
     * Obtain the part number of the to-be-copied part.
     * 
     * @return Part number
     */
    public int getPartNumber() {
        return partNumber;
    }

    /**
     * Obtain the ETag of the to-be-copied part.
     * 
     * @return ETag of the to-be-copied part
     */
    public String getEtag() {
        return etag;
    }

    /**
     * Obtain the crc64 of the copied part.
     *
     * @return crc64 of the copied part
     */
    public String getCrc64() {
        return crc64;
    }

    /**
     * Obtain the crc32c of the copied part.
     *
     * @return crc32c of the copied part
     */
    public String getCRC32C() {
        return crc32c;
    }

    /**
     * Obtain the last modification time of the to-be-copied part.
     * 
     * @return Last modification time of the to-be-copied part
     */
    public Date getLastModified() {
        return ServiceUtils.cloneDateIgnoreNull(this.lastModified);
    }

    @Override
    public String toString() {
        return "CopyPartResult [partNumber=" + partNumber + ", etag=" + etag + ", lastModified=" + lastModified
                + ", crc64=" + crc64 + ", crc32c=" + crc32c + "]";
    }

}

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

import java.util.Date;

import com.obs.services.internal.utils.ServiceUtils;

/**
 * Response to a request for copying an object
 */
public class CopyObjectResult extends HeaderResponse {
    private String etag;

    private String crc64;

    private String crc32c;

    private Date lastModified;

    private String versionId;

    private String copySourceVersionId;

    private StorageClassEnum storageClass;

    public CopyObjectResult(String etag, Date lastModified, String versionId, String copySourceVersionId,
            StorageClassEnum storageClass, String crc64, String crc32c) {
        this.etag = etag;
        this.lastModified = ServiceUtils.cloneDateIgnoreNull(lastModified);
        this.versionId = versionId;
        this.copySourceVersionId = copySourceVersionId;
        this.storageClass = storageClass;
        this.crc64 = crc64;
        this.crc32c = crc32c;
    }

    /**
     * Obtain the ETag of the destination object.
     * 
     * @return ETag value of the destination object
     */
    public String getEtag() {
        return etag;
    }

    /**
     * Last modification time of the destination object
     * 
     * @return Last modification time of the destination object
     */
    public Date getLastModified() {
        return ServiceUtils.cloneDateIgnoreNull(this.lastModified);
    }

    /**
     * Obtain the version ID of the destination object.
     * 
     * @return Version ID of the object
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * Obtain the version ID of the source object.
     * 
     * @return Version ID of the object
     */
    public String getCopySourceVersionId() {
        return copySourceVersionId;
    }

    /**
     * Obtain the storage class of the destination object.
     * 
     * @return Object storage class
     */
    public StorageClassEnum getObjectStorageClass() {
        return storageClass;
    }

    /**
     * Obtain the crc64 of the destination object.
     *
     * @return crc64 value of the destination object
     */
    public String getCRC64() {
        return crc64;
    }

    /**
     * Obtain the crc32c of the destination object.
     *
     * @return crc32c value of the destination object
     */
    public String getCRC32C() {
        return crc32c;
    }

    @Override
    public String toString() {
        return "CopyObjectResult [etag=" + etag + ", lastModified=" + lastModified + ", versionId=" + versionId
            + ", copySourceVersionId=" + copySourceVersionId + ", storageClass=" + storageClass+
            ", crc64=" + crc64 + ", crc32c=" + crc32c + "]";
    }

}

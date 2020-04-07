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
 * Request parameters for deleting an object.
 * 
 * @since 3.20.3
 */
public class DeleteObjectRequest extends GenericRequest {
    private String bucketName;

    private String objectKey;
    
    private String versionId;

    public DeleteObjectRequest() {

    }

    /**
     * Constructor
     * 
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     */
    public DeleteObjectRequest(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }
    
    /**
     * Constructor
     * 
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param versionId
     *            Object version ID
     */
    public DeleteObjectRequest(String bucketName, String objectKey, String versionId) {
        this(bucketName, objectKey);
        this.versionId = versionId;
    }

    /**
     * Obtain the version ID of the object to be deleted.
     * 
     * @return Version ID of the object to be deleted
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * Set the version ID of the object to be deleted.
       
     * @param versionId
     *            Version ID of the object to be deleted
     */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    /**
     * Obtain the name of the bucket to which the to-be-deleted object belongs.
     * 
     * @return Name of the bucket to which the to-be-deleted object belongs
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Set the name of the bucket to which the to-be-deleted object belongs.
     * 
     * @param bucketName
     *            Name of the bucket to which the to-be-deleted object belongs
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Obtain the name of the object to be deleted.
     * 
     * @return Name of the object to be deleted
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * Set the name of the object to be deleted.
     * 
     * @param objectKey
     *            Name of the object to be deleted
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    @Override
    public String toString() {
        return "AbortMultipartUploadRequest [bucketName=" + bucketName + ", objectKey="
                + objectKey + ", versionId=" + versionId + "]";
    }

}

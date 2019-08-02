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
package com.obs.services.model.fs;

/**
 * Request parameters for deleting objects
 */

public class DropFileRequest {
    
    private String bucketName;

    private String objectKey;

    private String versionId;

    public DropFileRequest() {
    }

    /**
     * Constructor
     * 
     * @param bucketName Bucket name
     * @param objectKey  Object name
     */
    public DropFileRequest(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    /**
     * Constructor
     * 
     * @param bucketName Bucket name
     * @param objectKey  Object name
     * @param versionId  Object version
     */
    public DropFileRequest(String bucketName, String objectKey, String versionId) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.versionId = versionId;
    }

    /**
     * Obtain the bucket name.
     * 
     * @return Bucket name
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Set the bucket name.
     * 
     * @param bucketName Bucket name
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Obtain the object name.
     * 
     * @return Object name
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * Set the object name.
     * 
     * @param objectKey Object name
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    /**
     * Obtain the object version ID.
     * 
     * @return Object version
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * Set the version ID of the object.
     * 
     * @param versionId
     */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    @Override
    public String toString() {
        return "DropFileRequest [bucketName=" + bucketName + ", objectKey=" + objectKey + ", versionId=" + versionId
                + "]";
    }
}

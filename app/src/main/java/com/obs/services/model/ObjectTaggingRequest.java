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
 * Request for setting tags for a object
 * 
 * @since 3.22.12
 *
 */
public class ObjectTaggingRequest extends BaseObjectRequest {

    {
        httpMethod = HttpMethodEnum.PUT;
    }

    protected String versionId;
    private ObjectTagResult objectTagResult;

    public ObjectTaggingRequest(String bucketName, String objectKey, String versionId, ObjectTagResult objectTagResult) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.versionId = versionId;
        this.objectTagResult = objectTagResult;
    }

    public ObjectTagResult getObjectTagInfo() {
        return objectTagResult;
    }

    public void setObjectTagging(ObjectTagResult objectTagResult) {
        this.objectTagResult = objectTagResult;
    }

    @Override
    public String toString() {
        return "ObjectTaggingRequest [ObjectTagResult=" + objectTagResult + ", getObjectKey()=" + getObjectKey()
                + ", getObjectVersionId()=" + getObjectVersionId() + ", isRequesterPays()=" + isRequesterPays() + "]";
    }

    public String getObjectVersionId() {
        return this.versionId;
    }
}
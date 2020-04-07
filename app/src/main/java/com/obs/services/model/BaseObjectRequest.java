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
 * 对象请求基础类
 * 
 * @since 3.20.3
 */
public class BaseObjectRequest extends GenericRequest {
    private String bucketName;
    private String objectKey;
    private String versionId;

    public BaseObjectRequest() {
    }

    public BaseObjectRequest(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    public BaseObjectRequest(String bucketName, String objectKey, String versionId) {
        this(bucketName, objectKey);
        this.versionId = versionId;
    }

    /**
     * 获取桶名
     * 
     * @return 桶名
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置桶名
     * 
     * @param bucketName
     *            桶名
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取对象名
     * 
     * @return 对象名
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * 设置对象名
     * 
     * @param objectKey
     *            对象名
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    /**
     * 获取对象版本号
     * 
     * @return 对象版本号
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * 设置对象版本号
     * 
     * @param versionId
     *            对象版本号
     */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    @Override
    public String toString() {
        return "BaseObjectRequest [bucketName=" + bucketName + ", objectKey=" + objectKey + ", versionId=" + versionId
                + ", isRequesterPays()=" + isRequesterPays() + "]";
    }
}

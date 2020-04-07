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
 * 获取对象访问权限的请求参数
 * 
 * @since 3.20.3
 */
public class GetObjectAclRequest extends GenericRequest {
    private String bucketName;

    private String objectKey;
    
    private String versionId;

    public GetObjectAclRequest() {

    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param versionId
     *            对象版本号
     */
    public GetObjectAclRequest(String bucketName, String objectKey, String versionId) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.versionId = versionId;
    }

    /**
     * 获取对象版本号
     * 
     * @return 对象的版本号
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * 设置对象的版本号
     * 
     * @param versionId
     *            对象的版本号
     */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    /**
     * 获取对象所属的桶名
     * 
     * @return 对象所属的桶名
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置对象所属的桶名
     * 
     * @param bucketName
     *            对象所属的桶名
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取对象的对象名
     * 
     * @return 对象的对象名
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * 设置对象的对象名
     * 
     * @param objectKey
     *            对象的对象名
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

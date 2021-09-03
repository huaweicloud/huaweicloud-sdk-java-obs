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
 * 分段上传任务
 */
public class MultipartUpload {
    private String uploadId;

    private String bucketName;

    private String objectKey;

    private Date initiatedDate;

    private StorageClassEnum storageClass;

    private Owner owner;

    private Owner initiator;

    public MultipartUpload(String uploadId, String objectKey, Date initiatedDate, StorageClassEnum storageClass,
            Owner owner, Owner initiator) {
        super();
        this.uploadId = uploadId;
        this.objectKey = objectKey;
        this.initiatedDate = ServiceUtils.cloneDateIgnoreNull(initiatedDate);
        this.storageClass = storageClass;
        this.owner = owner;
        this.initiator = initiator;
    }

    public MultipartUpload(String uploadId, String bucketName, String objectKey, Date initiatedDate,
            StorageClassEnum storageClass, Owner owner, Owner initiator) {
        super();
        this.uploadId = uploadId;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.initiatedDate = ServiceUtils.cloneDateIgnoreNull(initiatedDate);
        this.storageClass = storageClass;
        this.owner = owner;
        this.initiator = initiator;
    }

    /**
     * 获取分段上传任务的创建者
     * 
     * @return 获取分段上传任务的创建者
     */
    public Owner getInitiator() {
        return initiator;
    }

    /**
     * 获取分段上传任务的所有者
     * 
     * @return 分段上传任务的所有者
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * 获取分段上传任务的ID号
     * 
     * @return 分段上传任务的ID号
     */
    public String getUploadId() {
        return uploadId;
    }

    /**
     * 获取分段上传任务所属的桶名
     * 
     * @return 分段上传任务所属的桶名
     */
    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取分段上传任务所属的对象名
     * 
     * @return 分段上传任务所属的对象名
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * 获取分段上传任务最终生成对象的存储类别
     * 
     * @return 分段上传任务最终生成对象的存储类别
     */
    @Deprecated
    public String getStorageClass() {
        return storageClass != null ? storageClass.getCode() : null;
    }

    /**
     * 获取分段上传任务最终生成对象的存储类别
     * 
     * @return 分段上传任务最终生成对象的存储类别
     */
    public StorageClassEnum getObjectStorageClass() {
        return storageClass;
    }

    /**
     * 获取分段上传任务的创建时间
     * 
     * @return 分段上传任务的创建时间
     */
    public Date getInitiatedDate() {
        return ServiceUtils.cloneDateIgnoreNull(this.initiatedDate);
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

}

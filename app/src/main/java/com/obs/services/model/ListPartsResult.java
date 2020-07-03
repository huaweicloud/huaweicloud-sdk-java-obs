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

import java.util.ArrayList;
import java.util.List;

/**
 * 列举已上传的段的响应结果
 */
public class ListPartsResult extends HeaderResponse {
    private String bucket;

    private String key;

    private String uploadId;

    private Owner initiator;

    private Owner owner;

    private StorageClassEnum storageClass;

    private List<Multipart> multipartList;

    private Integer maxParts;

    private boolean isTruncated;

    private String partNumberMarker;

    private String nextPartNumberMarker;

    public ListPartsResult(String bucket, String key, String uploadId, Owner initiator, Owner owner,
            StorageClassEnum storageClass, List<Multipart> multipartList, Integer maxParts, boolean isTruncated,
            String partNumberMarker, String nextPartNumberMarker) {
        super();
        this.bucket = bucket;
        this.key = key;
        this.uploadId = uploadId;
        this.initiator = initiator;
        this.owner = owner;
        this.storageClass = storageClass;
        this.multipartList = multipartList;
        this.maxParts = maxParts;
        this.isTruncated = isTruncated;
        this.partNumberMarker = partNumberMarker;
        this.nextPartNumberMarker = nextPartNumberMarker;
    }

    /**
     * 获取分段上传任务所属的桶名
     * 
     * @return 分段上传任务所属的桶名
     */
    public String getBucket() {
        return bucket;
    }

    /**
     * 获取分段上传任务所属的对象名
     * 
     * @return 分段上传任务所属的对象名
     */
    public String getKey() {
        return key;
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
     * 获取分段上传任务的创建者
     * 
     * @return 分段上传任务的创建者
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
     * 获取分段上传任务最终对象的存储类型
     * 
     * @return 分段上传任务最终对象的存储类型
     */
    @Deprecated
    public String getStorageClass() {
        return this.storageClass == null ? null : this.storageClass.getCode();
    }

    /**
     * 获取分段上传任务最终对象的存储类型
     * 
     * @return 分段上传任务最终对象的存储类型
     */
    public StorageClassEnum getObjectStorageClass() {
        return storageClass;
    }

    /**
     * 获取列出已上传段的最大条目数
     * 
     * @return 列出已上传段的最大条目数
     */
    public Integer getMaxParts() {
        return maxParts;
    }

    /**
     * 获取已上传的段列表
     * 
     * @return 已上传的段列表
     */
    public List<Multipart> getMultipartList() {
        if (this.multipartList == null) {
            this.multipartList = new ArrayList<Multipart>();
        }
        return multipartList;
    }

    /**
     * 判断查询结果列表是否被截断。true表示截断，本次没有返回全部结果；false表示未截断，本次已经返回了全部结果。
     * 
     * @return 截断标识
     */
    public boolean isTruncated() {
        return isTruncated;
    }

    /**
     * 获取请求中待列出段的起始位置
     * 
     * @return 请求中待列出段的起始位置
     */
    public String getPartNumberMarker() {
        return partNumberMarker;
    }

    /**
     * 获取下次请求的起始位置
     * 
     * @return 下次请求的起始位置
     */
    public String getNextPartNumberMarker() {
        return nextPartNumberMarker;
    }

    @Override
    public String toString() {
        return "ListPartsResult [bucket=" + bucket + ", key=" + key + ", uploadId=" + uploadId + ", initiator="
                + initiator + ", owner=" + owner + ", storageClass=" + storageClass + ", multipartList=" + multipartList
                + ", maxParts=" + maxParts + ", isTruncated=" + isTruncated + ", partNumberMarker=" + partNumberMarker
                + ", nextPartNumberMarker=" + nextPartNumberMarker + "]";
    }

}

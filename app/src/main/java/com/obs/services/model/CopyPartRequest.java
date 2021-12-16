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
 * 复制段的请求参数
 */
public class CopyPartRequest extends AbstractMultipartRequest {

    {
        httpMethod = HttpMethodEnum.PUT;
    }

    private String sourceBucketName;

    private String sourceObjectKey;

    private String destinationBucketName;

    private String destinationObjectKey;

    private Long byteRangeStart;

    private Long byteRangeEnd;

    private SseCHeader sseCHeaderSource;

    private SseCHeader sseCHeaderDestination;

    private String versionId;

    private int partNumber;

    public CopyPartRequest() {
    }

    /**
     * 构造函数
     * 
     * @param uploadId
     *            分段上传任务的ID号
     * @param sourceBucketName
     *            源桶名
     * @param sourceObjectKey
     *            源对象名
     * @param destinationBucketName
     *            目标桶名
     * @param destinationObjectKey
     *            目标对象名
     * @param partNumber
     *            目标段的分段号
     */
    public CopyPartRequest(String uploadId, String sourceBucketName, String sourceObjectKey,
            String destinationBucketName, String destinationObjectKey, int partNumber) {
        this.uploadId = uploadId;
        this.sourceBucketName = sourceBucketName;
        this.sourceObjectKey = sourceObjectKey;
        this.destinationBucketName = destinationBucketName;
        this.destinationObjectKey = destinationObjectKey;
        this.partNumber = partNumber;
    }

    /**
     * 获取源对象SSE-C解密头域信息
     * 
     * @return 源对象SSE-C解密头域信息
     */
    public SseCHeader getSseCHeaderSource() {
        return sseCHeaderSource;
    }

    /**
     * 设置源对象SSE-C解密头域信息
     * 
     * @param sseCHeaderSource
     *            源对象SSE-C解密头域信息
     */
    public void setSseCHeaderSource(SseCHeader sseCHeaderSource) {
        this.sseCHeaderSource = sseCHeaderSource;
    }

    /**
     * 获取目标对象SSE-C加密头域信息
     * 
     * @return 目标对象SSE-C加密头域信息
     */
    public SseCHeader getSseCHeaderDestination() {
        return sseCHeaderDestination;
    }

    /**
     * 设置目标对象SSE-C加密头域信息
     * 
     * @param sseCHeaderDestination
     *            目标对象SSE-C加密头域信息
     */
    public void setSseCHeaderDestination(SseCHeader sseCHeaderDestination) {
        this.sseCHeaderDestination = sseCHeaderDestination;
    }

    /**
     * 获取复制的起始位置
     * 
     * @return 复制的起始位置
     */
    public Long getByteRangeStart() {
        return byteRangeStart;
    }

    /**
     * 设置复制的起始位置
     * 
     * @param byteRangeStart
     *            复制的起始位置
     * 
     */
    public void setByteRangeStart(Long byteRangeStart) {
        this.byteRangeStart = byteRangeStart;
    }

    /**
     * 获取复制的终止位置
     * 
     * @return 复制的终止位置
     */
    public Long getByteRangeEnd() {
        return byteRangeEnd;
    }

    /**
     * 设置复制的终止位置
     * 
     * @param byteRangeEnd
     *            复制的终止位置
     * 
     */
    public void setByteRangeEnd(Long byteRangeEnd) {
        this.byteRangeEnd = byteRangeEnd;
    }

    /**
     * 获取目标段的分段号
     * 
     * @return 目标段的分段号
     */
    public int getPartNumber() {
        return partNumber;
    }

    /**
     * 设置目标段的分段号
     * 
     * @param partNumber
     *            目标段的分段号
     * 
     */
    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    /**
     * 获取源桶名
     * 
     * @return 源桶名
     */
    public String getSourceBucketName() {
        return sourceBucketName;
    }

    /**
     * 设置源桶名
     * 
     * @param bucketName
     *            源桶名
     * 
     */
    public void setSourceBucketName(String bucketName) {
        this.sourceBucketName = bucketName;
    }

    /**
     * 获取源对象名
     * 
     * @return 源对象名
     */
    public String getSourceObjectKey() {
        return sourceObjectKey;
    }

    /**
     * 设置源对象名
     * 
     * @param objectKey
     *            源对象名
     * 
     */
    public void setSourceObjectKey(String objectKey) {
        this.sourceObjectKey = objectKey;
    }

    /**
     * 获取分段上传任务所属的桶名（目标桶名）
     * 
     * @return 分段上传任务所属的桶名
     */
    public String getDestinationBucketName() {
        return destinationBucketName;
    }

    /**
     * 设置分段上传任务所属的桶名（目标桶名）
     * 
     * @param destBucketName
     *            分段上传任务所属的桶名
     * 
     */
    public void setDestinationBucketName(String destBucketName) {
        this.destinationBucketName = destBucketName;
    }

    /**
     * 获取分段上传任务所属的对象名（目标对象名）
     * 
     * @return 分段上传任务所属的对象名
     * 
     */
    public String getDestinationObjectKey() {
        return destinationObjectKey;
    }

    /**
     * 设置分段上传任务所属的对象名（目标对象名）
     * 
     * @param destObjectKey
     *            分段上传任务所属的对象名
     * 
     */
    public void setDestinationObjectKey(String destObjectKey) {
        this.destinationObjectKey = destObjectKey;
    }

    /**
     * 获取源对象的版本号
     * 
     * @return 源对象版本号
     * 
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * 设置源对象的版本号
     * 
     * @param versionId
     *            源对象版本号
     * 
     */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    /**
     * 获取分段上传任务所属的桶名（目标桶名）
     *
     * @return 分段上传任务所属的桶名
     */
    @Override
    public String getBucketName() {
        return this.destinationBucketName;
    }

    /**
     * 设置分段上传任务所属的桶名（目标桶名）
     *
     * @param bucketName
     *            分段上传任务所属的桶名
     *
     */
    @Override
    public void setBucketName(String bucketName) {
        this.destinationBucketName = bucketName;
    }

    /**
     * 设置分段上传任务所属的对象名（目标对象名）
     *
     * @param objectKey
     *            分段上传任务所属的对象名
     *
     */
    @Override
    public void setObjectKey(String objectKey) {
        this.destinationObjectKey = objectKey;
    }

    /**
     * 获取分段上传任务所属的对象名（目标对象名）
     *
     * @return 分段上传任务所属的对象名
     *
     */
    @Override
    public String getObjectKey() {
        return this.destinationObjectKey;
    }

    @Override
    public String toString() {
        return "CopyPartRequest [uploadId=" + uploadId + ", sourceBucketName=" + sourceBucketName + ", sourceObjectKey="
                + sourceObjectKey + ", destinationBucketName=" + destinationBucketName + ", destinationObjectKey="
                + destinationObjectKey + ", byteRangeStart=" + byteRangeStart + ", byteRangeEnd=" + byteRangeEnd
                + ", sseCHeaderSource=" + sseCHeaderSource + ", sseCHeaderDestination=" + sseCHeaderDestination
                + ", versionId=" + versionId + ", partNumber=" + partNumber + "]";
    }

}

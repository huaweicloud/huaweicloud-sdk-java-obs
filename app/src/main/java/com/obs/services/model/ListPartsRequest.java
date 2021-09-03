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
 * 列举已上传的段的请求参数
 */
public class ListPartsRequest extends GenericRequest {
    private String bucketName;

    private String key;

    private String uploadId;

    private Integer maxParts;

    private Integer partNumberMarker;

    private String encodingType;

    public ListPartsRequest() {

    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            分段上传任务所属的桶名
     * @param key
     *            分段上传任务所属的对象名
     */
    public ListPartsRequest(String bucketName, String key) {
        this.bucketName = bucketName;
        this.key = key;
    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            分段上传任务所属的桶名
     * @param key
     *            分段上传任务所属的对象名
     * @param uploadId
     *            分段上传任务的ID号
     */
    public ListPartsRequest(String bucketName, String key, String uploadId) {
        this(bucketName, key);
        this.uploadId = uploadId;
    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            分段上传任务所属的桶名
     * @param key
     *            分段上传任务所属的对象名
     * @param uploadId
     *            分段上传任务的ID号
     * @param maxParts
     *            列举已上传的段返回结果最大段数目
     */
    public ListPartsRequest(String bucketName, String key, String uploadId, Integer maxParts) {
        this(bucketName, key, uploadId);
        this.maxParts = maxParts;
    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            分段上传任务所属的桶名
     * @param key
     *            分段上传任务所属的对象名
     * @param uploadId
     *            分段上传任务的ID号
     * @param maxParts
     *            列举已上传的段返回结果最大段数目
     * @param partNumberMarker
     *            待列出段的起始位置
     */
    public ListPartsRequest(String bucketName, String key, String uploadId, Integer maxParts,
                            Integer partNumberMarker) {
        this(bucketName, key, uploadId, maxParts);
        this.partNumberMarker = partNumberMarker;
    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            分段上传任务所属的桶名
     * @param key
     *            分段上传任务所属的对象名
     * @param uploadId
     *            分段上传任务的ID号
     * @param maxParts
     *            列举已上传的段返回结果最大段数目
     * @param partNumberMarker
     *            指定List的起始位置，只有Part Number数目大于该参数的Part会被列出
     * @param encodingType
     *            对响应中的 Key 进行指定类型的编码。如果 Key 包含xml 1.0标准不支持的控制字符，
     *            可通过设置 encoding-type 对响应中的Key进行编码，可选值 "url"
     */
    public ListPartsRequest(String bucketName, String key, String uploadId, Integer maxParts,
                            Integer partNumberMarker, String encodingType) {
        this(bucketName, key, uploadId, maxParts, partNumberMarker);
        this.encodingType = encodingType;
    }


    /**
     * 获取分段上传任务所属的桶名
     *
     * @return 分段上传任务所属的桶名
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置分段上传任务所属的桶名
     *
     * @param bucketName
     *            分段上传任务所属的桶名
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取分段上传任务所属的桶名
     *
     * @return 分段上传任务所属的桶名
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置分段上传任务所属的桶名
     *
     * @param key
     *            分段上传任务所属的桶名
     */
    public void setKey(String key) {
        this.key = key;
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
     * 设置分段上传任务的ID号
     *
     * @param uploadId
     *            分段上传任务的ID号
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    /**
     * 获取列举已上传的段返回结果最大段数目
     *
     * @return 列举已上传的段返回结果最大段数目
     */
    public Integer getMaxParts() {
        return maxParts;
    }

    /**
     * 设置列举已上传的段返回结果最大段数目
     *
     * @param maxParts
     *            列举已上传的段返回结果最大段数目
     */
    public void setMaxParts(Integer maxParts) {
        this.maxParts = maxParts;
    }

    /**
     * 获取待列出段的起始位置
     *
     * @return 待列出段的起始位置
     */
    public Integer getPartNumberMarker() {
        return partNumberMarker;
    }

    /**
     * 设置待列出段的起始位置
     *
     * @param partNumberMarker
     *            待列出段的起始位置
     */
    public void setPartNumberMarker(Integer partNumberMarker) {
        this.partNumberMarker = partNumberMarker;
    }

    /**
     * 设置 Key 编码格式，可选 "url"
     *
     * @param encodingType
     *             objectKey 编码格式
     */
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * 获取 Key 编码格式
     *
     * @return objectKey 编码所使用的格式
     */
    public String getEncodingType() {
        return encodingType;
    }

    @Override
    public String toString() {
        return "ListPartsRequest [bucketName=" + bucketName + ", key=" + key + ", uploadId=" + uploadId + ", maxParts="
                + maxParts + ", partNumberMarker=" + partNumberMarker + ", encodingType=" + encodingType + "]";
    }
}

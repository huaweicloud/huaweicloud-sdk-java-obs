/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.obs.services.model;

/**
 * 列举分段上传任务的请求参数
 */
public class ListMultipartUploadsRequest extends GenericRequest {
    private String bucketName;

    private String prefix;

    private String delimiter;

    private Integer maxUploads;

    private String keyMarker;

    private String uploadIdMarker;

    private String encodingType;

    public ListMultipartUploadsRequest() {

    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            桶名
     */
    public ListMultipartUploadsRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            桶名
     * @param maxUploads
     *            列举分段上传任务的最大数目
     */
    public ListMultipartUploadsRequest(String bucketName, Integer maxUploads) {
        this(bucketName);
        this.maxUploads = maxUploads;
    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            桶名
     * @param prefix
     *            限定返回的分段上传任务中的对象名必须带有的前缀
     * @param delimiter
     *            用于对分段上传任务中的对象名进行分组的字符
     * @param maxUploads
     *            列举分段上传任务的最大数目
     * @param keyMarker
     *            查询的起始位置
     * @param uploadIdMarker
     *            只有与keyMarker参数一起使用时才有意义，用于指定返回结果的起始位置，
     *            即列举时返回指定keyMarker的uploadIdMarker之后的分段上传任务
     */
    public ListMultipartUploadsRequest(String bucketName, String prefix, String delimiter, Integer maxUploads,
                                       String keyMarker, String uploadIdMarker) {
        this(bucketName, maxUploads);
        this.prefix = prefix;
        this.delimiter = delimiter;
        this.keyMarker = keyMarker;
        this.uploadIdMarker = uploadIdMarker;
    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            桶名
     * @param prefix
     *            限定返回的分段上传任务中的对象名必须带有的前缀
     * @param delimiter
     *            用于对分段上传任务中的对象名进行分组的字符
     * @param maxUploads
     *            列举分段上传任务的最大数目
     * @param keyMarker
     *            查询的起始位置
     * @param uploadIdMarker
     *            只有与keyMarker参数一起使用时才有意义，用于指定返回结果的起始位置，
     *            即列举时返回指定keyMarker的uploadIdMarker之后的分段上传任务
     * @param encodingType
     *            对响应中的Key进行指定类型的编码。如果 Key 包含xml 1.0标准不支持的控制字符，
     *            可通过设置encoding-type对响应中的Key进行编码，可选值 "url"
     */
    public ListMultipartUploadsRequest(String bucketName, String prefix, String delimiter, Integer maxUploads,
                                       String keyMarker, String uploadIdMarker, String encodingType) {
        this(bucketName, prefix, delimiter, maxUploads, keyMarker, uploadIdMarker);
        this.encodingType = encodingType;
    }

    /**
     * 获取限定返回的分段上传任务中的对象名必须带有的前缀
     *
     * @return 对象名前缀
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置限定返回的分段上传任务中的对象名必须带有的前缀
     *
     * @param prefix
     *            对象名前缀
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 获取用于对分段上传任务中的对象名进行分组的字符
     *
     * @return 分组字符
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * 设置用于对分段上传任务中的对象名进行分组的字符
     *
     * @param delimiter
     *            分组字符
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * 获取查询的起始位置（按对象名排序）
     *
     * @return 查询的起始位置
     */
    public String getKeyMarker() {
        return keyMarker;
    }

    /**
     * 设置查询的起始位置（按对象名排序）
     *
     * @param keyMarker
     *            查询的起始位置
     */
    public void setKeyMarker(String keyMarker) {
        this.keyMarker = keyMarker;
    }

    /**
     * 获取查询的起始位置（按分段上传任务的ID号排序），只有与keyMarker参数一起使用时才有意义，用于指定返回结果的起始位置
     *
     * @return 查询的起始位置
     */
    public String getUploadIdMarker() {
        return uploadIdMarker;
    }

    /**
     * 设置查询的起始位置（按分段上传任务的ID号排序），只有与keyMarker参数一起使用时才有意义，用于指定返回结果的起始位置
     *
     * @param uploadIdMarker
     *            查询的起始位置
     */
    public void setUploadIdMarker(String uploadIdMarker) {
        this.uploadIdMarker = uploadIdMarker;
    }

    /**
     * 获取分段上传任务所属的桶
     *
     * @return 分段上传任务所属的桶
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置分段上传任务所属的桶
     *
     * @param bucketName
     *            分段上传任务所属的桶
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取列举分段上传任务的最大数目
     *
     * @return 列举分段上传任务的最大数目
     */
    public Integer getMaxUploads() {
        return maxUploads;
    }

    /**
     * 设置列举分段上传任务的最大数目
     *
     * @param maxUploads
     *            列举分段上传任务的最大数目
     */
    public void setMaxUploads(Integer maxUploads) {
        this.maxUploads = maxUploads;
    }

    /**
     * 设置编码方式
     *
     * @param encodingType 编码方式，可选 url
     */
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * 获取编码方式
     *
     * @return 编码方式
     */
    public String getEncodingType() {
        return encodingType;
    }

    @Override
    public String toString() {
        return "ListMultipartUploadsRequest [bucketName=" + bucketName + ", prefix=" + prefix + ", delimiter="
                + delimiter + ", maxUploads=" + maxUploads + ", keyMarker=" + keyMarker + ", uploadIdMarker="
                + uploadIdMarker + ", encodingType=" + encodingType + "]";
    }

}

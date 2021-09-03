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
 * 列举桶内对象的请求参数
 */
public class ListObjectsRequest extends GenericRequest {
    private String bucketName;

    private String prefix;

    private String marker;

    private int maxKeys;

    private String delimiter;

    private int listTimeout;

    private String encodingType;

    public ListObjectsRequest() {
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     */
    public ListObjectsRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param maxKeys
     *            列举对象的最大条目数
     */
    public ListObjectsRequest(String bucketName, int maxKeys) {
        this(bucketName);
        this.maxKeys = maxKeys;
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param prefix
     *            列举对象时的对象名前缀
     * @param marker
     *            列举对象时的起始位置
     * @param delimiter
     *            用于对对象名进行分组的字符
     * @param maxKeys
     *            列举对象的最大条目数
     */
    public ListObjectsRequest(String bucketName, String prefix, String marker, String delimiter, int maxKeys) {
        this(bucketName, maxKeys);
        this.prefix = prefix;
        this.marker = marker;
        this.delimiter = delimiter;

    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            桶名
     * @param prefix
     *            列举对象时的对象名前缀
     * @param marker
     *            列举对象时的起始位置
     * @param delimiter
     *            用于对对象名进行分组的字符
     * @param maxKeys
     *            列举对象的最大条目数
     * @param encodingType
     *            对响应中的 Key 进行指定类型的编码。如果 Key 包含 xml 1.0标准不支持的控制字符，
     *            可通过设置 encoding-type 对响应中的Key进行编码，可选值 "url"
     */
    public ListObjectsRequest(String bucketName, String prefix, String marker, String delimiter, int maxKeys,
                              String encodingType) {
        this(bucketName, prefix, marker, delimiter, maxKeys);
        this.encodingType = encodingType;
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
     * 获取列举对象时的对象名前缀
     * 
     * @return 对象名前缀
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置列举对象时的对象名前缀
     * 
     * @param prefix
     *            对象名前缀
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 获取列举对象时的起始位置
     * 
     * @return 起始位置标识
     */
    public String getMarker() {
        return marker;
    }

    /**
     * 设置列举对象时的起始位置
     * 
     * @param marker
     *            起始位置标识
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * 获取列举对象的最大条目数
     * 
     * @return 列举对象的最大条目数
     */
    public int getMaxKeys() {
        return maxKeys;
    }

    /**
     * 设置列举对象的最大条目数
     * 
     * @param maxKeys
     *            列举对象的最大条目数
     */
    public void setMaxKeys(int maxKeys) {
        this.maxKeys = maxKeys;
    }

    /**
     * 设置编码类型
     *
     * @param encodingType 元素指定编码类型，可选 url
     */
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * 获取用于对对象名进行分组的字符
     * 
     * @return 分组字符
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * 获取字符所使用的编码方式
     *
     * @return 编码方式
     */
    public String getEncodingType() {
        return encodingType;
    }


    /**
     * 设置用于对对象名进行分组的字符
     * 
     * @param delimiter
     *            分组字符
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public int getListTimeout() {
        return listTimeout;
    }

    public void setListTimeout(int listTimeout) {
        this.listTimeout = listTimeout;
    }

    @Override
    public String toString() {
        return "ListObjectsRequest [bucketName=" + bucketName + ", prefix=" + prefix + ", marker=" + marker
                + ", maxKeys=" + maxKeys + ", delimiter=" + delimiter + ", listTimeout=" + listTimeout
                + ", encodingType=" + encodingType + "]";
    }

}

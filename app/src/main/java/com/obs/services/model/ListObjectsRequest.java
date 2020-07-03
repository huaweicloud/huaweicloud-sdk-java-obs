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
        this.bucketName = bucketName;
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
        this.bucketName = bucketName;
        this.prefix = prefix;
        this.marker = marker;
        this.delimiter = delimiter;
        this.maxKeys = maxKeys;
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
     * 获取用于对对象名进行分组的字符
     * 
     * @return 分组字符
     */
    public String getDelimiter() {
        return delimiter;
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
                + ", maxKeys=" + maxKeys + ", delimiter=" + delimiter + ", listTimeout=" + listTimeout + "]";
    }

}

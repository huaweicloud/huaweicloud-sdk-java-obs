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
import java.util.Arrays;
import java.util.List;

/**
 * 列举多段上传任务的返回结果
 */
public class MultipartUploadListing extends HeaderResponse {
    private String bucketName;

    private String keyMarker;

    private String uploadIdMarker;

    private String nextKeyMarker;

    private String nextUploadIdMarker;

    private String prefix;

    private int maxUploads;

    private boolean truncated;

    private List<MultipartUpload> multipartTaskList;

    private String delimiter;

    private String[] commonPrefixes;

    public MultipartUploadListing(String bucketName, String keyMarker, String uploadIdMarker, String nextKeyMarker,
            String nextUploadIdMarker, String prefix, int maxUploads, boolean truncated,
            List<MultipartUpload> multipartTaskList, String delimiter, String[] commonPrefixes) {
        super();
        this.bucketName = bucketName;
        this.keyMarker = keyMarker;
        this.uploadIdMarker = uploadIdMarker;
        this.nextKeyMarker = nextKeyMarker;
        this.nextUploadIdMarker = nextUploadIdMarker;
        this.prefix = prefix;
        this.maxUploads = maxUploads;
        this.truncated = truncated;
        this.multipartTaskList = multipartTaskList;
        this.delimiter = delimiter;
        if (null != commonPrefixes) {
            this.commonPrefixes = commonPrefixes.clone();
        } else {
            this.commonPrefixes = null;
        }
    }

    /**
     * 判断查询结果列表是否被截断。true表示截断，本次没有返回全部结果；false表示未截断，本次已经返回了全部结果。
     * 
     * @return 截断标识
     */
    public boolean isTruncated() {
        return truncated;
    }

    /**
     * 获取分组后的对象名前缀列表
     * 
     * @return 分组后的对象名前缀列表
     */
    public String[] getCommonPrefixes() {
        if (null != commonPrefixes) {
            return commonPrefixes.clone();
        }
        return null;
    }

    /**
     * 获取列举分段上传任务请求中（按分段上传任务ID号排序）的起始位置
     * 
     * @return 返回查询的起始位置标识
     */
    public String getUploadIdMarker() {
        return uploadIdMarker;
    }

    /**
     * 获取下次请求的起始位置（按对象名排序）
     * 
     * @return 下次请求的起始位置
     */
    public String getNextKeyMarker() {
        return nextKeyMarker;
    }

    /**
     * 获取下次请求的起始位置（按分段上传任务ID号排序）
     * 
     * @return 下次请求的起始位置
     */
    public String getNextUploadIdMarker() {
        return nextUploadIdMarker;
    }

    /**
     * 获取桶中尚未完成的分段上传任务列表
     * 
     * @return 桶中尚未完成的分段上传任务列表
     */
    public List<MultipartUpload> getMultipartTaskList() {
        if (this.multipartTaskList == null) {
            this.multipartTaskList = new ArrayList<MultipartUpload>();
        }
        return multipartTaskList;
    }

    /**
     * 获取列举分段上传任务所属的桶名
     * 
     * @return 列举分段上传任务所属的桶名
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 获取列举分段上传任务请求中的分组字符
     * 
     * @return 列举分段上传任务请求中的分组字符
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * 获取列举分段上传任务请求中的（按对象名排序）起始位置
     * 
     * @return 列举分段上传任务请求中的起始位置
     */
    public String getKeyMarker() {
        return keyMarker;
    }

    /**
     * 获取列举分段上传任务的最大条目数
     * 
     * @return 列举分段上传任务的最大条目数
     */
    public int getMaxUploads() {
        return maxUploads;
    }

    /**
     * 获取列举分段上传任务请求中的前缀
     * 
     * @return 列举分段上传任务请求中的前缀
     */
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return "MultipartUploadListing [bucketName=" + bucketName + ", keyMarker=" + keyMarker + ", uploadIdMarker="
                + uploadIdMarker + ", nextKeyMarker=" + nextKeyMarker + ", nextUploadIdMarker=" + nextUploadIdMarker
                + ", prefix=" + prefix + ", maxUploads=" + maxUploads + ", truncated=" + truncated
                + ", multipartTaskList=" + multipartTaskList + ", delimiter=" + delimiter + ", commonPrefixes="
                + Arrays.toString(commonPrefixes) + "]";
    }

}

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

import java.util.List;

/**
 * 获取桶元数据信息的请求参数
 *
 */
public class BucketMetadataInfoRequest extends OptionsInfoRequest {

    {
        httpMethod = HttpMethodEnum.HEAD;
    }

    // todo 这个 requestHeaders 具体作用，如何合并？
    private List<String> requestHeaders;

    public BucketMetadataInfoRequest() {
        super();
    }

    public BucketMetadataInfoRequest(String bucketName) {
        super(bucketName);
    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            桶名
     * @param origin
     *            跨域规则中允许的请求来源
     * @param requestHeaders
     *            跨域规则中允许携带的请求头域
     */
    public BucketMetadataInfoRequest(String bucketName, String origin, List<String> requestHeaders) {
        this.bucketName = bucketName;
        this.setOrigin(origin);
        this.requestHeaders = requestHeaders;
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

    @Override
    public String toString() {
        return "BucketMetadataInfoRequest [bucketName=" + bucketName + ", requestHeaders=" + requestHeaders + "]";
    }

}

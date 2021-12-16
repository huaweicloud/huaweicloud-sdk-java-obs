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
 * 对象请求基础类
 * 
 * @since 3.20.3
 */
public class BaseObjectRequest extends GenericRequest {
    protected String objectKey;
    protected boolean encodeHeaders = true;

    public BaseObjectRequest() {
    }

    public BaseObjectRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    public BaseObjectRequest(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    /**
     * 设置是否对返回的头域的字段进行编解码
     *
     * @param encodeHeaders
     *        是否对头域字段进行编解码
     */
    public void setIsEncodeHeaders(boolean encodeHeaders) {
        this.encodeHeaders = encodeHeaders;
    }

    /**
     * 获取是否对返回的头域的字段进行编解码
     *
     * @return 是否对头域字段进行编解码
     */
    public boolean isEncodeHeaders() {
        return encodeHeaders;
    }

    /**
     * 获取对象名
     * 
     * @return 对象名
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * 设置对象名
     * 
     * @param objectKey
     *            对象名
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    @Override
    public String toString() {
        return "BaseObjectRequest [bucketName=" + bucketName + ", objectKey=" + objectKey
                + ", isRequesterPays()=" + isRequesterPays() + "]";
    }
}

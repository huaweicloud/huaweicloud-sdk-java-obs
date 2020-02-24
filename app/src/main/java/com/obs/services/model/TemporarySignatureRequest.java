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

import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.utils.ServiceUtils;

import java.util.Date;

/**
 * 临时授权访问的请求参数
 *
 */
public class TemporarySignatureRequest extends AbstractTemporarySignatureRequest {

    private long expires = ObsConstraint.DEFAULT_EXPIRE_SECONEDS;

    private Date requestDate;

    public TemporarySignatureRequest() {
    }

    /**
     * 构造函数
     * 
     * @param method  HTTP/HTTPS请求方法
     * @param expires 有效时间，单位：秒
     */
    public TemporarySignatureRequest(HttpMethodEnum method, long expires) {
        this(method, null, null, null, expires);
    }

    /**
     * 构造函数
     * 
     * @param method       HTTP/HTTPS请求方法
     * @param bucketName   桶名
     * @param objectKey    对象名
     * @param specialParam 特殊操作符
     * @param expires      有效时间，单位：秒
     */
    public TemporarySignatureRequest(HttpMethodEnum method, String bucketName, String objectKey,
            SpecialParamEnum specialParam, long expires) {
        this(method, bucketName, objectKey, specialParam, expires, null);
    }

    /**
     * 构造函数
     * 
     * @param method       HTTP/HTTPS请求方法
     * @param bucketName   桶名
     * @param objectKey    对象名
     * @param specialParam 特殊操作符
     * @param expires      有效时间，单位：秒
     * @param requestDate  请求日期
     */
    public TemporarySignatureRequest(HttpMethodEnum method, String bucketName, String objectKey,
            SpecialParamEnum specialParam, long expires, Date requestDate) {
        this.method = method;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.specialParam = specialParam;
        this.expires = expires;
        this.requestDate = ServiceUtils.cloneDateIgnoreNull(requestDate);
    }

    /**
     * 获取临时授权访问有效时间，单位：秒。默认值为5分钟（300），最大取值为7天（604800）
     * 
     * @return 有效时间
     */
    public long getExpires() {
        return expires;
    }

    /**
     * 设置临时授权访问有效时间，单位：秒。默认值为5分钟（300），最大取值为7天（604800）
     * 
     * @param expires 有效时间
     */
    public void setExpires(long expires) {
        this.expires = expires;
    }

    /**
     * 获取请求时间
     * 
     * @return 请求时间
     */
    public Date getRequestDate() {
    	return ServiceUtils.cloneDateIgnoreNull(requestDate);
    }

    /**
     * 设置请求时间
     * 
     * @param requestDate 请求时间
     */
    public void setRequestDate(Date requestDate) {
    	if(null != requestDate) {
    		this.requestDate = (Date) requestDate.clone();
    	} else {
    		this.requestDate = null;
    	}
    }

    @Override
    public String toString() {
        return "TemporarySignatureRequest [method=" + method + ", bucketName=" + bucketName + ", objectKey=" + objectKey
                + ", specialParam=" + specialParam + ", expires=" + expires + ", requestDate=" + requestDate
                + ", headers=" + getHeaders() + ", queryParams=" + getQueryParams() + "]";
    }

}

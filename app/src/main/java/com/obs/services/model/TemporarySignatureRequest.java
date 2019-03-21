/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.obs.services.model;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.obs.services.internal.ObsConstraint;

/**
 * 临时授权访问的请求参数
 *
 */
public class TemporarySignatureRequest
{
    
    private HttpMethodEnum method;
    
    private String bucketName;
    
    private String objectKey;
    
    private SpecialParamEnum specialParam;
    
    private long expires = ObsConstraint.DEFAULT_EXPIRE_SECONEDS;
    
    private Date requestDate;
    
    private Map<String, String> headers;
    
    private Map<String, Object> queryParams;
    
    public TemporarySignatureRequest()
    {
    }
    
    /**
     * 构造函数
     * @param method HTTP/HTTPS请求方法
     * @param expires 有效时间，单位：秒
     */
    public TemporarySignatureRequest(HttpMethodEnum method, long expires)
    {
        this(method, null, null, null, expires);
    }
    
    /**
     * 构造函数
     * @param method HTTP/HTTPS请求方法
     * @param bucketName 桶名
     * @param objectKey 对象名
     * @param specialParam 特殊操作符
     * @param expires 有效时间，单位：秒
     */
    public TemporarySignatureRequest(HttpMethodEnum method, String bucketName, String objectKey, SpecialParamEnum specialParam,
        long expires)
    {
        this(method, bucketName, objectKey, specialParam, expires, null);
    }
    
    /**
     * 构造函数
     * @param method HTTP/HTTPS请求方法
     * @param bucketName 桶名
     * @param objectKey 对象名
     * @param specialParam 特殊操作符
     * @param expires 有效时间，单位：秒
     * @param requestDate 请求日期
     */
    public TemporarySignatureRequest(HttpMethodEnum method, String bucketName, String objectKey, SpecialParamEnum specialParam,
        long expires,
        Date requestDate)
    {
        this.method = method;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.specialParam = specialParam;
        this.expires = expires;
        this.requestDate = requestDate;
    }
    
    /**
     * 获取HTTP/HTTPS请求方法
     * @return HTTP/HTTPS请求方法
     */
    public HttpMethodEnum getMethod()
    {
        return method;
    }
    
    /**
     * 设置HTTP/HTTPS请求方法
     * @param method HTTP/HTTPS请求方法
     */
    public void setMethod(HttpMethodEnum method)
    {
        this.method = method;
    }
    
    /**
     * 获取桶名
     * @return 桶名
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    /**
     * 设置桶名
     * @param bucketName 桶名
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * 获取对象名
     * @return 对象名
     */
    public String getObjectKey()
    {
        return objectKey;
    }
    
    /**
     * 设置对象名
     * @param objectKey 对象名
     */
    public void setObjectKey(String objectKey)
    {
        this.objectKey = objectKey;
    }
    
    /**
     * 获取临时授权访问有效时间，单位：秒。默认值为5分钟（300），最大取值为7天（604800）
     * @return 有效时间
     */
    public long getExpires()
    {
        return expires;
    }
    
    /**
     * 设置临时授权访问有效时间，单位：秒。默认值为5分钟（300），最大取值为7天（604800）
     * @param expires 有效时间
     */
    public void setExpires(long expires)
    {
        this.expires = expires;
    }
    
    /**
     * 获取请求时间
     * @return 请求时间
     */
    public Date getRequestDate()
    {
        return requestDate;
    }
    
    /**
     * 设置请求时间
     * @param requestDate 请求时间
     */
    public void setRequestDate(Date requestDate)
    {
        this.requestDate = requestDate;
    }
    
    /**
     * 获取请求头信息
     * @return 请求头信息
     */
    public Map<String, String> getHeaders()
    {
        if (headers == null)
        {
            headers = new TreeMap<String, String>();
        }
        return headers;
    }
    
    /**
     * 获取请求查询参数
     * @return 查询参数信息
     */
    public Map<String, Object> getQueryParams()
    {
        if (queryParams == null)
        {
            queryParams = new TreeMap<String, Object>();
        }
        return queryParams;
    }
    
    /**
     * 获取特殊操作符
     * @return 特殊操作符
     */
    public SpecialParamEnum getSpecialParam()
    {
        return specialParam;
    }
    
    /**
     * 设置特殊操作符
     * @param specialParam 特殊操作符
     */
    public void setSpecialParam(SpecialParamEnum specialParam)
    {
        this.specialParam = specialParam;
    }

    /**
     * 设置请求头信息
     * @param headers 请求头信息
     */
    public void setHeaders(Map<String, String> headers)
    {
        this.headers = headers;
    }

    /**
     * 设置请求查询参数
     * @param queryParams 请求查询参数
     */
    public void setQueryParams(Map<String, Object> queryParams)
    {
        this.queryParams = queryParams;
    }
    
    @Override
    public String toString()
    {
        return "TemporarySignatureRequest [method=" + method + ", bucketName=" + bucketName + ", objectKey=" + objectKey
            + ", specialParam=" + specialParam + ", expires=" + expires + ", requestDate=" + requestDate + ", headers=" + getHeaders()
            + ", queryParams=" + getQueryParams() + "]";
    }
    
}

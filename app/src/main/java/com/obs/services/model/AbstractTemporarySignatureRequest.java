package com.obs.services.model;

import java.util.Map;
import java.util.TreeMap;

/**
 * Query 参数中携带签名的请求参数抽象类
 *
 */
public abstract class AbstractTemporarySignatureRequest {
    
    protected HttpMethodEnum method;

    protected String bucketName;

    protected String objectKey;

    protected SpecialParamEnum specialParam;

    protected Map<String, String> headers;

    protected Map<String, Object> queryParams;
    
    public AbstractTemporarySignatureRequest() {
    }

    /**
     * 构造方法
     * @param method HTTP/HTTPS请求方法
     * @param bucketName 桶名
     * @param objectKey 对象名
     */
    public AbstractTemporarySignatureRequest(HttpMethodEnum method, String bucketName, String objectKey) {
        this.method = method;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
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
}

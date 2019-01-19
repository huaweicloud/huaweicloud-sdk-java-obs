package com.obs.services.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 临时授权访问的响应结果
 *
 */
public class TemporarySignatureResponse
{
    private String signedUrl;
    
    private Map<String, String> actualSignedRequestHeaders;
    
    public TemporarySignatureResponse(String signedUrl)
    {
        this.signedUrl = signedUrl;
    }
    
    /**
     * 获取临时授权访问的URL
     * @return 临时授权访问的URL
     */
    public String getSignedUrl()
    {
        return signedUrl;
    }
    
    /**
     * 获取临时授权访问请求的头信息
     * @return 临时授权访问请求的头信息
     */
    public Map<String, String> getActualSignedRequestHeaders()
    {
        if (actualSignedRequestHeaders == null)
        {
            this.actualSignedRequestHeaders = new HashMap<String, String>();
        }
        return actualSignedRequestHeaders;
    }

    @Override
    public String toString()
    {
        return "TemporarySignatureResponse [signedUrl=" + signedUrl + ", actualSignedRequestHeaders=" + actualSignedRequestHeaders + "]";
    }
    
    
}

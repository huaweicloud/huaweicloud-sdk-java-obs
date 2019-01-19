package com.obs.services.model;

import java.util.List;

/**
 * 获取桶元数据信息的请求参数
 *
 */
public class BucketMetadataInfoRequest extends OptionsInfoRequest
{
    protected String bucketName;
    
    private List<String> requestHeaders;
    
    public BucketMetadataInfoRequest()
    {
        
    }
    
    /**
     * 构造函数
     * @param bucketName 桶名
     */
    public BucketMetadataInfoRequest(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * 构造函数
     * @param bucketName 桶名
     * @param origin 跨域规则中允许的请求来源
     * @param requestHeaders 跨域规则中允许携带的请求头域
     */
    public BucketMetadataInfoRequest(String bucketName, String origin, List<String> requestHeaders)
    {
        this.bucketName = bucketName;
        this.setOrigin(origin);
        this.requestHeaders = requestHeaders;
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

    @Override
    public String toString()
    {
        return "BucketMetadataInfoRequest [bucketName=" + bucketName + ", requestHeaders=" + requestHeaders + "]";
    }
    
}

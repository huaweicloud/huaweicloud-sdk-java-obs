package com.obs.services.model;

/**
 * 初始化分段上传任务的请求参数
 */
public class InitiateMultipartUploadRequest extends PutObjectBasicRequest
{
    private ObjectMetadata metadata;
    
    private int expires;
    
    public InitiateMultipartUploadRequest(){
        
    }
    
    /**
     * 构造函数
     * @param bucketName 分段上传任务所属的桶名
     * @param objectKey 分段上传任务所属的对象名
     */
    public InitiateMultipartUploadRequest(String bucketName, String objectKey)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    /**
     * 获取分段上传任务最终生成对象的过期时间
     * @return 对象的过期时间
     */
	public int getExpires() {
		return expires;
	}

	/**
	 * 设置分段上传任务最终生成对象的过期时间，正整数
	 * @param expires 对象的过期时间
	 */
	public void setExpires(int expires) {
		this.expires = expires;
	}
    
    /**
     * 获取分段上传任务所属的桶名
     * 
     * @return 分段上传任务所属的桶名
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    /**
     * 设置分段上传任务所属的桶名
     * 
     * @param bucketName 分段上传任务所属的桶名
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * 获取分段上传任务所属的对象名
     * 
     * @return 分段上传任务所属的对象名
     */
    public String getObjectKey()
    {
        return objectKey;
    }
    
    /**
     * 设置分段上传任务所属的对象名
     * 
     * @param objectKey 分段上传任务所属的对象名
     */
    public void setObjectKey(String objectKey)
    {
        this.objectKey = objectKey;
    }
    
    /**
     * 设置重定向链接，可以将获取这个对象的请求重定向到桶内另一个对象或一个外部的URL
     * 
     * @return 重定向链接
     */
    @Deprecated
    public String getWebSiteRedirectLocation()
    {
        return this.metadata != null ? this.metadata.getWebSiteRedirectLocation() : null;
    }
    
    /**
     * 获取重定向链接，可以将获取这个对象的请求重定向到桶内另一个对象或一个外部的URL
     * 
     * @param webSiteRedirectLocation 重定向链接
     */
    @Deprecated
    public void setWebSiteRedirectLocation(String webSiteRedirectLocation)
    {
        if(this.metadata != null) {
        	this.metadata.setWebSiteRedirectLocation(webSiteRedirectLocation);
        }
    }
    
	/**
     * 设置对象属性，支持content-type，用户自定义元数据
     * @return 对象属性
     */
    public ObjectMetadata getMetadata()
    {
        return metadata;
    }

    /**
     * 获取对象属性，支持content-type，用户自定义元数据
     * @param metadata 对象属性
     */
    public void setMetadata(ObjectMetadata metadata)
    {
        this.metadata = metadata;
    }

	@Override
	public String toString() {
		return "InitiateMultipartUploadRequest [bucketName=" + bucketName + ", objectKey=" + objectKey + ", acl=" + acl
				+ ", sseKmsHeader=" + sseKmsHeader + ", sseCHeader=" + sseCHeader + ", metadata=" + metadata
				+ ", expires=" + expires + "]";
	}
    
}

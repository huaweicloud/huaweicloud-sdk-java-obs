package com.obs.services.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置对象属性的请求参数
 */
public class SetObjectMetadataRequest{
	
	private String bucketName;
	
    private String objectKey;
    
    private String versionId;

	private StorageClassEnum storageClass;
    
    private String webSiteRedirectLocation;
    
    private boolean removeUnset;
    
    private Map<String, String> metadata;
    
    private ObjectRepleaceMetadata replaceMetadata = new ObjectRepleaceMetadata();
    
    public SetObjectMetadataRequest() {
    }
    
    /**
     * 构造函数
     * @param bucketName 桶名
     * @param objectKey 对象名
     */
    public SetObjectMetadataRequest(String bucketName, String objectKey)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    /**
     * 构造函数
     * @param bucketName 桶名
     * @param objectKey 对象名
     * @param versionId 对象的版本号
     */
    public SetObjectMetadataRequest(String bucketName, String objectKey, String versionId)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.versionId = versionId;
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
     * 获取对象版本号
     * @return 对象版本号
     */
    public String getVersionId()
    {
        return versionId;
    }
    
    /**
     * 设置对象版本号
     * @param versionId 对象版本号
     */
    public void setVersionId(String versionId)
    {
        this.versionId = versionId;
    }
    
    /**
     * 设置对象的重定向链接，可以将获取这个对象的请求重定向到桶内另一个对象或一个外部的URL
     * 
     * @return 重定向链接
     */
    public String getWebSiteRedirectLocation()
    {
        return webSiteRedirectLocation;
    }
    
    /**
     * 获取对象的重定向链接，可以将获取这个对象的请求重定向到桶内另一个对象或一个外部的URL
     * 
     * @param webSiteRedirectLocation 重定向链接
     */
    public void setWebSiteRedirectLocation(String webSiteRedirectLocation)
    {
        this.webSiteRedirectLocation = webSiteRedirectLocation;
    }
    
    /**
     * 获取对象的存储类型
     * @return 对象的存储类型
     */
    public StorageClassEnum getObjectStorageClass()
    {
        return storageClass;
    }
    
    /**
     * 设置对象的存储类型
     * @param storageClass 对象的存储类型
     */
    public void setObjectStorageClass(StorageClassEnum storageClass)
    {
        this.storageClass = storageClass;
    }

    /**
     * 判断是否删除未指定属性， 默认为false
     * true：       使用当前请求中的各项参数设置对象属性，对于已经存在值的属性进行替换，不存在值的属性进行赋值，未指定的属性删除
     * false：   使用当前请求中的各项参数设置对象属性，对于已经存在值的属性进行替换，不存在值的属性进行赋值，未指定的属性保持不变
     * @return 是否删除未指定属性标识
     */
	public boolean isRemoveUnset() {
		return removeUnset;
	}

	/**
	 * 设置是否删除未指定属性
	 * @param removeUnset 是否删除未指定属性标识
	 */
	public void setRemoveUnset(boolean removeUnset) {
		this.removeUnset = removeUnset;
	}
	
    /**
     * 新增对象的自定义元数据
     * @param key 自定义元数据的关键字
     * @param value 自定义元数据的值
     */
    public void addUserMetadata(String key, String value){
        getMetadata().put(key, value);
    }
    
    /**
     * 新增一组对象的自定义元数据
     * @param userMetadata 一组对象的自定义元数据
     */
    public void addAllUserMetadata(Map<String, String> userMetadata) {
    	if(userMetadata != null) {
    		getMetadata().putAll(userMetadata);
    	}
    }
    
    /**
     * 获取对象的自定义元数据
     * @param key 自定义元数据的关键字
     * @return 自定义元数据的值
     */
    public Object getUserMetadata(String key){
        return getMetadata().get(key);
    }
    
    /**
     * 获取重写响应中的Content-Type头
     * @return 响应中的Content-Type头
     */
    public String getContentType()
    {
        return replaceMetadata.getContentType();
    }
    
    /**
     * 设置重写响应中的Content-Type头
     * @param contentType 响应中的Content-Type头
     */
    public void setContentType(String contentType)
    {
    	replaceMetadata.setContentType(contentType);
    }
    
    /**
     * 获取重写响应中的Content-Language头
     * @return 响应中的Content-Language头
     */
    public String getContentLanguage()
    {
        return replaceMetadata.getContentLanguage();
    }
    
    /**
     * 设置重写响应中的Content-Language头
     * @param contentLanguage 响应中的Content-Language头
     */
    public void setContentLanguage(String contentLanguage)
    {
    	replaceMetadata.setContentLanguage(contentLanguage);
    }
    
    /**
     * 获取重写响应中的Expires头
     * @return 响应中的Expires头
     */
    public String getExpires()
    {
        return replaceMetadata.getExpires();
    }
    
    /**
     * 设置重写响应中的Expires头
     * @param expires 响应中的Expires头
     */
    public void setExpires(String expires)
    {
    	replaceMetadata.setExpires(expires);
    }
    
    /**
     * 获取重写响应中的Cache-Control头
     * @return 响应中的Cache-Control头
     */
    public String getCacheControl()
    {
        return replaceMetadata.getCacheControl();
    }
    
    /**
     * 设置重写响应中的Cache-Control头
     * @param cacheControl 响应中的Cache-Control头
     */
    public void setCacheControl(String cacheControl)
    {
    	replaceMetadata.setCacheControl(cacheControl);
    }
    
    /**
     * 获取重写响应中的Content-Disposition头
     * @return 响应中的Content-Disposition头
     */
    public String getContentDisposition()
    {
        return replaceMetadata.getContentDisposition();
    }
    
    /**
     * 设置重写响应中的Content-Disposition头
     * @param contentDisposition 响应中的Content-Disposition头
     */
    public void setContentDisposition(String contentDisposition)
    {
    	replaceMetadata.setContentDisposition(contentDisposition);
    }
    
    /**
     * 获取重写响应中的Content-Encoding头
     * @return 响应中的Content-Encoding头
     */
    public String getContentEncoding()
    {
        return replaceMetadata.getContentEncoding();
    }
    
    /**
     * 设置重写响应中的Content-Encoding头
     * @param contentEncoding 响应中的Content-Encoding头
     */
    public void setContentEncoding(String contentEncoding)
    {
    	replaceMetadata.setContentEncoding(contentEncoding);
    }
    
    public Map<String, String> getMetadata()
    {
    	if(metadata == null) {
    		metadata = new HashMap<String, String>();
    	}
    	return this.metadata;
    }
    
    
    @Override
	public String toString() {
		return "SetObjectMetadataRequest [bucketName=" + bucketName + ", objectKey=" + objectKey + ", versionId="
				+ versionId + ", storageClass=" + storageClass + ", webSiteRedirectLocation=" + webSiteRedirectLocation
				+ ", removeUnset=" + removeUnset + ", metadata=" + metadata + ", replaceMetadata=" + replaceMetadata
				+ "]";
	}

}

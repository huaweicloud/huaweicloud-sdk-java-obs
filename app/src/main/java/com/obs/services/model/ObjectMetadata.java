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
import java.util.Map.Entry;

/**
 * 对象的属性
 */
public class ObjectMetadata extends HeaderResponse
{
    private Date lastModified;
    
    private Long contentLength;
    
    private String contentType;
    
    private String contentEncoding;
    
    private String etag;
    
    private String contentMd5;
    
    private StorageClassEnum storageClass;
    
    private String webSiteRedirectLocation;
    
    private long nextPosition = -1;
    
    private boolean appendable;
    
    public ObjectMetadata() {
    	
	}
    
    /**
     * 判断对象是否可被追加写
     * @return 对象是否可被追加写标识
     */
	public boolean isAppendable() {
		return appendable;
	}
	
	public void setAppendable(boolean appendable) {
		this.appendable = appendable;
	}
    
	/**
	 * 获取下次追加上传的位置，仅在该值大于0，且isAppendable为true时有效
	 * @return 下次追加上传的位置
	 */
	public long getNextPosition() {
		return nextPosition;
	}
	
	public void setNextPosition(long nextPosition) {
		this.nextPosition = nextPosition;
	}

	/**
     * 获取对象属性集合
     * 
     * @return 对象属性集合
     */
    public Map<String, Object> getMetadata()
    {
    	return this.getResponseHeaders();
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
     * 获取对象的自定义元数据
     * @param key 自定义元数据的关键字
     * @return 自定义元数据的值
     */
    public Object getUserMetadata(String key){
        return getMetadata().get(key);
    }
    
    /** 
     * 获取对象的etag校验值
     * 
     * @return 对象的etag校验值
     */
    public String getEtag()
    {
        return etag;
    }
    
    public void setEtag(String objEtag)
    {
        this.etag = objEtag;
    }
    
    
    
    /**
     * 设置对象属性集合
     * 
     * @param metadata 对象属性集合
     */
    public void setMetadata(Map<String, Object> metadata)
    {
        this.responseHeaders = metadata;
    }
    
    /**
     * 获取对象的最后修改时间
     * 
     * @return 对象的最后修改时间
     */
    public Date getLastModified()
    {
        return lastModified;
    }
    
    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }
    
    /** 
     * 获取对象内容编码格式
     * 
     * @return 对象内容编码格式
     */
    public String getContentEncoding()
    {
        return contentEncoding;
    }
    
    /**
     * 设置对象内容编码格式
     * @param contentEncoding 对象内容编码格式
     */
    public void setContentEncoding(String contentEncoding)
    {
        this.contentEncoding = contentEncoding;
    }
    
    /** 
     * 获取对象内容的长度
     * 
     * @return 对象内容的长度
     */
    public Long getContentLength()
    {
        return contentLength;
    }
    
    /** 
     * 设置对象内容的长度
     * 
     * @param contentLength 对象内容的长度
     */
    public void setContentLength(Long contentLength)
    {
        this.contentLength = contentLength;
    }
    
    /** 
     * 获取对象的MIME类型
     * 
     * @return 对象的MIME类型
     */
    public String getContentType()
    {
        return contentType;
    }
    
    /** 设置对象的MIME类型
     * 
     * @param contentType 对象的MIME类型
     */
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    /**
     * 获取对象的存储类型
     * @return 对象的存储类型
     */
    @Deprecated
    public String getStorageClass()
    {
        return this.storageClass != null ? this.storageClass.getCode() : null;
    }
    
    /**
     * 设置对象的存储类型
     * @param storageClass 对象的存储类型
     */
    @Deprecated
    public void setStorageClass(String storageClass)
    {
        this.storageClass = StorageClassEnum.getValueFromCode(storageClass);
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
    
    public Object getValue(String name) {
        for (Entry<String, Object> entry: this.getMetadata().entrySet()) {
            if (isMatching(entry.getKey(), name)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    private boolean isMatching(String name1, String name2) {
        if (name1 == null && name2 == null) {
            return true;
        }
        // No match if one or other is null, but both are not
        if (name1 == null || name2 == null) {
            return false;
        }
        // Match if lower-cased names are equivalent
        return name1.toLowerCase().equals(name2.toLowerCase());
     }
    
    /**
     * 获取对象内容经过base64编码的MD5值
     * @return 对象内容经过base64编码的MD5值
     */
    public String getContentMd5()
    {
        return contentMd5;
    }

    /**
     * 设置对象内容经过base64编码的MD5值
     * @param contentMd5 对象内容经过base64编码的MD5值
     */
    public void setContentMd5(String contentMd5)
    {
        this.contentMd5 = contentMd5;
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

	@Override
	public String toString() {
		return "ObjectMetadata [metadata=" + this.getMetadata() + ", lastModified=" + lastModified + ", contentLength="
				+ contentLength + ", contentType=" + contentType + ", contentEncoding=" + contentEncoding + ", etag="
				+ etag + ", contentMd5=" + contentMd5 + ", storageClass=" + storageClass + ", webSiteRedirectLocation="
				+ webSiteRedirectLocation + ", nextPosition=" + nextPosition + ", appendable=" + appendable + "]";
	}

    
}

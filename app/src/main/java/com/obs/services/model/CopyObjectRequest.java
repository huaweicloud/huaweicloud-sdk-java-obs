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

/**
 * 复制对象的请求参数
 */
public class CopyObjectRequest extends PutObjectBasicRequest
{
    private String sourceBucketName;
    
    private String sourceObjectKey;
    
    private ObjectMetadata newObjectMetadata;
    
    private boolean replaceMetadata;
    
    private Date ifModifiedSince;
    
    private Date ifUnmodifiedSince;
    
    private String ifMatchTag;
    
    private String ifNoneMatchTag;
    
    private String versionId;
    
    private SseCHeader sseCHeaderSource;
    
    
    
    /**
     * 构造参数
     * 
     * @param sourceBucketName 源桶名
     * @param sourceObjectKey 源对象名
     * @param destinationBucketName 目标桶名
     * @param destinationObjectKey 目标对象名
     */
    public CopyObjectRequest(String sourceBucketName, String sourceObjectKey, String destinationBucketName, String destinationObjectKey)
    {
        this.sourceBucketName = sourceBucketName;
        this.sourceObjectKey = sourceObjectKey;
        this.bucketName = destinationBucketName;
        this.objectKey = destinationObjectKey;
    }
    
    public CopyObjectRequest(){
        
    }
    
    
    /**
     * 获取源对象SSE-C解密头域信息
     * 
     * @return 源对象SSE-C解密头域信息
     */
    public SseCHeader getSseCHeaderSource()
    {
        return sseCHeaderSource;
    }
    
    /**
     * 设置源对象SSE-C解密头域信息
     * 
     * @param sseCHeaderSource 源对象 SSE-C解密头域信息
     */
    public void setSseCHeaderSource(SseCHeader sseCHeaderSource)
    {
        this.sseCHeaderSource = sseCHeaderSource;
    }
    
    /**
     * 获取目标对象SSE-C加密头域信息
     * 
     * @return SSE-C加密头域信息
     */
    @Deprecated
    public SseCHeader getSseCHeaderDestination()
    {
        return this.sseCHeader;
    }
    
    /**
     * 设置目标对象SSE-C加密头域信息
     * 
     * @param sseCHeaderDestination SSE-C加密头域信息
     */
    @Deprecated
    public void setSseCHeaderDestination(SseCHeader sseCHeaderDestination)
    {
        this.sseCHeader = sseCHeaderDestination;
    }
    
    /**
     * 获取复制对象的时间条件（修改则复制），只有当源对象在此参数指定的时间之后修改过才进行复制，否则返回412（前置条件不满足）
     * 
     * @return 复制对象的时间条件
     */
    public Date getIfModifiedSince()
    {
        return ifModifiedSince;
    }
    
    /**
     * 设置复复制对象的时间条件（修改则复制），只有当源对象在此参数指定的时间之后修改过才进行复制，否则返回412（前置条件不满足）
     * 
     * @param ifModifiedSince 复制对象的时间条件
     * 
     */
    public void setIfModifiedSince(Date ifModifiedSince)
    {
        this.ifModifiedSince = ifModifiedSince;
    }
    
    /**
     * 获取复制对象的时间条件（未修改则复制），只有当源对象在此参数指定的时间之后没有修改过才进行复制，否则返回412（前置条件不满足）
     * 
     * @return 复制对象的时间条件
     */
    public Date getIfUnmodifiedSince()
    {
        return ifUnmodifiedSince;
    }
    
    /**
     * 设置复制对象的时间条件（未修改则复制），只有当源对象在此参数指定的时间之后没有修改过才进行复制，否则返回412（前置条件不满足）
     * 
     * @param ifUnmodifiedSince 复制对象的时间条件
     */
    public void setIfUnmodifiedSince(Date ifUnmodifiedSince)
    {
        this.ifUnmodifiedSince = ifUnmodifiedSince;
    }
    
    /**
     * 获取复制对象的校验值条件（相等则复制），只有当源对象的etag校验值与此参数指定的值相等时才进行复制。否则返回412（前置条件不满足）
     * 
     * @return 复制对象的校验值条件
     */
    public String getIfMatchTag()
    {
        return ifMatchTag;
    }
    
    /**
     * 设置复制对象的校验值条件（相等则复制），只有当源对象的etag校验值与此参数指定的值相等时才进行复制。否则返回412（前置条件不满足）
     * 
     * @param ifMatchTag 复制对象的校验值条件
     */
    public void setIfMatchTag(String ifMatchTag)
    {
        this.ifMatchTag = ifMatchTag;
    }
    
    /**
     * 获取复制对象的校验值条件（不相等则复制），只有当源对象的etag校验值与此参数指定的值不相等时才进行复制。否则返回412（前置条件不满足）
     * 
     * @return 复制对象的校验值条件
     */
    public String getIfNoneMatchTag()
    {
        return ifNoneMatchTag;
    }
    
    /**
     * 设置复制对象的校验值条件（不相等则复制），只有当源对象的etag校验值与此参数指定的值不相等时才进行复制。否则返回412（前置条件不满足）
     * 
     * @param ifNoneMatchTag 复制对象的校验值条件
     * 
     */
    public void setIfNoneMatchTag(String ifNoneMatchTag)
    {
        this.ifNoneMatchTag = ifNoneMatchTag;
    }
    
    /**
     * 获取源对象的版本号
     * 
     * @return 源对象版本号
     * 
     */
    public String getVersionId()
    {
        return versionId;
    }
    
    /**
     * 设置源对象的版本号
     * 
     * @param versionId 源对象版本号
     * 
     */
    public void setVersionId(String versionId)
    {
        this.versionId = versionId;
    }
    
    /**
     * 获取源桶名
     * 
     * @return 源桶名
     */
    public String getSourceBucketName()
    {
        return sourceBucketName;
    }
    
    /**
     * 设置源桶名
     * 
     * @param sourceBucketName 源桶名
     */
    public void setSourceBucketName(String sourceBucketName)
    {
        this.sourceBucketName = sourceBucketName;
    }
    
    /**
     * 获取源对象名
     * 
     * @return 源对象名
     */
    public String getSourceObjectKey()
    {
        return sourceObjectKey;
    }
    
    /**
     * 设置源对象名
     * 
     * @param sourceObjectKey 源对象名
     */
    public void setSourceObjectKey(String sourceObjectKey)
    {
        this.sourceObjectKey = sourceObjectKey;
    }
    
    /**
     * 获取目标桶名
     * 
     * @return 目标桶名
     */
    public String getDestinationBucketName()
    {
        return this.bucketName;
    }
    
    /**
     * 设置目标桶名
     * 
     * @param destinationBucketName 目标桶名
     */
    public void setDestinationBucketName(String destinationBucketName)
    {
        this.bucketName = destinationBucketName;
    }
    
    /**
     * 获取目标对象名
     * 
     * @return 目标对象名
     */
    public String getDestinationObjectKey()
    {
        return this.objectKey;
    }
    
    /**
     * 设置目标对象名
     * 
     * @param destinationObjectKey 目标对象名
     */
    public void setDestinationObjectKey(String destinationObjectKey)
    {
        this.objectKey = destinationObjectKey;
    }
    
    /**
     * 获取目标对象的属性，支持自定义元数据
     * 
     * @return ObjectMetadata 目标对象的属性
     */
    public ObjectMetadata getNewObjectMetadata()
    {
        return newObjectMetadata;
    }
    
    /**
     * 设置目标对象的属性，支持自定义元数据
     * 
     * @param newObjectMetadata 目标对象的属性
     */
    public void setNewObjectMetadata(ObjectMetadata newObjectMetadata)
    {
        this.newObjectMetadata = newObjectMetadata;
    }
    
    /**
     * 获取是否替换目标对象的属性，true表示替换，与setNewObjectMetadata搭配使用，false表示继承源对象的属性
     * 
     * @return 是否替换目标对象属性标识
     */
    public boolean isReplaceMetadata()
    {
        return replaceMetadata;
    }
    
    /**
     * 设置是否替换目标对象的属性，true表示替换，与setNewObjectMetadata搭配使用，false表示继承源对象的属性
     * 
     * @param replaceMetadata 是否替换目标对象属性标识
     *           
     */
    public void setReplaceMetadata(boolean replaceMetadata)
    {
        this.replaceMetadata = replaceMetadata;
    }
    
	@Override
	public String toString() {
		return "CopyObjectRequest [sourceBucketName=" + sourceBucketName + ", sourceObjectKey=" + sourceObjectKey
				+ ", destinationBucketName=" + bucketName + ", destinationObjectKey=" + objectKey
				+ ", newObjectMetadata=" + newObjectMetadata + ", replaceMetadata=" + replaceMetadata
				+ ", ifModifiedSince=" + ifModifiedSince + ", ifUnmodifiedSince=" + ifUnmodifiedSince + ", ifMatchTag="
				+ ifMatchTag + ", ifNoneMatchTag=" + ifNoneMatchTag + ", versionId=" + versionId + ", sseKmsHeader="
				+ sseKmsHeader + ", sseCHeaderSource=" + sseCHeaderSource + ", sseCHeaderDestination="
				+ sseCHeader + ", acl=" + acl + ", successRedirectLocation=" + successRedirectLocation + "]";
	}

    
}

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

import java.util.Map;

/**
 * 上传对象的响应结果
 */
public class PutObjectResult extends HeaderResponse
{
    
    private String bucketName;
    
    private String objectKey;
    
    private String etag;
    
    private String versionId;
    
    private StorageClassEnum storageClass;
    
    private String objectUrl;
    
    public PutObjectResult(String bucketName, String objectKey, String etag, String versionId,
			StorageClassEnum storageClass, String objectUrl) {
		super();
		this.bucketName = bucketName;
		this.objectKey = objectKey;
		this.etag = etag;
		this.versionId = versionId;
		this.storageClass = storageClass;
		this.objectUrl = objectUrl;
	}
    
    public PutObjectResult(String bucketName, String objectKey, String etag, String versionId, 
    		String objectUrl, Map<String,Object> responseHeaders, int statusCode) {
		super();
		this.bucketName = bucketName;
		this.objectKey = objectKey;
		this.etag = etag;
		this.versionId = versionId;
		this.objectUrl = objectUrl;
		this.responseHeaders = responseHeaders;
		this.statusCode = statusCode;
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
    

    /**
     * 获取对象所属的桶名
     * @return 对象所属的桶名
     */
    public String getBucketName()
    {
        return bucketName;
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
     * 获取对象版本号
     * @return 对象版本号
     */
    public String getVersionId()
    {
        return versionId;
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
     * 获取对象的全路径
     * @return 对象的全路径
     */
	public String getObjectUrl() {
		return objectUrl;
	}


	@Override
	public String toString() {
		return "PutObjectResult [bucketName=" + bucketName + ", objectKey=" + objectKey + ", etag=" + etag
				+ ", versionId=" + versionId + ", storageClass=" + storageClass + ", objectUrl=" + objectUrl + "]";
	}


    
}

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

/**
 * 追加上传请求结果
 *
 */
public class AppendObjectResult extends HeaderResponse{
	
    
    private String bucketName;
    
    private String objectKey;
    
    private String etag;
    
    private long nextPosition = -1;
    
    private StorageClassEnum storageClass;
    
    private String objectUrl;
	

	public AppendObjectResult(String bucketName, String objectKey, String etag, long nextPosition, StorageClassEnum storageClass, String objectUrl) {
		this.bucketName = bucketName;
		this.objectKey = objectKey;
		this.nextPosition = nextPosition;
		this.etag = etag;
		this.storageClass = storageClass;
		this.objectUrl = objectUrl;
	}

	/**
	 * 获取下次追加上传的位置
	 * @return 下次追加上传的位置
	 */
	public long getNextPosition() {
		return nextPosition;
	}

	/**
     * 获取本次追加内容的etag校验值
     * 
     * @return 本次追加内容的etag校验值
     */
	public String getEtag() {
		return etag;
	}

	/**
     * 获取对象所属的桶名
     * @return 对象所属的桶名
     */
	public String getBucketName() {
		return bucketName;
	}

	/**
     * 获取对象名
     * @return 对象名
     */
	public String getObjectKey() {
		return objectKey;
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
		return "AppendObjectResult [bucketName=" + bucketName + ", objectKey=" + objectKey + ", etag=" + etag
				+ ", nextPosition=" + nextPosition + ", storageClass=" + storageClass + ", objectUrl=" + objectUrl
				+ "]";
	}
    
}

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
package com.obs.services.model.fs;

/**
 * 设置桶的文件网关特性状态的请求参数
 *
 */
public class SetBucketFSStatusRequest {
	
	private String bucketName;
	
	private FSStatusEnum status;
	
	public SetBucketFSStatusRequest() {
		
	}
	
	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param status 桶的文件网关特性状态
	 */
	public SetBucketFSStatusRequest(String bucketName, FSStatusEnum status) {
		super();
		this.bucketName = bucketName;
		this.setStatus(status);
	}

	/**
	 * 获取桶名
	 * @return 桶名
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * 设置桶名
	 * @param bucketName 桶名
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * 获取桶的文件网关特性状态
	 * @return 桶的文件网关特性状态
	 */
	public FSStatusEnum getStatus() {
		return status;
	}

	/**
	 * 设置桶的文件网关特性状态
	 * @param status 桶的文件网关特性状态
	 */
	public void setStatus(FSStatusEnum status) {
		this.status = status;
	}


	
}

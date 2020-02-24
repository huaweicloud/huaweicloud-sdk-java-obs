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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 预读对象请求的响应结果
 *
 */
public class ReadAheadResult extends HeaderResponse {
	@JsonProperty(value = "bucket")
	private String bucketName;

	@JsonProperty(value = "prefix")
	private String prefix;
    
    @JsonProperty(value = "taskID")
	private String taskId;
    
    /**
     * 构造函数
     */
    public ReadAheadResult() {
    	
    }
    
    /**
     * 构造函数
     * @param bucketName 桶名
     * @param prefix 预读对象的对象名前缀
     * @param taskId 预读任务ID
     */
    public ReadAheadResult(String bucketName, String prefix, String taskId) {
    	this.bucketName = bucketName;
    	this.prefix = prefix;
    	this.taskId = taskId;
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
	    * 获取预读对象的对象名前缀
	 * @return 预读对象的对象名前缀
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	    * 设置预读对象的对象名前缀
	 * @param prefix 预读对象的对象名前缀
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * 获取预读任务ID
	 * @return 预读任务ID
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * 设置预读任务ID
	 * @param taskId 预读任务ID
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public String toString() {
		return "ReadAheadResult [bucketName=" + bucketName + ", prefix=" + prefix 
				+", taskId=" + taskId +  "]";
	}
}

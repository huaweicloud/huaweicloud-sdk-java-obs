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
 * 查询预读任务进度的响应结果
 *
 */
public class ReadAheadQueryResult extends HeaderResponse {
	@JsonProperty(value = "bucket")
	private String bucketName;

	@JsonProperty(value = "prefix")
	private String prefix;
	
	@JsonProperty(value = "consumedTime")
	private long consumedTime;
	
	@JsonProperty(value = "finishedObjectNum")
	private long finishedObjectNum;
	
	@JsonProperty(value = "finishedSize")
	private long finishedSize;
	
	@JsonProperty(value = "status")
	private String status;
	
	/**
	 * 构造函数
	 */
	public ReadAheadQueryResult() {
		
	}
	
	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param prefix 预读对象的对象名前缀
	 * @param consumedTime 消耗时间 单位：秒
	 * @param finishedObjectNum 已完成的对象个数
	 * @param finishedSize 已完成的对象大小
	 * @param status 任务状态
	 */
	public ReadAheadQueryResult(String bucketName, String prefix, long consumedTime, 
			long finishedObjectNum, long finishedSize, String status) {
		this.bucketName = bucketName;
		this.prefix = prefix;
		this.consumedTime = consumedTime;
		this.finishedObjectNum = finishedObjectNum;
		this.finishedSize = finishedSize;
		this.status = status;
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
	 * 获取消耗时间 单位：秒
	 * @return 消耗时间 单位：秒
	 */
	public long getConsumedTime() {
		return consumedTime;
	}

	/**
	 * 设置消耗时间 单位：秒
	 * @param consumedTime 消耗时间 单位：秒
	 */
	public void setConsumedTime(long consumedTime) {
		this.consumedTime = consumedTime;
	}

	/**
	 * 获取已完成的对象个数
	 * @return 已完成的对象个数
	 */
	public long getFinishedObjectNum() {
		return finishedObjectNum;
	}

	/**
	 * 设置已完成的对象个数
	 * @param finishedObjectNum 已完成的对象个数
	 */
	public void setFinishedObjectNum(long finishedObjectNum) {
		this.finishedObjectNum = finishedObjectNum;
	}

	/**
	 * 获取已完成的对象大小
	 * @return 已完成的对象大小
	 */
	public long getFinishedSize() {
		return finishedSize;
	}

	/**
	 * 设置已完成的对象大小
	 * @param finishedSize 已完成的对象大小
	 */
	public void setFinishedSize(long finishedSize) {
		this.finishedSize = finishedSize;
	}

	/**
	 * 获取任务状态
	 * @return 任务状态
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置任务状态
	 * @param status 任务状态
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "ReadAheadQueryResult [bucketName=" + bucketName + ", prefix=" + prefix 
				+", consumedTime=" + consumedTime + ", finishedObjectNum=" + finishedObjectNum 
				+ ", finishedSize=" + finishedSize + ", status=" + status + "]";
	}
}

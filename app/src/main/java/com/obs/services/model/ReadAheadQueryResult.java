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
	
	public ReadAheadQueryResult() {
		
	}
	
	public ReadAheadQueryResult(String bucketName, String prefix, long consumedTime, 
			long finishedObjectNum, long finishedSize, String status) {
		this.bucketName = bucketName;
		this.prefix = prefix;
		this.consumedTime = consumedTime;
		this.finishedObjectNum = finishedObjectNum;
		this.finishedSize = finishedSize;
		this.status = status;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public long getConsumedTime() {
		return consumedTime;
	}

	public void setConsumedTime(long consumedTime) {
		this.consumedTime = consumedTime;
	}

	public long getFinishedObjectNum() {
		return finishedObjectNum;
	}

	public void setFinishedObjectNum(long finishedObjectNum) {
		this.finishedObjectNum = finishedObjectNum;
	}

	public long getFinishedSize() {
		return finishedSize;
	}

	public void setFinishedSize(long finishedSize) {
		this.finishedSize = finishedSize;
	}

	public String getStatus() {
		return status;
	}

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

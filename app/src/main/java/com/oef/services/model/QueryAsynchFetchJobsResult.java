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
**/
package com.oef.services.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obs.services.model.HeaderResponse;

public class QueryAsynchFetchJobsResult extends HeaderResponse {
	@JsonProperty(value = "request_Id")
	private String requestId;
	
	@JsonProperty(value = "err")
	private String err;
	
	@JsonProperty(value = "code")
	private String code;
	
	@JsonProperty(value = "status")
	private String status;
	
	@JsonProperty(value = "wait")
	private int wait;
	
	@JsonProperty(value = "job")
	private CreateAsyncFetchJobsRequest job;
	
	public QueryAsynchFetchJobsResult() {
		job = new CreateAsyncFetchJobsRequest();
	}
	
	public QueryAsynchFetchJobsResult(String requestId, String err, String code, String status, int wait, CreateAsyncFetchJobsRequest job) {
		this.setRequestId(requestId);
		this.setErr(err);
		this.setCode(code);
		this.setStatus(status);
		this.setWait(wait);
		this.setJob(job);
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getErr() {
		return err;
	}

	public void setErr(String err) {
		this.err = err;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getWait() {
		return wait;
	}

	public void setWait(int wait) {
		this.wait = wait;
	}

	public CreateAsyncFetchJobsRequest getJob() {
		return job;
	}

	public void setJob(CreateAsyncFetchJobsRequest job) {
		this.job = job;
	}
	
	@Override
	public String toString() {
		return "QueryAsynchFetchJobsResult [requestId=" + requestId + ", err=" + err 
				+", code=" + code + ", status=" + status + ", wait=" + wait
				+ ", job url=" + job.getUrl() + ", job bucket=" + job.getBucketName() 
		        + ", job key=" + job.getObjectKey() + ", job callbackurl=" + job.getCallBackUrl() 
				+ ", job callbackbody=" + job.getCallBackBody() + "]";
	}
}

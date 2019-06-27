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

import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obs.services.internal.Constants;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.utils.ServiceUtils;

public class CreateAsyncFetchJobsRequest {
	@JsonProperty(value = "url")
	private String url;
	
	@JsonProperty(value = "bucket")
	private String bucket;
	
	@JsonProperty(value = "host")
	private String host;
	
	@JsonProperty(value = "key")
	private String key;
	
	@JsonProperty(value = "md5")
	private String md5;

	@JsonProperty(value = "callbackurl")
	private String callBackUrl;
	
	@JsonProperty(value = "callbackbody")
	private String callBackBody;
	
	@JsonProperty(value = "callbackbodytype")
	private String callBackBodyType;
	
	@JsonProperty(value = "callbackhost")
	private String callBackHost;
	
	@JsonProperty(value = "file_type")
	private String fileType;
	
	@JsonProperty(value = "ignore_same_key")
	private boolean ignoreSameKey;
	
	
	
	public CreateAsyncFetchJobsRequest(){
	}
	
	public CreateAsyncFetchJobsRequest(String url, String bucket){
		this.setUrl(url);
		this.setBucketName(bucket);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBucketName() {
		return bucket;
	}

	public void setBucketName(String bucket) {
		this.bucket = bucket;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getObjectKey() {
		return key;
	}

	public void setObjectKey(String key) {
		this.key = key;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}


	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getCallBackBody() {
		return callBackBody;
	}

	public void setCallBackBody(String callBackBody) throws ServiceException {
		try {
			this.callBackBody = ServiceUtils.toBase64(callBackBody.getBytes(Constants.DEFAULT_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new ServiceException("Unable to get bytes from canonical string", e);
		};
	}
	
	public String getCallbackBodyType() {
		return callBackBodyType;
	}

	public void setCallbackBodyType(String callBackBodyType) {
		this.callBackBodyType = callBackBodyType;
	}

	public String getCallBackHost() {
		return callBackHost;
	}

	public void setCallBackHost(String callBackHost) {
		this.callBackHost = callBackHost;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public boolean isignoreSameKey() {
		return ignoreSameKey;
	}

	public void setignoreSameKey(boolean ignoreSameKey) {
		this.ignoreSameKey = ignoreSameKey;
	}

	@Override
	public String toString() {
		return "CreateAsyncFetchJobsRequest [url=" + url + ", bucket=" + bucket + ", host=" + host + ", key=" + key 
				+ ", md5=" + md5 + ", callBackUrl=" + callBackUrl + ", callBackBody=" + callBackBody 
				+ ", callBackBodyType=" + callBackBodyType + ", callBackHost=" + callBackHost 
				+ ", fileType=" + fileType + ", ignoreSameKey=" + ignoreSameKey + "]";
	}

}

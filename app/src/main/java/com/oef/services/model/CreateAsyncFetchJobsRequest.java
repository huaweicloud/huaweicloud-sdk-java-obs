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
package com.oef.services.model;

import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obs.services.internal.Constants;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.utils.ServiceUtils;

/**
 * 创建异步抓取任务请求json
 *
 */
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
	
	/**
	 * 构造函数
	 * @param url 需要抓取的url,支持设置多个，以';'分隔
	 * @param bucket 桶名
	 */
	public CreateAsyncFetchJobsRequest(String url, String bucket){
		this.setUrl(url);
		this.setBucketName(bucket);
	}

	/**
	 * 获取url
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置url
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取桶名
	 * @return 桶名
	 */
	public String getBucketName() {
		return bucket;
	}

	/**
	 * 设置桶名
	 * @param bucket 桶名
	 */
	public void setBucketName(String bucket) {
		this.bucket = bucket;
	}

	/**
	 * 获取从指定url下载数据时使用的Host
	 * @return 从指定url下载数据时使用的Host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * 设置从指定url下载数据时使用的Host
	 * @param host 从指定url下载数据时使用的Host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * 获取对象名
	 * @return 对象名
	 */
	public String getObjectKey() {
		return key;
	}

	/**
	 * 设置对象名
	 * @param key 对象名
	 */
	public void setObjectKey(String key) {
		this.key = key;
	}

	/**
	 * 获取文件md5
	 * @return 文件md5
	 */
	public String getMd5() {
		return md5;
	}

	/**
	 * 设置文件md5
	 * @param md5
	 */
	public void setMd5(String md5) {
		this.md5 = md5;
	}

	/**
	 * 获取回调url
	 * @return 回调url
	 */
	public String getCallBackUrl() {
		return callBackUrl;
	}

	/**
	 * 设置回调url
	 * @param callBackUrl 回调url
	 */
	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	/**
	 * 获取回调body
	 * @return 回调body
	 */
	public String getCallBackBody() {
		return callBackBody;
	}

	/**
	 * 设置回调body(安全base64编码)
	 * @param callBackBody 回调body
	 * @throws ServiceException
	 */
	public void setCallBackBody(String callBackBody) throws ServiceException {
		try {
			this.callBackBody = ServiceUtils.toBase64(callBackBody.getBytes(Constants.DEFAULT_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new ServiceException("Unable to get bytes from canonical string", e);
		};
	}
	
	/**
	 * 获取回调Body内容类型
	 * @return 回调Body内容类型
	 */
	public String getCallbackBodyType() {
		return callBackBodyType;
	}

	/**
	 * 设置回调Body内容类型
	 * @param callBackBodyType 回调Body内容类型
	 */
	public void setCallbackBodyType(String callBackBodyType) {
		this.callBackBodyType = callBackBodyType;
	}

	/**
	 * 获取回调时使用的Host
	 * @return 回调时使用的Host
	 */
	public String getCallBackHost() {
		return callBackHost;
	}

	/**
	 * 设置回调时使用的Host
	 * @param callBackHost 回调时使用的Host
	 */
	public void setCallBackHost(String callBackHost) {
		this.callBackHost = callBackHost;
	}

	/**
	 * 获取存储文件类型
	 * @return 存储文件类型
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * 设置存储文件类型
	 * @param fileType 存储文件类型
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * 如果空间中已经存在同名文件则放弃本次抓取
	 * @return ignoreSameKey
	 */
	public boolean isignoreSameKey() {
		return ignoreSameKey;
	}

	/**
	 * @param ignoreSameKey
	 */
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

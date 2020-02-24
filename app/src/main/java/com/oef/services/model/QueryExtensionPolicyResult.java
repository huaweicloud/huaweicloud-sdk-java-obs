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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obs.services.model.HeaderResponse;

/**
 * 查询异步策略响应json
 *
 */
public class QueryExtensionPolicyResult extends HeaderResponse {
	@JsonProperty(value = "fetch")
	private FetchBean fetch;
	
	@JsonProperty(value = "transcode")
	private TranscodeBean transcode;
	
	@JsonProperty(value = "compress")
	private CompressBean compress;
	
	public QueryExtensionPolicyResult() {
		fetch = new FetchBean();
		transcode = new TranscodeBean();
		compress = new CompressBean();
	}
	
	/**
	 * 构造函数
	 * @param fetch 异步抓取策略内容
	 * @param transcode 异步转码策略内容
	 * @param compress 文件压缩策略内容
	 */
	public QueryExtensionPolicyResult(FetchBean fetch, TranscodeBean transcode, CompressBean compress) {
		this.fetch = fetch;
		this.transcode = transcode;
		this.compress = compress;
	}

	/**
	 * 获取异步抓取策略内容
	 * @return 异步抓取策略内容
	 */
	public FetchBean getFetch() {
		return fetch;
	}

	/**
	 * 设置异步抓取策略内容
	 * @param fetch 异步抓取策略内容
	 */
	public void setFetch(FetchBean fetch) {
		this.fetch = fetch;
	}

	/**
	 * 获取异步转码策略内容
	 * @return 异步转码策略内容
	 */
	public TranscodeBean getTranscode() {
		return transcode;
	}

	/**
	 * 设置异步转码策略内容
	 * @param transcode 异步转码策略内容
	 */
	public void setTranscode(TranscodeBean transcode) {
		this.transcode = transcode;
	}

	/**
	 * 获取文件压缩策略内容
	 * @return 文件压缩策略内容
	 */
	public CompressBean getCompress() {
		return compress;
	}

	/**
	 * 设置文件压缩策略内容
	 * @param compress 文件压缩策略内容
	 */
	public void setCompress(CompressBean compress) {
		this.compress = compress;
	}
	

	@Override
    public String toString()
    {
        return "ExtensionPolicyResult [fetch status=" + fetch.getStatus() + ", fetch agency=" + fetch.getAgency() 
            + ", transcode status=" + transcode.getStatus() + ", transcode agency=" + transcode.getAgency() 
            + ", compress status=" + compress.getStatus() + ", compress agency=" + compress.getAgency() +"]";
    }
}

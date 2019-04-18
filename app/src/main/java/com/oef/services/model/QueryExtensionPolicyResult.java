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

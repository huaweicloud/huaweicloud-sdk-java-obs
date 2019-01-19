package com.obs.services.model.fs;

import com.obs.services.model.BucketMetadataInfoRequest;

/**
 * 获取桶的文件网关特性状态的请求参数
 *
 */
public class GetBucketFSStatusRequest extends BucketMetadataInfoRequest{
	
	public GetBucketFSStatusRequest() {
		
	}
	
	/**
	 * 构造函数
	 * @param bucketName 桶名
	 */
	public GetBucketFSStatusRequest(String bucketName) {
		this.bucketName = bucketName;
	}
}

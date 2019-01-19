package com.obs.services.model.fs;

import com.obs.services.model.GetObjectMetadataRequest;

/**
 * 获取文件/文件夹属性请求参数
 *
 */
public class GetAttributeRequest extends GetObjectMetadataRequest{

	public GetAttributeRequest() {
		super();
	}

	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param objectKey 文件/文件夹名
	 */
	public GetAttributeRequest(String bucketName, String objectKey) {
		super(bucketName, objectKey);
	}
	
}

package com.obs.services.model.fs;

import com.obs.services.model.GetObjectRequest;

/**
 * 读取文件内容请求参数 
 *
 */
public class ReadFileRequest extends GetObjectRequest{

	public ReadFileRequest() {
		super();
	}

	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param objectKey 文件名
	 */
	public ReadFileRequest(String bucketName, String objectKey) {
		super(bucketName, objectKey);
	}
	
}

package com.obs.services.model.fs;

import com.obs.services.model.PutObjectBasicRequest;

/**
 * 创建文件夹请求参数
 *
 */
public class NewFolderRequest extends PutObjectBasicRequest{
	
	public NewFolderRequest() {
		super();
	}
	
	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param objectKey 文件夹名
	 */
	public NewFolderRequest(String bucketName, String objectKey) {
		super();
		this.bucketName = bucketName;
		this.objectKey = objectKey;
	}
	
}	

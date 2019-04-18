package com.obs.services.model.fs;

import java.io.File;
import java.io.InputStream;

import com.obs.services.model.PutObjectRequest;

/**
 * 创建文件请求参数 
 *
 */
public class NewFileRequest extends PutObjectRequest{

	public NewFileRequest() {
		super();
	}

	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param objectKey 文件名
	 * @param file 本地文件路径
	 */
	public NewFileRequest(String bucketName, String objectKey, File file) {
		super(bucketName, objectKey, file);
	}

	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param objectKey 文件名
	 * @param input 待上传的数据流
	 */
	public NewFileRequest(String bucketName, String objectKey, InputStream input) {
		super(bucketName, objectKey, input);
	}

	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param objectKey 文件名
	 */
	public NewFileRequest(String bucketName, String objectKey) {
		super(bucketName, objectKey);
	}
	
}

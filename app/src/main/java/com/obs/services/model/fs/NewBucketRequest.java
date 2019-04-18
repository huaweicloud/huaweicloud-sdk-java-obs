package com.obs.services.model.fs;

import com.obs.services.model.CreateBucketRequest;

/**
 * 创建桶请求参数
 *
 */
public class NewBucketRequest extends CreateBucketRequest{

	public NewBucketRequest() {
		super();
	}

	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param location 桶区域位置
	 */
	public NewBucketRequest(String bucketName, String location) {
		super(bucketName, location);
	}

	/**
	 * 构造函数
	 * @param bucketName 桶名
	 */
	public NewBucketRequest(String bucketName) {
		super(bucketName);
	}
	
}

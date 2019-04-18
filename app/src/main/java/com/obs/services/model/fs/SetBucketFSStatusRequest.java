package com.obs.services.model.fs;

/**
 * 设置桶的文件网关特性状态的请求参数
 *
 */
public class SetBucketFSStatusRequest {
	
	private String bucketName;
	
	private FSStatusEnum status;
	
	public SetBucketFSStatusRequest() {
		
	}
	
	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param status 桶的文件网关特性状态
	 */
	public SetBucketFSStatusRequest(String bucketName, FSStatusEnum status) {
		super();
		this.bucketName = bucketName;
		this.setStatus(status);
	}

	/**
	 * 获取桶名
	 * @return 桶名
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * 设置桶名
	 * @param bucketName 桶名
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * 获取桶的文件网关特性状态
	 * @return 桶的文件网关特性状态
	 */
	public FSStatusEnum getStatus() {
		return status;
	}

	/**
	 * 设置桶的文件网关特性状态
	 * @param status 桶的文件网关特性状态
	 */
	public void setStatus(FSStatusEnum status) {
		this.status = status;
	}


	
}

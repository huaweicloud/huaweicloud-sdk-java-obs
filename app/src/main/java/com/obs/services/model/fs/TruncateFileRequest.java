package com.obs.services.model.fs;

/**
 * 截断文件的请求参数
 *
 */
public class TruncateFileRequest {
	
	private String bucketName;

	private String objectKey;
	
	private long newLength;
	
	public TruncateFileRequest() {
		
	}
	
	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param objectKey 文件名
	 * @param newLength 文件截断后的大小
	 */
	public TruncateFileRequest(String bucketName, String objectKey, long newLength) {
		super();
		this.bucketName = bucketName;
		this.objectKey = objectKey;
		this.newLength = newLength;
	}



	/**
	 * 获取桶名
	 * 
	 * @return 桶名
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * 设置桶名
	 * 
	 * @param bucketName
	 *            桶名
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * 获取文件名
	 * 
	 * @return 文件名
	 */
	public String getObjectKey() {
		return objectKey;
	}

	/**
	 * 设置文件名
	 * 
	 * @param objectKey
	 *            文件名
	 * 
	 */
	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}
	
	/**
	 * 获取文件截断后的大小
	 * @return 文件截断后的大小
	 */
	public long getNewLength() {
		return newLength;
	}

	/**
	 * 设置文件截断后的大小
	 * @param newLength 文件截断后的大小
	 */
	public void setNewLength(long newLength) {
		this.newLength = newLength;
	}
}

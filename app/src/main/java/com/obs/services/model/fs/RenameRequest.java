package com.obs.services.model.fs;

/**
 * 重命名文件/文件夹请求参数
 *
 */
public class RenameRequest {
	
	private String bucketName;
	
	private String objectKey;
	
	private String newObjectKey;
	
	
	public RenameRequest() {
		
	}
	
	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param objectKey 文件/文件夹名
	 * @param newObjectKey  新的文件/文件夹名
	 */
	public RenameRequest(String bucketName, String objectKey, String newObjectKey) {
		super();
		this.bucketName = bucketName;
		this.objectKey = objectKey;
		this.newObjectKey = newObjectKey;
	}

    /**
     * 获取桶名
     * 
     * @return 桶名
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    /**
     * 设置桶名
     * 
     * @param bucketName 桶名
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * 获取文件/文件夹名
     * 
     * @return 文件/文件夹名
     */
    public String getObjectKey()
    {
        return objectKey;
    }
    
    /**
     * 设置文件/文件夹名
     * 
     * @param objectKey 文件/文件夹名
     *           
     */
    public void setObjectKey(String objectKey)
    {
        this.objectKey = objectKey;
    }

    /**
     * 获取新的文件/文件夹名
     * @return 新的文件/文件夹名
     */
	public String getNewObjectKey() {
		return newObjectKey;
	}

	 /**
     * 设置新的文件/文件夹名
     * @param newObjectKey  新的文件/文件夹名
     */
	public void setNewObjectKey(String newObjectKey) {
		this.newObjectKey = newObjectKey;
	}
	
	
}

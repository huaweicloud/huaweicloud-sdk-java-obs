package com.obs.services.model;

/**
 * 合并段的响应结果
 */
public class CompleteMultipartUploadResult extends HeaderResponse
{
    private String bucketName;
    
    private String objectKey;
    
    private String etag;
    
    private String location;
    
    private String versionId;
    
    private String objectUrl;
    
    public CompleteMultipartUploadResult(String bucketName, String objectKey, String etag, String location,
			String versionId, String objectUrl) {
		this.bucketName = bucketName;
		this.objectKey = objectKey;
		this.etag = etag;
		this.location = location;
		this.versionId = versionId;
		this.objectUrl = objectUrl;
	}

	/**
     *获取分段上传任务所属的桶名
     * 
     * @return 分段上传任务所属的桶名
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    /**
     * 获取分段上传任务所属的对象名
     * 
     * @return 分段上传任务所属的对象名
     */
    public String getObjectKey()
    {
        return objectKey;
    }
    
    /**
     * 获取分段上传任务所属对象的etag值
     * 
     * @return 分段上传任务所属对象的etag值
     */
    public String getEtag()
    {
        return etag;
    }
    

    /**
     * 获取合并后得到的对象的uri
     * @return 合并后得到的对象的uri
     */
	public String getLocation() {
		return location;
	}

	
	/**
	 * 获取合并得到的对象的版本号
	 * @return 合并得到的对象的版本号
	 */
	public String getVersionId() {
		return versionId;
	}
	
    /**
     * 获取合并段后得到的对象的全路径
     * @return 对象的全路径
     */
	public String getObjectUrl() {
		return objectUrl;
	}

	@Override
	public String toString() {
		return "CompleteMultipartUploadResult [bucketName=" + bucketName + ", objectKey=" + objectKey + ", etag=" + etag
				+ ", location=" + location + ", versionId=" + versionId + ", objectUrl=" + objectUrl + "]";
	}


    
    
}

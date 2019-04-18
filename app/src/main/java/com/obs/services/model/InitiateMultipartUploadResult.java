package com.obs.services.model;

/**
 * 初始化分段上传任务的响应结果
 */
public class InitiateMultipartUploadResult extends HeaderResponse
{
    private String uploadId;
    
    private String bucketName;
    
    private String objectKey;
    
    
    public InitiateMultipartUploadResult(String bucketName, String objectKey, String uploadId)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.uploadId = uploadId;
    }
    
    /**
     * 获取分段上传任务的ID号
     * 
     * @return 分段上传任务的ID号
     */
    public String getUploadId()
    {
        return uploadId;
    }
    
    
    /**
     * 获取分段上传任务所属的桶名
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
    


    @Override
    public String toString()
    {
        return "InitiateMultipartUploadResult [uploadId=" + uploadId + ", bucketName=" + bucketName + ", objectKey=" + objectKey +  "]";
    }
    
}

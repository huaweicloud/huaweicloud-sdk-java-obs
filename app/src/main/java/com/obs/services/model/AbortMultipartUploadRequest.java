package com.obs.services.model;

/**
 * 取消分段上传任务的请求参数
 */
public class AbortMultipartUploadRequest
{
    private String uploadId;
    
    private String bucketName;
    
    private String objectKey;
    
    
    public AbortMultipartUploadRequest(){
        
    }
    
    /**
     * 构造函数
     * @param bucketName 桶名
     * @param objectKey 对象名
     * @param uploadId 分段上传任务ID号
     */
    public AbortMultipartUploadRequest(String bucketName, String objectKey, String uploadId)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.uploadId = uploadId;
    }

    /**
     * 获取分段上传任务的ID号
     * 
     * @return 标识分段上传任务的ID号
     */
    public String getUploadId()
    {
        return uploadId;
    }
    
    /**
     * 设置分段上传任务的ID号
     * 
     * @param uploadId 标识分段上传任务的ID号
     */
    public void setUploadId(String uploadId)
    {
        this.uploadId = uploadId;
    }
    
    /**
     * 获取待取消的分段上传任务所属的桶名
     * 
     * @return 待取消的分段上传任务所属的桶名
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    /**
     * 设置待取消的分段上传任务所属的桶名
     * 
     * @param bucketName 待取消的分段上传任务所属的桶名
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * 获取待取消的分段上传任务所属的对象名
     * 
     * @return 待取消的分段上传任务所属的对象名
     */
    public String getObjectKey()
    {
        return objectKey;
    }
    
    /**
     * 设置待取消的分段上传任务所属的对象名
     * 
     @param objectKey 待取消的分段上传任务所属的对象名
     */
    public void setObjectKey(String objectKey)
    {
        this.objectKey = objectKey;
    }

    @Override
    public String toString()
    {
        return "AbortMultipartUploadRequest [uploadId=" + uploadId + ", bucketName=" + bucketName + ", objectKey=" + objectKey + "]";
    }
    
}

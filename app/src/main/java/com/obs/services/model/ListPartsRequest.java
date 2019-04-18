package com.obs.services.model;

/**
 * 列举已上传的段的请求参数
 */
public class ListPartsRequest
{
    private String bucketName;
    
    private String key;
    
    private String uploadId;
    
    private Integer maxParts;
    
    private Integer partNumberMarker;
    
    public ListPartsRequest(){
        
    }
    
    /**
     * 构造函数
     * @param bucketName 分段上传任务所属的桶名
     * @param key 分段上传任务所属的对象名
     */
    public ListPartsRequest(String bucketName, String key){
        this.bucketName = bucketName;
        this.key = key;
    }
    
    /**
     * 构造函数
     * @param bucketName 分段上传任务所属的桶名
     * @param key 分段上传任务所属的对象名
     * @param uploadId 分段上传任务的ID号
     */
    public ListPartsRequest(String bucketName, String key, String uploadId)
    {
        this.bucketName = bucketName;
        this.key = key;
        this.uploadId = uploadId;
    }

    /**
     * 构造函数
     * @param bucketName 分段上传任务所属的桶名
     * @param key 分段上传任务所属的对象名
     * @param uploadId 分段上传任务的ID号
     * @param maxParts 列举已上传的段返回结果最大段数目
     */
    public ListPartsRequest(String bucketName, String key, String uploadId, Integer maxParts)
    {
        this.bucketName = bucketName;
        this.key = key;
        this.uploadId = uploadId;
        this.maxParts = maxParts;
    }

    
    /**
     * 构造函数
     * @param bucketName 分段上传任务所属的桶名
     * @param key 分段上传任务所属的对象名
     * @param uploadId 分段上传任务的ID号
     * @param maxParts 列举已上传的段返回结果最大段数目
     * @param partNumberMarker 待列出段的起始位置
     */
    public ListPartsRequest(String bucketName, String key, String uploadId, Integer maxParts, Integer partNumberMarker)
    {
        this.bucketName = bucketName;
        this.key = key;
        this.uploadId = uploadId;
        this.maxParts = maxParts;
        this.partNumberMarker = partNumberMarker;
    }



    /**
     * 获取分段上传任务所属的桶名
     * @return 分段上传任务所属的桶名
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    /**
     * 设置分段上传任务所属的桶名
     * @param bucketName 分段上传任务所属的桶名
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * 获取分段上传任务所属的桶名
     * @return 分段上传任务所属的桶名
     */
    public String getKey()
    {
        return key;
    }
    
    /**
     * 设置分段上传任务所属的桶名
     * @param key 分段上传任务所属的桶名
     */
    public void setKey(String key)
    {
        this.key = key;
    }
    
    /**
     * 获取分段上传任务的ID号
     * @return 分段上传任务的ID号
     */
    public String getUploadId()
    {
        return uploadId;
    }
    
    /**
     * 设置分段上传任务的ID号
     * @param uploadId 分段上传任务的ID号
     */
    public void setUploadId(String uploadId)
    {
        this.uploadId = uploadId;
    }
    
    /**
     * 获取列举已上传的段返回结果最大段数目
     * @return 列举已上传的段返回结果最大段数目
     */
    public Integer getMaxParts()
    {
        return maxParts;
    }
    
    /**
     * 设置列举已上传的段返回结果最大段数目
     * @param maxParts 列举已上传的段返回结果最大段数目
     */
    public void setMaxParts(Integer maxParts)
    {
        this.maxParts = maxParts;
    }
    
    /**
     * 获取待列出段的起始位置
     * @return 待列出段的起始位置
     */
    public Integer getPartNumberMarker()
    {
        return partNumberMarker;
    }
    
    /**
     * 设置待列出段的起始位置
     * @param partNumberMarker 待列出段的起始位置
     */
    public void setPartNumberMarker(Integer partNumberMarker)
    {
        this.partNumberMarker = partNumberMarker;
    }
    
    @Override
    public String toString()
    {
        return "ListPartsRequest [bucketName=" + bucketName + ", key=" + key + ", uploadId=" + uploadId + ", maxParts=" + maxParts
            + ", partNumberMarker=" + partNumberMarker + "]";
    }
}

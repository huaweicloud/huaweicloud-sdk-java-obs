package com.obs.services.model;

/**
 * 上传段的响应结果
 */
public class UploadPartResult extends HeaderResponse
{
    private int partNumber;
    
    private String etag;
    
    /**
     * 获取分段号
     * 
     * @return 分段号
     */
    public int getPartNumber()
    {
        return partNumber;
    }
    
    public void setPartNumber(int partNumber)
    {
        this.partNumber = partNumber;
    }
    
    /** 
     * 获取段的etag校验值
     * 
     * @return 段的etag校验值
     */
    public String getEtag()
    {
        return etag;
    }
    
    public void setEtag(String objEtag)
    {
        this.etag = objEtag;
    }

    @Override
    public String toString()
    {
        return "UploadPartResult [partNumber=" + partNumber + ", etag=" + etag + "]";
    }
}

package com.obs.services.model;

import java.util.Date;

/**
 * 分段上传任务中的段信息
 */
public class Multipart extends HeaderResponse
{
    private Integer partNumber;
    
    private Date lastModified;
    
    private String etag;
    
    private Long size;
    
    public Multipart(){
        
    }
    
    /**
     * 构造函数
     * @param partNumber 分段号
     * @param lastModified 分段最后修改时间
     * @param etag 分段的etag校验值
     * @param size 分段的大小，单位：字节
     */
    public Multipart(Integer partNumber, Date lastModified, String etag, Long size)
    {
        this.partNumber = partNumber;
        this.lastModified = lastModified;
        this.etag = etag;
        this.size = size;
    }

    /**
     * 获取分段号
     * 
     * @return 分段号
     */
    public Integer getPartNumber()
    {
        return partNumber;
    }
    
    
    /**
     * 获取分段的最后修改时间
     * 
     * @return 分段的最后修改时间
     */
    public Date getLastModified()
    {
        return lastModified;
    }
    
    
    /**
     * 获取分段的etag校验值
     * 
     * @return 分段的etag校验值
     */
    public String getEtag()
    {
        return etag;
    }
    
    
    /**
     * 获取分段的大小，单位：字节
     * 
     * @return 分段的大小
     */
    public Long getSize()
    {
        return size;
    }
    

    @Override
    public String toString()
    {
        return "Multipart [partNumber=" + partNumber + ", lastModified=" + lastModified + ", etag=" + etag + ", size=" + size + "]";
    }
}

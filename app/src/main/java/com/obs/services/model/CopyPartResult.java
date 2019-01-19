package com.obs.services.model;

import java.util.Date;

/**
 * 复制段的响应结果
 */
public class CopyPartResult extends HeaderResponse
{
    private int partNumber;
    
    private String etag;
    
    private Date lastModified;
    
    
    public CopyPartResult(int partNumber, String etag, Date lastModified) {
		this.partNumber = partNumber;
		this.etag = etag;
		this.lastModified = lastModified;
	}


	/**
     * 获取目标段的分段号
     * 
     * @return 目标段的分段号
     */
    public int getPartNumber()
    {
        return partNumber;
    }
    
    
    /** 
     * 获取目标段的etag值
     * 
     * @return 目标段的etag值
     */
    public String getEtag()
    {
        return etag;
    }
    

    /**
     * 获取目标段的最后修改时间
     * 
     * @return 目标段的最后修改时间
     */
    public Date getLastModified()
    {
        return lastModified;
    }
    

    @Override
    public String toString()
    {
        return "CopyPartResult [partNumber=" + partNumber + ", etag=" + etag + ", lastModified=" + lastModified + "]";
    }
    
}

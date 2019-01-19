package com.obs.services.model;

import java.io.Serializable;

/**
 * 段信息，包含段的etag校验值和分段号
 */
public class PartEtag implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2946156755118245847L;

	private String etag;
    
    private Integer partNumber;
    
    public PartEtag(){
        
    }
    
    /**
     * 构造函数
     * @param etag 分段的etag校验值
     * @param partNumber 分段号
     */
    public PartEtag(String etag, Integer partNumber)
    {
        this.etag = etag;
        this.partNumber = partNumber;
    }

    
    /**
     * 获取段的etag校验值
     * @return 段的etag校验值
     */
    public String getEtag()
    {
        return etag;
    }
    
    /**
     * 设置段的etag校验值
     * @param etag 段的etag校验值
     */
    public void setEtag(String etag)
    {
        this.etag = etag;
    }


    /**
     * 获取段的etag校验值
     * @return 段的etag校验值
     */
    @Deprecated
    public String geteTag()
    {
        return this.getEtag();
    }
    
    /**
     * 设置段的etag校验值
     * @param etag 段的etag校验值
     */
    @Deprecated
    public void seteTag(String etag)
    {
    	this.setEtag(etag);
    }
    
    /**
     * 获取分段号
     * @return 分段号
     */
    public Integer getPartNumber()
    {
        return partNumber;
    }
    
    /**
     * 设置分段号
     * @param partNumber 分段号
     */
    public void setPartNumber(Integer partNumber)
    {
        this.partNumber = partNumber;
    }

    @Override
    public String toString()
    {
        return "PartEtag [etag=" + etag + ", partNumber=" + partNumber + "]";
    }
    
    @Override
    public int hashCode() {
    	 final int prime = 31;
         int result = 1;
         result = prime * result + ((etag == null) ? 0 : etag.hashCode());
         result = prime * result + partNumber;
         return result;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == null) {
			return false;
		}else {
			if (obj instanceof PartEtag) {
				PartEtag partEtag = (PartEtag) obj;
				if (partEtag.etag.equals(this.etag) && partEtag.partNumber.equals(this.partNumber)) {
					return true;
				}
			}
		}
    	return false;
    }
}

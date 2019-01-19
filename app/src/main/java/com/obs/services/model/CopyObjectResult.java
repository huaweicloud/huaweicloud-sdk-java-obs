package com.obs.services.model;

import java.util.Date;

/**
 * 复制对象的响应结果
 */
public class CopyObjectResult extends HeaderResponse
{
    private String etag;
    
    private Date lastModified;
    
    private String versionId;
    
    private String copySourceVersionId;
    
    private StorageClassEnum storageClass;
    
    
    public CopyObjectResult(String etag, Date lastModified, String versionId, String copySourceVersionId,
			StorageClassEnum storageClass) {
		this.etag = etag;
		this.lastModified = lastModified;
		this.versionId = versionId;
		this.copySourceVersionId = copySourceVersionId;
		this.storageClass = storageClass;
	}


	/**
     * 获取目标对象的etag值
     * 
     * @return 目标对象的etag值
     */
    public String getEtag()
    {
        return etag;
    }
    
    
    /**
     * 获取目标对象的最后修改时间
     * 
     * @return 目标对象的最后修改时间
     */
    public Date getLastModified()
    {
        return lastModified;
    }
    
    /**
     * 获取目标对象的版本号
     * @return 对象版本号
     */
	public String getVersionId() {
		return versionId;
	}


	/**
     * 获取源对象的版本号
     * @return 对象版本号
     */
	public String getCopySourceVersionId() {
		return copySourceVersionId;
	}


	/**
     * 获取目标对象的存储类型
     * @return 对象的存储类型
     */
    public StorageClassEnum getObjectStorageClass()
    {
        return storageClass;
    }
    

	@Override
	public String toString() {
		return "CopyObjectResult [etag=" + etag + ", lastModified=" + lastModified + ", versionId=" + versionId
				+ ", copySourceVersionId=" + copySourceVersionId + ", storageClass=" + storageClass + "]";
	}
	
    
}

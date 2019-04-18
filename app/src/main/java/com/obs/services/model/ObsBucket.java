package com.obs.services.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * OBS中的桶
 * 
 */
public class ObsBucket extends S3Bucket	
{
    
    public ObsBucket(){
        
    }
    
    /**
     * 构造函数
     * @param bucketName 桶名
     * @param location 桶的区域位置
     */
    public ObsBucket(String bucketName, String location){
        this.bucketName = bucketName;
        this.location = location;
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
     * 只能包含小写字母、数字、 "-"、 "."
     * @param bucketName 桶名
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * 获取桶的所有者
     * 
     * @return 桶的所有者
     */
    public Owner getOwner()
    {
        return owner;
    }
    
    /**
     * 设置桶的所有者
     * 
     * @param bucketOwner 桶的所有者
     */
    public void setOwner(Owner bucketOwner)
    {
        this.owner = bucketOwner;
    }
    
    /**
     * 获取桶的创建时间
     * 
     * @return 桶的创建时间
     */
    public Date getCreationDate()
    {
        return creationDate;
    }
    
    /**
     * 设置桶的创建时间
     * 
     * @param bucketCreationDate 桶的创建时间
     */
    public void setCreationDate(Date bucketCreationDate)
    {
        this.creationDate = bucketCreationDate;
    }
    
    /**
     * 获取桶的属性
     * @return 桶的属性
     */
    @Deprecated
    public Map<String, Object> getMetadata()
    {
    	if(this.metadata == null) {
    		this.metadata = new HashMap<String, Object>();
    	}
        return metadata;
    }
    
    /**
     * 设置桶的属性
     * @param metadata 桶的属性
     */
    @Deprecated
    public void setMetadata(Map<String, Object> metadata)
    {
        this.metadata = metadata;
    }
    
    /**
     * 获取桶的区域位置
     * @return 桶的区域位置
     */
    public String getLocation()
    {
        return location;
    }
    
    /**
     * 设置桶的区域位置
     * @param location 桶的区域位置，如果使用的终端节点归属于默认区域，可以不携带此参数；如果使用的终端节点归属于其他区域，则必须携带此参数
     */
    public void setLocation(String location)
    {
        this.location = location;
    }
    
    
    /**
     * 获取桶的访问权限
     * @return 桶的访问权限
     */
    public AccessControlList getAcl()
    {
        return acl;
    }
    
    /**
     * 设置桶的访问权限
     * @param acl 桶的访问权限
     */
    public void setAcl(AccessControlList acl)
    {
        this.acl = acl;
    }

    /**
     * 获取桶的存储类型
     * @return 桶存储类型
     */
    @Deprecated
    public String getStorageClass()
    {
        return this.storageClass != null ? this.storageClass.getCode() : null;
    }

    /**
     * 设置桶的存储类型
     * @param storageClass 桶存储类型
     */
    @Deprecated
    public void setStorageClass(String storageClass)
    {
        this.storageClass = StorageClassEnum.getValueFromCode(storageClass);
    }
    
    /**
     * 获取桶的存储类型
     * @return 桶存储类型
     */
    public StorageClassEnum getBucketStorageClass()
    {
        return storageClass;
    }

    /**
     * 设置桶的存储类型
     * @param storageClass 桶存储类型
     */
    public void setBucketStorageClass(StorageClassEnum storageClass)
    {
        this.storageClass = storageClass;
    }

    @Override
    public String toString()
    {
        return "ObsBucket [bucketName=" + bucketName + ", owner=" + owner + ", creationDate=" + creationDate + ", location=" + location
            + ", storageClass=" + storageClass + ", metadata=" + metadata + ", acl=" + acl + "]";
    }
}

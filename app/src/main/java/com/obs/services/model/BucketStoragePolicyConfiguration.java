package com.obs.services.model;

/**
 * 桶的存储策略
 */
public class BucketStoragePolicyConfiguration extends HeaderResponse {
	
    private StorageClassEnum storageClass;
    
    /**
     * 构造函数
     * @param storageClass 桶的存储类型
     */
    @Deprecated
    public BucketStoragePolicyConfiguration(String storageClass)
    {
        this.storageClass = StorageClassEnum.getValueFromCode(storageClass);
    }
    
    /**
     * 构造函数
     * @param storageClass 桶的存储类型
     */
    public BucketStoragePolicyConfiguration(StorageClassEnum storageClass)
    {
        this.storageClass = storageClass;
    }
    
    public BucketStoragePolicyConfiguration(){
        
    }

    @Override
    public String toString()
    {
        return "BucketStoragePolicyConfiguration [storageClass=" + storageClass + "]";
    }

    /**
     * 获取桶的存储类型
     * @return storageClass 桶的存储类型
     * @see #getBucketStorageClass()
     */
    @Deprecated
    public String getStorageClass()
    {
        return this.storageClass != null ? this.storageClass.getCode() : null;
    }

    /**
     * 设置桶的存储类型
     * @param storageClass 桶的存储类型
     * @see #setBucketStorageClass(StorageClassEnum storageClass)
     */
    @Deprecated
    public void setStorageClass(String storageClass)
    {
        this.storageClass = StorageClassEnum.getValueFromCode(storageClass);
    }
    
    /**
     * 获取桶的存储类型
     * @return storageClass 桶的存储类型
     */
    public StorageClassEnum getBucketStorageClass()
    {
        return storageClass;
    }

    /**
     * 设置桶的存储类型
     * @param storageClass 桶的存储类型
     */
    public void setBucketStorageClass(StorageClassEnum storageClass)
    {
        this.storageClass = storageClass;
    }
    
}

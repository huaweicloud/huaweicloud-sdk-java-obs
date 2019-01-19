package com.obs.services.model;

/**
 * 桶配额信息
 */
public class BucketQuota extends HeaderResponse
{

    private long bucketQuota;
    
    public BucketQuota(){
        
    }
    
    /**
     * 构造函数
     * @param bucketQuota 桶配额大小
     */
    public BucketQuota(long bucketQuota)
    {
        this.bucketQuota = bucketQuota;
    }
    
    /**
     * 获取桶配额大小，单位：字节
     * 
     * @return 桶配额大小
     */
    public long getBucketQuota()
    {
        return bucketQuota;
    }
    
    /**
     * 设置配额大小，单位：字节
     * 
     * @param quota 桶配额大小
     */
    public void setBucketQuota(long quota)
    {
        this.bucketQuota = quota;
    }

    @Override
    public String toString()
    {
        return "BucketQuota [bucketQuota=" + bucketQuota + "]";
    }
    
}

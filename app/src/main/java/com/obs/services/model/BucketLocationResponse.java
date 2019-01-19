package com.obs.services.model;

/**
 * 获取桶区域位置的响应结果
 *
 */
public class BucketLocationResponse extends HeaderResponse
{
    private String location;
    
    public BucketLocationResponse(String location) {
    	this.location = location;
    }
    
    /**
     * 获取桶的区域位置
     * @return 桶的区域位置
     */
    public String getLocation()
    {
        return location;
    }

    @Override
    public String toString()
    {
        return "BucketLocationResponse [location=" + location + "]";
    }
    
}

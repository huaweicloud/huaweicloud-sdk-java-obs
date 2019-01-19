package com.obs.services.model;

import java.util.List;

/**
 * 获取桶元数据信息的响应结果
 *
 */
public class BucketMetadataInfoResult extends OptionsInfoResult
{
    
    private StorageClassEnum storageClass;
    
    private String location;
    
    private String obsVersion;
    
    private AvailableZoneEnum availableZone;
    
    private String epid;
    
	public BucketMetadataInfoResult(String allowOrigin, List<String> allowHeaders, int maxAge,
			List<String> allowMethods, List<String> exposeHeaders, StorageClassEnum storageClass, String location,
			String obsVersion) {
		super(allowOrigin, allowHeaders, maxAge, allowMethods, exposeHeaders);
		this.storageClass = storageClass;
		this.location = location;
		this.obsVersion = obsVersion;
	}
	
	public BucketMetadataInfoResult(String allowOrigin, List<String> allowHeaders, int maxAge,
			List<String> allowMethods, List<String> exposeHeaders, StorageClassEnum storageClass, String location,
			String obsVersion, AvailableZoneEnum availableZone) {
		this(allowOrigin, allowHeaders, maxAge, allowMethods, exposeHeaders, storageClass, location, obsVersion);
		this.availableZone = availableZone;
	}
	
	public BucketMetadataInfoResult(String allowOrigin, List<String> allowHeaders, int maxAge,
            List<String> allowMethods, List<String> exposeHeaders, StorageClassEnum storageClass, String location,
            String obsVersion, AvailableZoneEnum availableZone, String epid) {
        this(allowOrigin, allowHeaders, maxAge, allowMethods, exposeHeaders, storageClass, location, obsVersion);
        this.availableZone = availableZone;
        this.epid = epid;
    }
	
	

	/**
     * 获取桶的存储类型
     * @return 桶的存储类型
     */
    @Deprecated
    public String getDefaultStorageClass()
    {
        return this.storageClass == null ? null : storageClass.getCode();
    }
    
    /**
     * 获取桶的存储类型
     * @return 桶的存储类型
     */
    public StorageClassEnum getBucketStorageClass() {
    	return this.storageClass;
    }
    
    /**
     * 获取桶的区域位置
     * @return 桶的区域位置
     */
	public String getLocation() {
		return location;
	}
	
	/**
	 * 获取OBS服务的版本
	 * @return OBS服务的版本
	 */
	public String getObsVersion() {
		return obsVersion;
	}
	
	/**
	 * 获取桶的企业ID
	 * @return 企业ID
	 */
	public String getEpid() {
	    return epid;
	}
	
	/**
	 * 获取桶的集群类型
	 * @return 桶的集群类型
	 */
	public AvailableZoneEnum getAvailableZone() {
		return this.availableZone;
	}

	@Override
	public String toString() {
		return "BucketMetadataInfoResult [storageClass=" + storageClass + ", location=" + location + ", obsVersion="
				+ obsVersion + "]";
	}
	
	
    
}

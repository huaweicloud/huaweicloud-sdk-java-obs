package com.obs.services.model.fs;

import java.util.List;

import com.obs.services.model.AvailableZoneEnum;
import com.obs.services.model.BucketMetadataInfoResult;
import com.obs.services.model.StorageClassEnum;

/**
 * 获取桶的文件网关特性状态的响应结果
 *
 */
public class GetBucketFSStatusResult extends BucketMetadataInfoResult{
	
	private FSStatusEnum status;
	
	public GetBucketFSStatusResult(String allowOrigin, List<String> allowHeaders, int maxAge, List<String> allowMethods,
			List<String> exposeHeaders, StorageClassEnum storageClass, String location, String obsVersion) {
		super(allowOrigin, allowHeaders, maxAge, allowMethods, exposeHeaders, storageClass, location, obsVersion);
	}
	
	public GetBucketFSStatusResult(String allowOrigin, List<String> allowHeaders, int maxAge, List<String> allowMethods,
			List<String> exposeHeaders, StorageClassEnum storageClass, String location, String obsVersion, FSStatusEnum status) {
		this(allowOrigin, allowHeaders, maxAge, allowMethods, exposeHeaders, storageClass, location, obsVersion);
		this.status = status;
	}
	
	public GetBucketFSStatusResult(String allowOrigin, List<String> allowHeaders, int maxAge, List<String> allowMethods,
			List<String> exposeHeaders, StorageClassEnum storageClass, String location, String obsVersion, FSStatusEnum status, AvailableZoneEnum availableZone) {
		super(allowOrigin, allowHeaders, maxAge, allowMethods, exposeHeaders, storageClass, location, obsVersion, availableZone);
		this.status = status;
	}
	
	public GetBucketFSStatusResult(String allowOrigin, List<String> allowHeaders, int maxAge, List<String> allowMethods,
            List<String> exposeHeaders, StorageClassEnum storageClass, String location, String obsVersion, FSStatusEnum status, AvailableZoneEnum availableZone, String epid) {
        super(allowOrigin, allowHeaders, maxAge, allowMethods, exposeHeaders, storageClass, location, obsVersion, availableZone, epid);
        this.status = status;
    }
	
	/**
	 * 获取桶的文件网关特性状态
	 * @return 桶的文件网关特性状态
	 */
	public FSStatusEnum getStatus() {
		return status;
	}

}

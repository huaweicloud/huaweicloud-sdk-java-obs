/**
* Copyright 2019 Huawei Technologies Co.,Ltd.
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use
* this file except in compliance with the License.  You may obtain a copy of the
* License at
* 
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software distributed
* under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
* CONDITIONS OF ANY KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations under the License.
**/
package com.obs.services.model;

import java.util.List;

/**
 * Response to a request for obtaining bucket metadata
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
     * Obtain the bucket storage class. 
     * @return Bucket storage class
     */
    @Deprecated
    public String getDefaultStorageClass()
    {
        return this.storageClass == null ? null : storageClass.getCode();
    }
    
    /**
     * Obtain the bucket storage class. 
     * @return Bucket storage class
     */
    public StorageClassEnum getBucketStorageClass() {
    	return this.storageClass;
    }
    
    /**
     * Obtain the bucket location.
     * @return Bucket location
     */
	public String getLocation() {
		return location;
	}
	
	/**
	 * Obtain the OBS version.
	 * @return OBS version
	 */
	public String getObsVersion() {
		return obsVersion;
	}
	
	/**
	 * Obtain the enterprise ID of a bucket.
	 * @return Enterprise ID
	 */
	public String getEpid() {
	    return epid;
	}
	
	/**
	 * Obtain the bucket cluster type.
	 * @return Bucket cluster type
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

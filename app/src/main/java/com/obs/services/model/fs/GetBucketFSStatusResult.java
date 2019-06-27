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
package com.obs.services.model.fs;

import java.util.List;

import com.obs.services.model.AvailableZoneEnum;
import com.obs.services.model.BucketMetadataInfoResult;
import com.obs.services.model.StorageClassEnum;

/**
 * Response to a request of obtaining status of the file gateway feature of a bucket
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
	 * Obtain the status of the file gateway feature of a bucket. 
	 * @return Status of the file gateway feature
	 */
	public FSStatusEnum getStatus() {
		return status;
	}

}

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
 */
package com.obs.services.model;

public class ReadAheadRequest {
	 private String bucketName;
	    
	 private String prefix;
	 
	 private CacheOptionEnum cacheOption;
	 
	 private long ttl = 60 * 60 * 24L;
	 
	 public ReadAheadRequest(String bucketName, String prefix) {
		 this.setBucketName(bucketName);
		 this.setPrefix(prefix);
	 }
	 
	 public	ReadAheadRequest(String bucketName, String prefix, CacheOptionEnum cacheOption, long ttl) {
		 this.setBucketName(bucketName);
		 this.setPrefix(prefix);
		 this.setCacheOption(cacheOption);
		 this.setTtl(ttl);
	 }

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public CacheOptionEnum getCacheOption() {
		return cacheOption;
	}

	public void setCacheOption(CacheOptionEnum cacheOption) {
		this.cacheOption = cacheOption;
	}

	public long getTtl() {
		return ttl;
	}

	public void setTtl(long ttl) {
		if(ttl < 0 || ttl > 259200) {
			return;
		}
		this.ttl = ttl;
	}
}

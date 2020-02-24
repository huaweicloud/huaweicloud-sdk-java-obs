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

/**
 * 预读缓存的控制选项
 *
 */
public enum CacheOptionEnum {
	//联调版本暂不支持
//	/**
//	 * 保证预读缓存的一致性，低缓存性能 
//	 */
//	CONSISTENCY("cache-with-consistency"),
	
	/**
	 * 不保证预读缓存的一致性，高缓存性能
	 */
	 PERFORMANCE("cache-with-performance");
	
	private String code;
	
	private CacheOptionEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	public static CacheOptionEnum getValueFromCode(String code) {
		for (CacheOptionEnum val : CacheOptionEnum.values()) {
			if (val.code.equals(code)) {
				return val;
			}
		}
		return null;
	}
}

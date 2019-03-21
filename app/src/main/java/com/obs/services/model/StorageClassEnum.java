/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.obs.services.model;

/**
 *
 * 存储类型
 *
 */
public enum StorageClassEnum {
	
	/**
	 * 标准存储
	 */
	STANDARD,

	/**
	 * 低频访问存储
	 */
	WARM,
	
	/**
	 * 归档存储
	 */
	COLD;


	public String getCode() {
		return this.name();
	}

	public static StorageClassEnum getValueFromCode(String code) {
		if("STANDARD".equals(code)) {
			return STANDARD;
		}else if("WARM".equals(code) || "STANDARD_IA".equals(code)) {
			return WARM;
		}else if("COLD".equals(code) || "GLACIER".equals(code)) {
			return COLD;
		}
		return null;
	}
}

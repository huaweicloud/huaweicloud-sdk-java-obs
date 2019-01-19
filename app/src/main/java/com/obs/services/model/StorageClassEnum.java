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

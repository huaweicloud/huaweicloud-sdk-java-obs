package com.obs.services.model;

/**
 * 桶多版本状态
 *
 */
public enum VersioningStatusEnum {
	
	/**
	 * 暂停多版本
	 */
	SUSPENDED("Suspended"),
	
	/**
	 * 启用多版本
	 */
	ENABLED("Enabled");
	
	private String code;
	
	private VersioningStatusEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	public static VersioningStatusEnum getValueFromCode(String code) {
		for (VersioningStatusEnum val : VersioningStatusEnum.values()) {
			if (val.code.equals(code)) {
				return val;
			}
		}
		return null;
	}

}

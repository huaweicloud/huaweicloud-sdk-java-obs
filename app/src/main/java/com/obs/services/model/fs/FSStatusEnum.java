package com.obs.services.model.fs;


/**
 * 桶的文件网关特性状态
 *
 */
public enum FSStatusEnum {

	/**
	 * 启用文件网关特性
	 */
	ENABLED("Enabled"),

	/**
	 * 禁用文件网关特性
	 */
	DISABLED("Disabled");

	private String code;

	private FSStatusEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static FSStatusEnum getValueFromCode(String code) {
		for (FSStatusEnum val : FSStatusEnum.values()) {
			if (val.code.equals(code)) {
				return val;
			}
		}
		return null;
	}
}

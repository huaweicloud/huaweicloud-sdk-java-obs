package com.obs.services.model;

/**
 * 取回选项
 *
 */
public enum RestoreTierEnum {
	/**
	 * 快速取回，取回耗时1~5分钟
	 */
	EXPEDITED("Expedited"),
	/**
	 * 标准取回，取回耗时3~5小时
	 */
	STANDARD("Standard"),
	/**
	 * 批量取回，取回耗时5~12小时
	 */
	@Deprecated
	BULK("Bulk");

	private String code;

	private RestoreTierEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static RestoreTierEnum getValueFromCode(String code) {
		for (RestoreTierEnum val : RestoreTierEnum.values()) {
			if (val.code.equals(code)) {
				return val;
			}
		}
		return null;
	}
}

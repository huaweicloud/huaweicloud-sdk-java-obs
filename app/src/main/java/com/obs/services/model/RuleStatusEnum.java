package com.obs.services.model;

/**
 *
 * 规则状态
 *
 */
public enum RuleStatusEnum {
	
	/**
	 * 启用规则
	 */
	ENABLED("Enabled"),

	/**
	 * 禁用规则
	 */
	DISABLED("Disabled");

	private String code;

	private RuleStatusEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static RuleStatusEnum getValueFromCode(String code) {
		for (RuleStatusEnum val : RuleStatusEnum.values()) {
			if (val.code.equals(code)) {
				return val;
			}
		}
		return null;
	}
}

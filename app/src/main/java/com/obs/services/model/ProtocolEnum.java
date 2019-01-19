package com.obs.services.model;

/**
 *
 * 重定向协议
 *
 */
public enum ProtocolEnum {
	
	/**
	 * 重定向时使用HTTP协议
	 */
	HTTP("http"),

	/**
	 * 重定向时使用HTTPS协议
	 */
	HTTPS("https");

	private String code;

	private ProtocolEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static ProtocolEnum getValueFromCode(String code) {
		for (ProtocolEnum val : ProtocolEnum.values()) {
			if (val.code.equals(code)) {
				return val;
			}
		}
		return null;
	}
}

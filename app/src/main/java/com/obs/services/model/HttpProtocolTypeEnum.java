package com.obs.services.model;

/**
 * HTTP协议类型
 *
 */
public enum HttpProtocolTypeEnum{
	
	/**
	 * HTTP 1.1协议
	 */
	HTTP1_1("http1.1"),
	/**
	 * HTTP 2.0协议
	 */
	HTTP2_0("http2.0");
	
	private String code;

	private HttpProtocolTypeEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static HttpProtocolTypeEnum getValueFromCode(String code) {
		for (HttpProtocolTypeEnum val : HttpProtocolTypeEnum.values()) {
			if (val.code.equals(code)) {
				return val;
			}
		}
		return HTTP1_1;
	}
	
}
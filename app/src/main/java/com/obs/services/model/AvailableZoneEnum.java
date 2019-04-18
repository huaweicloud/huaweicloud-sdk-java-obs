package com.obs.services.model;

/**
 *  桶的集群类型
 *
 */
public enum AvailableZoneEnum {
	
	MULTI_AZ("3az");
	
	private String code;

	private AvailableZoneEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static AvailableZoneEnum getValueFromCode(String code) {
		for (AvailableZoneEnum val : AvailableZoneEnum.values()) {
			if (val.code.equals(code)) {
				return val;
			}
		}
		return null;
	}
}

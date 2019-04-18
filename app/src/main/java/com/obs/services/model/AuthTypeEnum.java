package com.obs.services.model;

/**
 * 鉴权类型 
 *
 */
public enum AuthTypeEnum{
	/**
	 * V2协议
	 */
	V2,
	/**
	 * OBS协议
	 */
	OBS,
	
	/**
	 * V4协议
	 */
	@Deprecated
	V4
}
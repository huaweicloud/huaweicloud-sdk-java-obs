package com.obs.services.model;

import com.obs.services.internal.Constants;

/**
 * 用户组类型 
 *
 */
public enum GroupGranteeEnum {
    /**
     * 匿名用户组，代表所有用户
     */
    ALL_USERS,

    /**
     * OBS授权用户组，代表任何拥有OBS账户的用户
     */
    @Deprecated
    AUTHENTICATED_USERS,

    /**
     * 日志投递用户组，一般用户配置访问日志
     */
    @Deprecated
    LOG_DELIVERY;
    
	public String getCode() {
		return this.name();
	}

	public static GroupGranteeEnum getValueFromCode(String code) {
		if("Everyone".equals(code) || Constants.ALL_USERS_URI.equals(code)) {
			return ALL_USERS;
		}else if(Constants.AUTHENTICATED_USERS_URI.equals(code)) {
			return AUTHENTICATED_USERS;
		}else if(Constants.LOG_DELIVERY_URI.equals(code)) {
			return LOG_DELIVERY;
		}
		return null;
	}
}

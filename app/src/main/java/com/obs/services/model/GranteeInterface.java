package com.obs.services.model;

/**
 * 表示ACL中被授权的用户/用户组的接口抽象，{@link AccessControlList}
 */
public interface GranteeInterface
{
    /**
     * 设置被授权用户/用户组的标识
     * 
     * @param id 被授权用户/用户组的标识
     */
    public void setIdentifier(String id);
    
    /**
     * 获取被授权用户/用户组的标识
     * 
     * @return 被授权用户/用户组的标识
     */
    public String getIdentifier();
	
	
}

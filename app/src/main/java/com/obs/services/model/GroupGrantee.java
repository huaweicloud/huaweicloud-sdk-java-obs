package com.obs.services.model;

/**
 * ACL中被授权用户组信息，{@link AccessControlList}
 */
public class GroupGrantee implements GranteeInterface
{
    
    /**
     * 匿名用户组，代表所有用户
     */
    public static final GroupGrantee ALL_USERS = new GroupGrantee(GroupGranteeEnum.ALL_USERS);

    /**
     * OBS授权用户组，代表任何拥有OBS账户的用户
     */
    @Deprecated
    public static final GroupGrantee AUTHENTICATED_USERS = new GroupGrantee(GroupGranteeEnum.AUTHENTICATED_USERS);

    /**
     * 日志投递用户组，一般用户配置访问日志
     */
    @Deprecated
    public static final GroupGrantee LOG_DELIVERY = new GroupGrantee(GroupGranteeEnum.LOG_DELIVERY);

    
    private GroupGranteeEnum groupGranteeType;
    
    public GroupGrantee()
    {
    }
    
    /**
     * 构造函数
     * @param uri 代表被授权用户组的URI
     */
    public GroupGrantee(String uri)
    {
        this.groupGranteeType = GroupGranteeEnum.getValueFromCode(uri);
    }
    
    public GroupGrantee(GroupGranteeEnum groupGranteeType)
    {
        this.groupGranteeType = groupGranteeType;
    }
    
    /**
     * 设置代表被授权用户组的URI
     * @param uri 代表被授权用户组的URI
     */
    @Override
    public void setIdentifier(String uri)
    {
        this.groupGranteeType = GroupGranteeEnum.getValueFromCode(uri);
    }
    
    /**
     * 获取代表被授权用户组的URI
     * 
     * @return 代表被授权用户组的URI
     */
    @Override
    public String getIdentifier()
    {
        return this.groupGranteeType == null ? null : this.groupGranteeType.getCode();
    }
    
    /**
     * 获取被授权用户组的类型
     * @return 被授权用户组的类型
     */
    public GroupGranteeEnum getGroupGranteeType() {
    	return this.groupGranteeType;
    }
    

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupGranteeType == null) ? 0 : groupGranteeType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupGrantee other = (GroupGrantee) obj;
		if (groupGranteeType != other.groupGranteeType)
			return false;
		return true;
	}

	/**
     * 返回对象描述信息
     * 
     * @return 返回对象描述字符串
     */
    @Override
    public String toString()
    {
        return "GroupGrantee [" + groupGranteeType + "]";
    }
}

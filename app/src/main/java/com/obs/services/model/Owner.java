package com.obs.services.model;

/**
 * 桶或对象的所有者
 */
public class Owner
{
    private String displayName;
    
    private String id;
    
    /**
     * 获取所有者的名称
     * 
     * @return 所有者的名称
     */
    @Deprecated
    public String getDisplayName()
    {
        return displayName;
    }
    
    /**
     * 设置所有者的名称
     * 
     * @param displayName 所有者的名称
     */
    @Deprecated
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
    
    /**
     * 获取所有者的DomainId
     * 
     * @return 所有者的DomainId
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * 设置所有者的DomainId
     * 
     * @param id 所有者的DomainId
     */
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "Owner [displayName=" + displayName + ", id=" + id + "]";
    }
    
}

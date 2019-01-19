package com.obs.services.model;

/**
 * ACL中被授权用户信息，{@link AccessControlList}
 */
public class CanonicalGrantee implements GranteeInterface
{
    private String grantId;
    
    private String displayName;
    
    public CanonicalGrantee()
    {
    }
    
    /**
     * 构造函数
     * 
     * @param identifier 被授权用户的DomainId
     */
    public CanonicalGrantee(String identifier)
    {
        this.grantId = identifier;
    }
    
    /**
     * 设置被授权用户的DomainId
     * 
     * @param canonicalGrantId 被授权用户的DomainId
     */
    public void setIdentifier(String canonicalGrantId)
    {
        this.grantId = canonicalGrantId;
    }
    
    /**
     * 获取被授权用户的DomainId
     * 
     * @return 被授权用户的DomainId
     */
    public String getIdentifier()
    {
        return grantId;
    }
    
    /**
     * 设置被授权用户的用户名
     * 
     * @param displayName 被授权用户的用户名
     */
    @Deprecated
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
    
    /**
     * 获取被授权用户的用户名
     * 
     * @return 被授权用户的用户名
     */
    @Deprecated
    public String getDisplayName()
    {
        return this.displayName;
    }
    
   
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((grantId == null) ? 0 : grantId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CanonicalGrantee other = (CanonicalGrantee)obj;
        if (grantId == null)
        {
            if (other.grantId != null)
                return false;
        }
        else if (!grantId.equals(other.grantId))
            return false;
        return true;
    }

    public String toString()
    {
        return "CanonicalGrantee [id=" + grantId + (displayName != null ? ", displayName=" + displayName : "") + "]";
    }
}

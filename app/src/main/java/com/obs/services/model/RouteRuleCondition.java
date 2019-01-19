package com.obs.services.model;

/**
 * 重定向条件
 */
public class RouteRuleCondition
{
    private String keyPrefixEquals;
    
    private String httpErrorCodeReturnedEquals;
    
    /**
     * 获取当重定向生效时对象名的前缀
     * @return 当重定向生效时对象名的前缀
     */
    public String getKeyPrefixEquals()
    {
        return keyPrefixEquals;
    }
    
    /**
     * 设置当重定向生效时对象名的前缀
     * @param keyPrefixEquals 当重定向生效时对象名的前缀
     */
    public void setKeyPrefixEquals(String keyPrefixEquals)
    {
        this.keyPrefixEquals = keyPrefixEquals;
    }
    
    /**
     * 获取重定向生效时的HTTP错误码配置
     * @return 重定向生效时的HTTP错误码配置
     */
    public String getHttpErrorCodeReturnedEquals()
    {
        return httpErrorCodeReturnedEquals;
    }
    
    /**
     * 设置重定向生效时的HTTP错误码配置
     * @param httpErrorCodeReturnedEquals 重定向生效时的HTTP错误码配置
     */
    public void setHttpErrorCodeReturnedEquals(String httpErrorCodeReturnedEquals)
    {
        this.httpErrorCodeReturnedEquals = httpErrorCodeReturnedEquals;
    }
    
    @Override
    public String toString()
    {
        return "RouteRuleCondition [keyPrefixEquals=" + keyPrefixEquals + ", httpErrorCodeReturnedEquals=" + httpErrorCodeReturnedEquals
            + "]";
    }
    
}

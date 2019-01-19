package com.obs.services.model;

/**
 * 重定向时的路由规则配置
 */
public class RouteRule
{
    private RouteRuleCondition condition;
    
    private Redirect redirect;
    
    /**
     * 获取重定向条件
     * @return 重定向条件
     */
    public RouteRuleCondition getCondition()
    {
        return condition;
    }
    
    /**
     * 设置重定向条件
     * @param condition 重定向条件
     */
    public void setCondition(RouteRuleCondition condition)
    {
        this.condition = condition;
    }
    
    /**
     * 获取重定向配置
     * @return 重定向配置
     */
    public Redirect getRedirect()
    {
        return redirect;
    }
    
    /**
     * 设置重定向配置
     * @param redirect 重定向配置
     */
    public void setRedirect(Redirect redirect)
    {
        this.redirect = redirect;
    }

    @Override
    public String toString()
    {
        return "RouteRule [condition=" + condition + ", redirect=" + redirect + "]";
    }
    
}

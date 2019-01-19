package com.obs.services.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 桶的website（托管）配置
 */
public class WebsiteConfiguration extends HeaderResponse
{
    private String suffix;
    
    private String key;
    
    private RedirectAllRequest redirectAllRequestsTo;
    
    private List<RouteRule> routeRules;
    
    /**
     * 获取托管首页
     * @return 托管首页 
     * 
     */
    public String getSuffix()
    {
        return suffix;
    }
    
    /**
     * 设置托管首页
     * @param suffix 托管首页 
     * 
     */
    public void setSuffix(String suffix)
    {
        this.suffix = suffix;
    }
    
    /**
     * 获取托管错误页面
     * @return key 托管错误页面
     */
    public String getKey()
    {
        return key;
    }
    
    /**
     * 设置托管错误页面
     * @param key 托管错误页面
     */
    public void setKey(String key)
    {
        this.key = key;
    }
    
    /**
     * 获取重定向路由规则列表
     * @return routeRules 重定向路由规则列表
     */
    public List<RouteRule> getRouteRules()
    {
    	if(this.routeRules == null) {
    		this.routeRules = new ArrayList<RouteRule>();
    	}
        return routeRules;
    }
    
    /**
     * 设置重定向路由规则列表
     * @param routeRules 重定向路由规则列表
     */
    public void setRouteRules(List<RouteRule> routeRules)
    {
        this.routeRules = routeRules;
    }
    
    /**
     * 获取所有请求重定向规则
     * @return 所有请求重定向规则
     */
    public RedirectAllRequest getRedirectAllRequestsTo()
    {
        return redirectAllRequestsTo;
    }
    
    /**
     * 设置所有请求重定向规则
     * @param redirectAllRequestsTo 所有请求重定向规则
     */
    public void setRedirectAllRequestsTo(RedirectAllRequest redirectAllRequestsTo)
    {
        this.redirectAllRequestsTo = redirectAllRequestsTo;
    }
    
    
    @Override
    public String toString()
    {
        return "WebsiteConfigration [suffix=" + suffix + ", key=" + key + ", redirectAllRequestsTo=" + redirectAllRequestsTo
            + ", routeRules=" + routeRules + "]";
    }
}

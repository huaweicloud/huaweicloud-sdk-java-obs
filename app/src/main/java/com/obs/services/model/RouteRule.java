/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
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

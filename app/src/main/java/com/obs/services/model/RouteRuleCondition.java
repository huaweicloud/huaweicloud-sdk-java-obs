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

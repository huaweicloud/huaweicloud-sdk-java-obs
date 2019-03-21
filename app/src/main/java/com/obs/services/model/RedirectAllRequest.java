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
 * 所有请求重定向的配置
 */
public class RedirectAllRequest
{
    private ProtocolEnum protocol;
    
    private String hostName;
    
    /**
     * 获取重定向请求时使用的协议
     * @return 重定向请求时使用的协议
     * @see #getRedirectProtocol()
     */
    @Deprecated
    public String getProtocol()
    {
        return this.protocol != null ? this.protocol.getCode() : null;
    }
    
    /**
     * 设置重定向请求时使用的协议
     * @param protocol 重定向请求时使用的协议
     * @see #setRedirectProtocol(ProtocolEnum protocol)
     */
    @Deprecated
    public void setProtocol(String protocol)
    {
        this.protocol = ProtocolEnum.getValueFromCode(protocol);
    }
    
    /**
     * 获取重定向请求时使用的协议
     * @return 重定向请求时使用的协议
     */
    public ProtocolEnum getRedirectProtocol()
    {
        return protocol;
    }
    
    /**
     * 设置重定向请求时使用的协议
     * @param protocol 重定向请求时使用的协议
     */
    public void setRedirectProtocol(ProtocolEnum protocol)
    {
        this.protocol = protocol;
    }
    
    /**
     * 获取重定向请求时使用的主机名
     * @return 重定向请求时使用的主机名
     */
    public String getHostName()
    {
        return hostName;
    }
    
    /**
     * 设置重定向请求时使用的主机名
     * @param hostName 重定向请求时使用的主机名
     */
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    @Override
    public String toString()
    {
        return "RedirectAllRequest [protocol=" + protocol + ", hostName=" + hostName + "]";
    }
    
}

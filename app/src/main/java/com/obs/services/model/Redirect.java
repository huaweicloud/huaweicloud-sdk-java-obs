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
 * 请求重定向配置
 */
public class Redirect
{
    private ProtocolEnum protocol;
    
    private String hostName;
    
    private String replaceKeyPrefixWith;
    
    private String replaceKeyWith;
    
    private String httpRedirectCode;
    
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
    
    /**
     * 获取重定向请求时使用的对象名前缀
     * @return 重定向请求时使用的对象名前缀
     */
    public String getReplaceKeyPrefixWith()
    {
        return replaceKeyPrefixWith;
    }
    
    /**
     * 设置重定向请求时使用的对象名前缀
     * @param replaceKeyPrefixWith 重定向请求时使用的对象名前缀
     */
    public void setReplaceKeyPrefixWith(String replaceKeyPrefixWith)
    {
        this.replaceKeyPrefixWith = replaceKeyPrefixWith;
    }
    
    /**
     * 获取重定向请求时使用的对象名
     * @return 重定向请求时使用的对象名
     */
    public String getReplaceKeyWith()
    {
        return replaceKeyWith;
    }
    
    /**
     * 设置重定向请求时使用的对象名
     * @param replaceKeyWith 重定向请求时使用的对象名
     */
    public void setReplaceKeyWith(String replaceKeyWith)
    {
        this.replaceKeyWith = replaceKeyWith;
    }
    
    /**
     * 获取响应中的HTTP状态码配置
     * @return HTTP状态码配置
     */
    public String getHttpRedirectCode()
    {
        return httpRedirectCode;
    }
    
    /**
     * 设置响应中的HTTP状态码配置
     * @param httpRedirectCode HTTP状态码配置
     */
    public void setHttpRedirectCode(String httpRedirectCode)
    {
        this.httpRedirectCode = httpRedirectCode;
    }
    
    @Override
    public String toString()
    {
        return "RedirectRule [protocol=" + protocol + ", hostName=" + hostName + ", replaceKeyPrefixWith=" + replaceKeyPrefixWith
            + ", replaceKeyWith=" + replaceKeyWith + ", httpRedirectCode=" + httpRedirectCode + "]";
    }
}

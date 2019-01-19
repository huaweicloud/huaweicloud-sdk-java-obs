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

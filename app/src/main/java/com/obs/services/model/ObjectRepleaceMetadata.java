package com.obs.services.model;

/**
 * 下载对象时可重写的响应消息头信息
 */
public class ObjectRepleaceMetadata
{
    
    private String contentType;
    
    private String contentLanguage;
    
    private String expires;
    
    private String cacheControl;
    
    private String contentDisposition;
    
    private String contentEncoding;
    
    /**
     * 获取重写响应中的Content-Type头
     * @return 响应中的Content-Type头
     */
    public String getContentType()
    {
        return contentType;
    }
    
    /**
     * 设置重写响应中的Content-Type头
     * @param contentType 响应中的Content-Type头
     */
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
    
    /**
     * 获取重写响应中的Content-Language头
     * @return 响应中的Content-Language头
     */
    public String getContentLanguage()
    {
        return contentLanguage;
    }
    
    /**
     * 设置重写响应中的Content-Language头
     * @param contentLanguage 响应中的Content-Language头
     */
    public void setContentLanguage(String contentLanguage)
    {
        this.contentLanguage = contentLanguage;
    }
    
    /**
     * 获取重写响应中的Expires头
     * @return 响应中的Expires头
     */
    public String getExpires()
    {
        return expires;
    }
    
    /**
     * 设置重写响应中的Expires头
     * @param expires 响应中的Expires头
     */
    public void setExpires(String expires)
    {
        this.expires = expires;
    }
    
    /**
     * 获取重写响应中的Cache-Control头
     * @return 响应中的Cache-Control头
     */
    public String getCacheControl()
    {
        return cacheControl;
    }
    
    /**
     * 设置重写响应中的Cache-Control头
     * @param cacheControl 响应中的Cache-Control头
     */
    public void setCacheControl(String cacheControl)
    {
        this.cacheControl = cacheControl;
    }
    
    /**
     * 获取重写响应中的Content-Disposition头
     * @return 响应中的Content-Disposition头
     */
    public String getContentDisposition()
    {
        return contentDisposition;
    }
    
    /**
     * 设置重写响应中的Content-Disposition头
     * @param contentDisposition 响应中的Content-Disposition头
     */
    public void setContentDisposition(String contentDisposition)
    {
        this.contentDisposition = contentDisposition;
    }
    
    /**
     * 获取重写响应中的Content-Encoding头
     * @return 响应中的Content-Encoding头
     */
    public String getContentEncoding()
    {
        return contentEncoding;
    }
    
    /**
     * 设置重写响应中的Content-Encoding头
     * @param contentEncoding 响应中的Content-Encoding头
     */
    public void setContentEncoding(String contentEncoding)
    {
        this.contentEncoding = contentEncoding;
    }

    @Override
    public String toString()
    {
        return "ObjectRepleaceMetadata [contentType=" + contentType + ", contentLanguage=" + contentLanguage + ", expires=" + expires
            + ", cacheControl=" + cacheControl + ", contentDisposition=" + contentDisposition + ", contentEncoding=" + contentEncoding
            + "]";
    }
    
}

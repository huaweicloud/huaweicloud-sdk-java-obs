package com.obs.services.model;

/**
 * 桶的多版本配置
 */
public class BucketVersioningConfiguration extends HeaderResponse
{
    /**
     * 暂停状态
     */
	@Deprecated
    public static final String SUSPENDED = "Suspended";
    
    /**
     * 启用状态
     */
	@Deprecated
    public static final String ENABLED = "Enabled";
    
    private VersioningStatusEnum status;
    
    /**
     * 构造函数
     * 提示：如果一个桶的多版本状态一旦被启用，则版本状态将无法关闭，或更改为{@link #SUSPENDED}，
     * @param status 多版本状态
     * @see #ENABLED
     * @see #SUSPENDED
     */
    @Deprecated
    public BucketVersioningConfiguration(String status)
    {
        this.status = VersioningStatusEnum.getValueFromCode(status);
    }
    
    /**
     * 构造函数
     * 提示：如果一个桶的多版本状态一旦被启用，则版本状态将无法关闭，或更改为暂停状态，
     * @param status 多版本状态
     */
    public BucketVersioningConfiguration(VersioningStatusEnum status)
    {
        this.status = status;
    }
    
    public BucketVersioningConfiguration(){
        
    }
    
    /**
     * 获取多版本状态
     * @return status 多版本状态
     * @see #getVersioningStatus()
     */
    @Deprecated
    public String getStatus()
    {
        return this.status != null ? this.status.getCode() : null;
    }
    
    /**
     * 设置多版本状态
     * @param status 多版本状态
     * @see #setVersioningStatus(VersioningStatusEnum status)
     */
    @Deprecated
    public void setStatus(String status)
    {
        this.status = VersioningStatusEnum.getValueFromCode(status);
    }
    
    /**
     * 获取多版本状态
     * @return status 多版本状态
     */
    public VersioningStatusEnum getVersioningStatus()
    {
        return status;
    }
    
    /**
     * 设置多版本状态
     * @param status 多版本状态
     */
    public void setVersioningStatus(VersioningStatusEnum status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "BucketVersioningConfiguration [status=" + status + "]";
    }
    
}

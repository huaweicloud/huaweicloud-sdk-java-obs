package com.obs.services.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 桶的日志管理配置
 */
public class BucketLoggingConfiguration extends HeaderResponse
{
    private String targetBucketName;
    
    private String logfilePrefix;
    
    private String agency;
    
    private final List<GrantAndPermission> targetGrantsList = new ArrayList<GrantAndPermission>();
    
    public BucketLoggingConfiguration()
    {
    }
    
    
    /**
     * 构造函数
     * @param targetBucketName 日志目标桶
     * @param logfilePrefix 日志对象名前缀
     */
    public BucketLoggingConfiguration(String targetBucketName, String logfilePrefix)
    {
        this.targetBucketName = targetBucketName;
        this.logfilePrefix = logfilePrefix;
    }


    /**
     * 获取日志目标桶
     * @return 日志目标桶
     */
    public String getTargetBucketName()
    {
        return targetBucketName;
    }
    
    /**
     * 设置日志目标桶
     * @param targetBucketName 日志目标桶
     */
    public void setTargetBucketName(String targetBucketName)
    {
        this.targetBucketName = targetBucketName;
    }
    
    /**
     * 获取日志对象名前缀
     * @return 日志对象名前缀
     */
    public String getLogfilePrefix()
    {
        return logfilePrefix;
    }
    
    /**
     * 设置日志对象名前缀
     * @param logfilePrefix 日志对象名前缀
     */
    public void setLogfilePrefix(String logfilePrefix)
    {
        this.logfilePrefix = logfilePrefix;
    }
    
    /**
     * 获取日志对象权限组
     * @return 日志对象权限组{@link GrantAndPermission}
     */
    public GrantAndPermission[] getTargetGrants()
    {
        return targetGrantsList.toArray(new GrantAndPermission[targetGrantsList.size()]);
    }
    
    /**
     * 设置日志对象权限组
     * @param targetGrants 日志对象权限组 {@link GrantAndPermission}
     */
    public void setTargetGrants(GrantAndPermission[] targetGrants)
    {
        targetGrantsList.clear();
        targetGrantsList.addAll(Arrays.asList(targetGrants));
    }
    
    /**
     * 添加日志对象权限
     * @param targetGrant 日志对象权限
     */
    public void addTargetGrant(GrantAndPermission targetGrant)
    {
        targetGrantsList.add(targetGrant);
    }
    
    /**
     * 是否开启桶日志标识
     * @return 是否开启标识
     */
    public boolean isLoggingEnabled()
    {
        return targetBucketName != null || logfilePrefix != null || this.targetGrantsList.size() > 0;
    }


	/**
	 * 设置委托名字
	 * @return 委托名字
	 */
	public String getAgency() {
		return agency;
	}

	/**
	 * 获取委托名字
	 * @param agency 委托名字
	 */
	public void setAgency(String agency) {
		this.agency = agency;
	}


	@Override
	public String toString() {
		return "BucketLoggingConfiguration [targetBucketName=" + targetBucketName + ", logfilePrefix=" + logfilePrefix
				+ ", agency=" + agency + ", targetGrantsList=" + targetGrantsList + "]";
	}
	
}

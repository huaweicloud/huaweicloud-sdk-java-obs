package com.obs.services.model;

/**
 * 桶归档对象直读策略
 *
 */
public class BucketDirectColdAccess extends HeaderResponse {
	
	private RuleStatusEnum status;
	
	/**
	 * 构造函数
	 * @param status 桶的归档对象直读状态
	 */
	public BucketDirectColdAccess(RuleStatusEnum status) {
		this.status = status;
	}

	/**
	 * 构造函数
	 */
	public BucketDirectColdAccess() {
	}

	/**
	 * 获取桶的归档对象直读状态
	 * @return 桶的归档对象直读状态
	 */
	public RuleStatusEnum getStatus() {
		return status;
	}

	/**
	 * 设置桶的归档对象直读状态
	 * @param status 桶的归档对象直读状态
	 */
	public void setStatus(RuleStatusEnum status) {
		this.status = status;
	}
	
	@Override
    public String toString() {
        return "BucketDirectColdAccess [Status=" + status.getCode() + "]";
    }
	
}

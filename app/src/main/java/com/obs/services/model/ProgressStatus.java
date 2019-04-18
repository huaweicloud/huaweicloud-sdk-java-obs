package com.obs.services.model;

/**
 * 数据传输状态 
 *
 */
public interface ProgressStatus {
	
	/**
	 * 获取瞬时速率
	 * @return 瞬时速率
	 */
	public double getInstantaneousSpeed();
	
	/**
	 * 获取平均速率
	 * @return 平均速率
	 */
	public double getAverageSpeed();
	
	
	/**
	 * 获取传输进度
	 * @return 传输进度
	 */
	public int getTransferPercentage();
	
	/**
	 * 获取新增的字节数
	 * @return 新增的字节数
	 */
	public long getNewlyTransferredBytes();
	
	/**
	 * 获取已传输的字节数
	 * @return 已传输的字节数
	 */
	public long getTransferredBytes();
	
	/**
	 * 获取待传输的总字节数
	 * @return 待传输的总字节数
	 */
	public long getTotalBytes();
}

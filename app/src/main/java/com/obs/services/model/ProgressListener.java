package com.obs.services.model;

/**
 * 数据传输监听器
 * 
 */
public interface ProgressListener {
	
	/**
	 * 数据传输回调函数
	 * @param status 数据传输状态
	 */
	public void progressChanged(ProgressStatus status);
	
}

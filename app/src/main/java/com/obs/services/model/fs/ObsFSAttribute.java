package com.obs.services.model.fs;

import com.obs.services.model.ObjectMetadata;

/**
 * 文件/文件夹的属性
 *
 */
public class ObsFSAttribute extends ObjectMetadata{
	private int mode = -1;
	
	/**
	 * 获取文件/文件夹的类型
	 * @return 文件/文件夹的类型
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * 设置文件/文件夹的类型
	 * @param mode 文件/文件夹的类型
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}
}

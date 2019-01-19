package com.obs.services.model;

/**
 * 
 * 追加上传请求参数
 * 
 *
 */
public class AppendObjectRequest extends PutObjectRequest{
	
	protected long position;
	
	/**
	 * 获取追加上传位置
	 * @return 追加上传位置
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * 设置追加上传位置
	 * @param position 追加上传位置
	 */
	public void setPosition(long position) {
		this.position = position;
	}
	
}

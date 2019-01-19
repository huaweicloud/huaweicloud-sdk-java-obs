package com.obs.services.model;

/**
 * 列举桶的请求参数 
 *
 */
public class ListBucketsRequest {
	private boolean queryLocation = true;

	public boolean isQueryLocation() {
		return queryLocation;
	}

	/**
	 * 设置是否列出所有桶的区域信息
	 * @param queryLocation 是否列出所有桶的区域信息标识
	 */
	public void setQueryLocation(boolean queryLocation) {
		this.queryLocation = queryLocation;
	}

	@Override
	public String toString() {
		return "ListBucketsRequest [queryLocation=" + queryLocation + "]";
	}

}

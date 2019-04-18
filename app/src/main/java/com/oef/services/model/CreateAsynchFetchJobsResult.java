package com.oef.services.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obs.services.model.HeaderResponse;

/**
 * 创建异步抓取任务响应
 *
 */
public class CreateAsynchFetchJobsResult extends HeaderResponse {
	@JsonProperty(value = "id")
	private String id;

    @JsonProperty(value = "Wait")
	private int Wait;
	
	public CreateAsynchFetchJobsResult() {
		
	}
	
	/**
	 * 构造函数
	 * @param id 任务ID
	 * @param Wait 当前任务前面的排队任务数量。0表示当前任务正在进行， -1表示任务已经至少被处理过一次（可能会进入重试逻辑）。
	 */
	public CreateAsynchFetchJobsResult(String id, int Wait) {
		this.setId(id);
		this.setWait(Wait);
	}

    /**
     * 获取任务ID
     * @return 任务ID
     */
	public String getId() {
		return id;
	}

	/**
	 * 设置任务ID
	 * @param id 任务ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取排队任务数
	 * @return 排队任务数
	 */
	public int getWait() {
		return Wait;
	}

	/**
	 * 设置排队任务数
	 * @param Wait 排队任务数
	 */
	public void setWait(int Wait) {
		this.Wait = Wait;
	}
	
	@Override
	public String toString() {
		return "CreateAsynchFetchJobsResult [id=" + id + ", Wait=" + Wait + "]";
	}
}

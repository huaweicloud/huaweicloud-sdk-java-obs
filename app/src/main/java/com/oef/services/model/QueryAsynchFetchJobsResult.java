package com.oef.services.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obs.services.model.HeaderResponse;

/**
 * 查询异步抓取任务响应
 *
 */
public class QueryAsynchFetchJobsResult extends HeaderResponse {
	@JsonProperty(value = "request_Id")
	private String requestId;
	
	@JsonProperty(value = "err")
	private String err;
	
	@JsonProperty(value = "code")
	private String code;
	
	@JsonProperty(value = "status")
	private String status;
	
	@JsonProperty(value = "wait")
	private int wait;
	
	@JsonProperty(value = "job")
	private CreateAsyncFetchJobsRequest job;
	
	public QueryAsynchFetchJobsResult() {
		job = new CreateAsyncFetchJobsRequest();
	}
	
	/**
	 * 构造函数
	 * @param requestId 请求的唯一标示ID
	 * @param err 错误描述
	 * @param code 错误码
	 * @param status 任务状态
	 * @param wait 当前任务前面的排队任务数量。0表示当前任务正在进行，-1表示任务已经至少被处理过一次（可能会进入重试逻辑）。
	 * @param job 任务详情
	 */
	public QueryAsynchFetchJobsResult(String requestId, String err, String code, String status, int wait, CreateAsyncFetchJobsRequest job) {
		this.setRequestId(requestId);
		this.setErr(err);
		this.setCode(code);
		this.setStatus(status);
		this.setWait(wait);
		this.setJob(job);
	}

	/**
	 * 获取请求的唯一标示ID
	 * @return 请求的唯一标示ID
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * 设置请求的唯一标示ID
	 * @param requestId 请求的唯一标示ID
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * 获取错误描述
	 * @return 错误描述
	 */
	public String getErr() {
		return err;
	}

	/**
	 * 设置错误描述
	 * @param err 错误描述
	 */
	public void setErr(String err) {
		this.err = err;
	}

	/**
	 * 获取错误码
	 * @return 错误码
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 设置错误码
	 * @param code 错误码
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 获取任务状态
	 * @return 任务状态
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置任务状态
	 * @param status 任务状态
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 获取排队任务数量
	 * @return 排队任务数量
	 */
	public int getWait() {
		return wait;
	}

	/**
	 * 设置排队任务数量
	 * @param wait 排队任务数量
	 */
	public void setWait(int wait) {
		this.wait = wait;
	}

	/**
	 * 获取任务详情
	 * @return 任务详情
	 */
	public CreateAsyncFetchJobsRequest getJob() {
		return job;
	}

	/**
	 * 设置任务详情
	 * @param job 任务详情
	 */
	public void setJob(CreateAsyncFetchJobsRequest job) {
		this.job = job;
	}
	
	@Override
	public String toString() {
		return "QueryAsynchFetchJobsResult [requestId=" + requestId + ", err=" + err 
				+", code=" + code + ", status=" + status + ", wait=" + wait
				+ ", job url=" + job.getUrl() + ", job bucket=" + job.getBucketName() 
		        + ", job key=" + job.getObjectKey() + ", job callbackurl=" + job.getCallBackUrl() 
				+ ", job callbackbody=" + job.getCallBackBody() + "]";
	}
}

package com.oef.services.model;

import com.obs.services.model.HeaderResponse;

/**
 * 获取dis通知策略响应结果
 *
 */
public class GetDisPolicyResult extends HeaderResponse {
	private DisPolicy policy;
	
	public GetDisPolicyResult() {
		
	}
	
	public GetDisPolicyResult(DisPolicy policy) {
		this.setPolicy(policy);
	}

	public DisPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(DisPolicy policy) {
		this.policy = policy;
	}
}

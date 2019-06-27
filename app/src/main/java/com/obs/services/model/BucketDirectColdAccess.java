package com.obs.services.model;


public class BucketDirectColdAccess extends HeaderResponse {
	
	private RuleStatusEnum status;
	
	public BucketDirectColdAccess(RuleStatusEnum status) {
		this.status = status;
	}

	public BucketDirectColdAccess() {
	}

	public RuleStatusEnum getStatus() {
		return status;
	}

	public void setStatus(RuleStatusEnum status) {
		this.status = status;
	}
	
	@Override
    public String toString() {
        return "BucketDirectColdAccess [Status=" + status.getCode() + "]";
    }
	
}

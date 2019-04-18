package com.obs.services.model;

/**
 * 获取桶策略的响应结果
 *
 */
public class BucketPolicyResponse extends HeaderResponse
{
    private String policy;
    
    public BucketPolicyResponse(String policy) {
		this.policy = policy;
	}

	/**
     * 获取桶策略
     * @return 桶策略
     */
    public String getPolicy()
    {
        return policy;
    }

    @Override
    public String toString()
    {
        return "BucketPolicyResponse [policy=" + policy + "]";
    }
    
    
}

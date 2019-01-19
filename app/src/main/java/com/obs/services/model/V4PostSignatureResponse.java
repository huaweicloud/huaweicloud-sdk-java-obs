package com.obs.services.model;

/**
 * 
 * 基于浏览器表单授权访问的响应结果
 *
 */
public class V4PostSignatureResponse extends PostSignatureResponse
{
    private String algorithm;
    
    private String credential;
    
    private String date;
    
    public V4PostSignatureResponse(String policy, String originPolicy, String algorithm, String credential, String date, String signature,
        String expiration)
    {
        this.policy = policy;
        this.originPolicy = originPolicy;
        this.algorithm = algorithm;
        this.credential = credential;
        this.date = date;
        this.signature = signature;
        this.expiration = expiration;
    }
    
    /**
     * 获取签名算法
     * @return 签名算法
     */
    public String getAlgorithm()
    {
        return algorithm;
    }
    
    /**
     * 获取Credential信息
     * @return credential信息
     */
    public String getCredential()
    {
        return credential;
    }
    
    /**
     * 获取ISO 8601格式日期
     * @return ISO 8601格式日期
     */
    public String getDate()
    {
        return date;
    }
    

	@Override
	public String toString() {
		return "V4PostSignatureResponse [algorithm=" + algorithm + ", credential=" + credential + ", date=" + date
				+ ", expiration=" + expiration + ", policy=" + policy + ", originPolicy=" + originPolicy
				+ ", signature=" + signature + "]";
	}
    
}

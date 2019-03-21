/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
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

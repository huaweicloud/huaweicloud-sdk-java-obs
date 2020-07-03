/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
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
public class PostSignatureResponse {
    protected String policy;

    protected String originPolicy;

    protected String signature;

    protected String expiration;

    protected String token;

    public PostSignatureResponse() {

    }

    public PostSignatureResponse(String policy, String originPolicy, String signature, String expiration,
            String accessKey) {
        this.policy = policy;
        this.originPolicy = originPolicy;
        this.signature = signature;
        this.expiration = expiration;
        this.token = accessKey + ":" + signature + ":" + policy;
    }

    /**
     * 获取请求安全策略Base64格式
     * 
     * @return 安全策略Base64格式
     */
    public String getPolicy() {
        return policy;
    }

    /**
     * 获取请求安全策略原始格式
     * 
     * @return 安全策略原始格式
     */
    public String getOriginPolicy() {
        return originPolicy;
    }

    /**
     * 获取签名串
     * 
     * @return 签名串
     */
    public String getSignature() {
        return signature;
    }

    /**
     * 获取请求失效日期
     * 
     * @return 失效日期
     */
    public String getExpiration() {
        return expiration;
    }

    /**
     * 获取 token
     * 
     * @return token
     */
    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "PostSignatureResponse [policy=" + policy + ", originPolicy=" + originPolicy + ", signature=" + signature
                + ", expiration=" + expiration + ", token=" + token + "]";
    }

}

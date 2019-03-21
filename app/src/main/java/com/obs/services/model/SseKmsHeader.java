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
 * SSE-KMS加密头域信息
 */
public class SseKmsHeader
{
    private ServerEncryption encryption;
    
    private String kmsKeyId;
    
    private String context;
    
    /**
     * 获取加密算法类型，目前仅支持kms
     * 
     * @return 加密算法类型
     */
    public ServerEncryption getEncryption()
    {
        return encryption;
    }
    
    /**
     * 设置加密算法类型，目前仅支持kms
     * 
     * @param encryption 加密算法类型
     */
    public void setEncryption(ServerEncryption encryption)
    {
        this.encryption = encryption;
    }
    
    /**
     * 获取SSE-KMS方式下使用的主密钥，可为空，如果为空，那么默认的主密钥将会被使用
     * 
     * @return SSE-KMS方式下使用的主密钥
     */
    public String getKmsKeyId()
    {
        return kmsKeyId;
    }
    
    /**
     * 设置SSE-KMS方式下使用的主密钥，可为空，如果为空，那么默认的主密钥将会被使用
     * 
     * @param kmsKeyId SSE-KMS方式下使用的主密钥
     */
    public void setKmsKeyId(String kmsKeyId)
    {
        this.kmsKeyId = kmsKeyId;
    }
    
    @Deprecated
    public String getContext()
    {
        return context;
    }
    
    @Deprecated
    public void setContext(String context)
    {
        this.context = context;
    }

    @Override
    public String toString()
    {
        return "SseKmsHeader [encryption=" + encryption + ", kmsKeyId=" + kmsKeyId + ", context=" + context + "]";
    }
    
}

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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.obs.services.internal.Constants;

/**
 * SSE-C加解密头域信息
 */
public class SseCHeader
{
    
    private ServerAlgorithm algorithm;
    
    private SSEAlgorithmEnum sseAlgorithm = SSEAlgorithmEnum.AES256;
    
    private byte[] sseCKey;
    
    private String sseCKeyBase64;
    
    /**
     * 获取加密算法类型，目前仅支持AES256，需要和sseCKey一起使用
     * 
     * @return 加密算法类型
     */
    @Deprecated
    public ServerAlgorithm getAlgorithm()
    {
        return algorithm;
    }
    
    /**
     * 设置加密算法类型，目前仅支持AES256，需要和sseCKey一起使用
     * 
     * @param algorithm 加密算法类型
     */
    @Deprecated
    public void setAlgorithm(ServerAlgorithm algorithm)
    {
        this.algorithm = algorithm;
    }
    
    
    /**
     * 获取加密算法类型，目前仅支持AES256，需要和sseCKey一起使用
     * 
     * @return 加密算法类型
     */
    public SSEAlgorithmEnum getSSEAlgorithm() {
        return sseAlgorithm;
    }
    
    /**
     * 获取SSE-C方式下使用的密钥，用于加解密对象，该值是密钥未进行base64encode的原始值
     * 
     * @return SSE-C方式下使用的密钥
     */
    public byte[] getSseCKey()
    {
        return this.sseCKey;
    }
    
    /**
     * 设置SSE-C方式下使用的密钥，用于加解密对象，该值是密钥未进行base64encode的原始值
     * 
     * @param sseCKey SSE-C方式下使用的密钥，用于加解密对象
     */
    @Deprecated
    public void setSseCKey(String sseCKey)
    {
    	if(sseCKey != null) {
    		try {
				this.sseCKey = sseCKey.getBytes(Constants.ISO_8859_1_ENCOING);
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException("fail to read sseCkey", e);
			}
    	}
    }
    
    /**
     * 设置SSE-C方式下使用的密钥，用于加解密对象，该值是密钥未进行base64encode的原始值
     * 
     * @param sseCKey SSE-C方式下使用的密钥，用于加解密对象
     */
    public void setSseCKey(byte[] sseCKey)
    {
        this.sseCKey = sseCKey;
    }
    
    /**
     * 获取SSE-C方式下使用的密钥，用于加解密对象，该值是密钥进行base64encode后的值
     * 
     * @return SSE-C方式下使用的密钥
     */
	public String getSseCKeyBase64() {
		return sseCKeyBase64;
	}

	/**
     * 设置SSE-C方式下使用的密钥，用于加解密对象，该值是密钥进行base64encode后的值
     * 
     * @param sseCKeyBase64 SSE-C方式下使用的密钥，用于加解密对象
     */
	public void setSseCKeyBase64(String sseCKeyBase64) {
		this.sseCKeyBase64 = sseCKeyBase64;
	}

	@Override
	public String toString() {
		return "SseCHeader [algorithm=" + algorithm + ", sseCKey=" + Arrays.toString(sseCKey) + ", sseCKeyBase64=" + sseCKeyBase64 + "]";
	}
	
	
}

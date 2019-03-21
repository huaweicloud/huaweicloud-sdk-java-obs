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

import java.util.ArrayList;
import java.util.List;

/**
 * OPTIONS桶或对象的响应结果
 *
 */
public class OptionsInfoResult extends HeaderResponse
{
    private String allowOrigin;
    
    private List<String> allowHeaders;
    
    private int maxAge;
    
    private List<String> allowMethods;
    
    private List<String> exposeHeaders;
    
    
    
    public OptionsInfoResult(String allowOrigin, List<String> allowHeaders, int maxAge, List<String> allowMethods,
			List<String> exposeHeaders) {
		super();
		this.allowOrigin = allowOrigin;
		this.allowHeaders = allowHeaders;
		this.maxAge = maxAge;
		this.allowMethods = allowMethods;
		this.exposeHeaders = exposeHeaders;
	}


	/**
     * 获取允许跨域请求的来源
     * 
     * @return 允许跨域请求的来源
     */
    public String getAllowOrigin()
    {
        return allowOrigin;
    }
    
    
    /** 
     * 获取允许携带的请求头域列表
     * 
     * @return 允许携带的请求头域列表
     */
    public List<String> getAllowHeaders()
    {
        if(this.allowHeaders == null){
            allowHeaders = new ArrayList<String>();
        }
        return allowHeaders;
    }
    
    
    /** 
     * 获取客户端对请求结果的缓存时间，单位：秒
     * 
     * @return 客户端对请求结果的缓存时间
     */
    public int getMaxAge()
    {
        return maxAge;
    }
    /**
     * 获取允许的跨域请求方法列表
     * 
     * @return 允许的跨域请求方法列表
     */
    public List<String> getAllowMethods()
    {
        if(this.allowMethods == null){
            this.allowMethods = new ArrayList<String>();
        }
        return allowMethods;
    }
    
    
    /**
     * 获取允许响应中带的附加头域列表
     * 
     * @return 允许响应中带的附加头域列表
     */
    public List<String> getExposeHeaders()
    {
        if(this.exposeHeaders == null){
            this.exposeHeaders = new ArrayList<String>();
        }
        return exposeHeaders;
    }
    

    @Override
    public String toString()
    {
        return "OptionsInfoResult [allowOrigin=" + allowOrigin + ", allowHeaders=" + allowHeaders + ", maxAge=" + maxAge + ", allowMethods="
            + allowMethods + ", exposeHeaders=" + exposeHeaders + "]";
    }
    
}

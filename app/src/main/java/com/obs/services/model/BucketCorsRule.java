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

import java.util.ArrayList;
import java.util.List;

/**
 * 桶的跨域资源共享规则（CORS）
 */
public class BucketCorsRule {
    private String id;

    private int maxAgeSecond = Integer.MIN_VALUE;

    private List<String> allowedMethod;

    private List<String> allowedOrigin;

    private List<String> allowedHeader;

    private List<String> exposeHeader;

    /**
     * 获取跨域规则ID
     * 
     * @return 跨域规则ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置跨域规则ID
     * 
     * @param id
     *            跨域规则ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取客户端对请求结果的缓存时间，单位：秒
     * 
     * @return 缓存时间
     */
    public int getMaxAgeSecond() {
        if (this.maxAgeSecond == Integer.MIN_VALUE) {
            return 0;
        }
        return maxAgeSecond;
    }

    /**
     * 设置客户端对请求结果的缓存时间，单位：秒
     * 
     * @param maxAgeSecond
     *            缓存时间
     */
    public void setMaxAgeSecond(int maxAgeSecond) {
        this.maxAgeSecond = maxAgeSecond;
    }

    /**
     * 获取跨域规则中允许的方法列表
     * 
     * @return 方法列表
     */
    public List<String> getAllowedMethod() {
        if (null == allowedMethod) {
            return allowedMethod = new ArrayList<String>();
        }
        return allowedMethod;
    }

    /**
     * 设置跨域规则中允许的方法列表，允许值（GET/PUT/DELETE/POST/HEAD）
     * 
     * @param allowedMethod
     *            方法列表
     */
    public void setAllowedMethod(List<String> allowedMethod) {
        this.allowedMethod = allowedMethod;
    }

    /**
     * 获取跨域规则中允许的请求来源列表（表示域名的字符串）
     * 
     * @return 请求来源列表
     */
    public List<String> getAllowedOrigin() {
        if (null == allowedOrigin) {
            return allowedOrigin = new ArrayList<String>();
        }
        return allowedOrigin;
    }

    /**
     * 设置跨域规则中允许的请求来源列表（表示域名的字符串）
     * 
     * @param allowedOrigin
     *            请求来源列表
     */
    public void setAllowedOrigin(List<String> allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }

    /**
     * 获取跨域规则中允许携带的请求头域列表
     * 
     * @return 请求头域列表
     */
    public List<String> getAllowedHeader() {
        if (null == allowedHeader) {
            return allowedHeader = new ArrayList<String>();
        }
        return allowedHeader;
    }

    /**
     * 设置跨域规则中允许携带的请求头域列表
     * 
     * @param allowedHeader
     *            请求头域列表
     */
    public void setAllowedHeader(List<String> allowedHeader) {
        this.allowedHeader = allowedHeader;
    }

    /**
     * 获取跨域规则允许响应中带的附加头域列表
     * 
     * @return 附加头域列表
     */
    public List<String> getExposeHeader() {
        if (null == exposeHeader) {
            return exposeHeader = new ArrayList<String>();
        }
        return exposeHeader;
    }

    /**
     * 设置跨域规则允许响应中带的附加头域列表
     * 
     * @param exposeHeader
     *            附加头域列表
     */
    public void setExposeHeader(List<String> exposeHeader) {
        this.exposeHeader = exposeHeader;
    }

    @Override
    public String toString() {
        return "BucketCorsRule [id=" + id + ", maxAgeSecond=" + maxAgeSecond + ", allowedMethod=" + allowedMethod
                + ", allowedOrigin=" + allowedOrigin + ", allowedHeader=" + allowedHeader + ", exposeHeader="
                + exposeHeader + "]";
    }

}

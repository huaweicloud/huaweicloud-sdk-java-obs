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
 * OPTIONS桶或对象的请求参数
 * 
 */
public class OptionsInfoRequest extends GenericRequest {
    private String origin;

    private List<String> requestMethod;

    private List<String> requestHeaders;

    /**
     * 获取预请求的来源
     * 
     * @return 预请求的来源
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * 设置预请求的来源
     * 
     * @param origin
     *            预请求的来源
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * 获取允许的跨域请求方法列表
     * 
     * @return 允许的跨域请求方法列表
     */
    public List<String> getRequestMethod() {
        if (this.requestMethod == null) {
            this.requestMethod = new ArrayList<String>();
        }
        return requestMethod;
    }

    /**
     * 设置允许的跨域请求方法列表
     * 
     * @param requestMethod
     *            允许的跨域请求方法列表
     */
    public void setRequestMethod(List<String> requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * 获取允许携带的请求头域列表
     * 
     * @return 允许携带的请求头域列表
     */
    public List<String> getRequestHeaders() {
        if (this.requestHeaders == null) {
            this.requestHeaders = new ArrayList<String>();
        }
        return requestHeaders;
    }

    /**
     * 设置允许携带的请求头域列表
     * 
     * @param requestHeaders
     *            允许携带的请求头域列表
     */
    public void setRequestHeaders(List<String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    @Override
    public String toString() {
        return "OptionsInfoRequest [origin=" + origin + ", requestMethod=" + requestMethod + ", requestHeaders="
                + requestHeaders + "]";
    }

}

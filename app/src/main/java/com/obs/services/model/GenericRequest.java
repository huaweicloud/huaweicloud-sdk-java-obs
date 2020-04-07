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
 * 所有请求的基础类，封装了所有请求都会用到的参数
 * 
 * @since 3.20.3
 */
public class GenericRequest {
    /**
     * 如果启用了请求者付费，则向请求者付费的桶执行操作的时候，由请求者支付费用。
     */
    private boolean isRequesterPays;
    
    /**
     * 如果允许请求者支付费用，则返回true，否则返回false。
     *
     * <p>
     * 如果一个桶开启了请求者付费，非桶拥有者请求该桶的时候，必须将该属性设置为true，否则请求将返回403。
     *
     * <p>
     * 启用请求者付费后，将禁用对此桶的匿名访问。
     *
     * @return 如果允许请求者支付费用，则返回true，否则返回false。
     */
    public boolean isRequesterPays() {
        return isRequesterPays;
    }

    /**
     * 用于设置是否允许启用请求者付费。
     *
     * <p>
     * 如果一个桶开启了请求者付费，非桶拥有者请求该桶的时候，必须将该属性设置为true，否则请求将返回403。
     *
     * <p>
     * 启用请求者付费后，将禁用对此桶的匿名访问。
     *
     * @param isRequesterPays 如果允许启用请求者付费，则设置为true，否则设置为false
     */
    public void setRequesterPays(boolean isRequesterPays) {
        this.isRequesterPays = isRequesterPays;
    }

    @Override
    public String toString() {
        return "GenericRequest [isRequesterPays=" + isRequesterPays + "]";
    }
}

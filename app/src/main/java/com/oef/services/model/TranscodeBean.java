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

package com.oef.services.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 异步转码策略内容
 *
 */
public class TranscodeBean {
    @JsonProperty(value = "status")
    private String status;

    @JsonProperty(value = "agency")
    private String agency;

    public TranscodeBean() {

    }

    /**
     * 构造函数
     * 
     * @param status
     *            策略状态
     * @param agency
     *            IAM 委托名
     */
    public TranscodeBean(String status, String agency) {
        this.status = status;
        this.agency = agency;
    }

    /**
     * 获取策略状态
     * 
     * @return 策略状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置策略状态
     * 
     * @param status
     *            策略状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取IAM 委托名
     * 
     * @return IAM 委托名
     */
    public String getAgency() {
        return agency;
    }

    /**
     * 设置IAM 委托名
     * 
     * @param agency
     *            IAM 委托名
     */
    public void setAgency(String agency) {
        this.agency = agency;
    }

    @Override
    public String toString() {
        return "TranscodeBean [status=" + status + ", agency=" + agency + "]";
    }
}

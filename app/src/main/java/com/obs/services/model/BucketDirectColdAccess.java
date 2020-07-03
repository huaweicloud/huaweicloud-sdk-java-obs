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
 * 桶归档对象直读策略
 *
 */
public class BucketDirectColdAccess extends HeaderResponse {

    private RuleStatusEnum status;

    /**
     * 构造函数
     * 
     * @param status
     *            桶的归档对象直读状态
     */
    public BucketDirectColdAccess(RuleStatusEnum status) {
        this.status = status;
    }

    /**
     * 构造函数
     */
    public BucketDirectColdAccess() {
    }

    /**
     * 获取桶的归档对象直读状态
     * 
     * @return 桶的归档对象直读状态
     */
    public RuleStatusEnum getStatus() {
        return status;
    }

    /**
     * 设置桶的归档对象直读状态
     * 
     * @param status
     *            桶的归档对象直读状态
     */
    public void setStatus(RuleStatusEnum status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BucketDirectColdAccess [Status=" + status.getCode() + "]";
    }

}

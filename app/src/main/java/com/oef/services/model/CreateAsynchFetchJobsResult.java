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
import com.obs.services.model.HeaderResponse;

/**
 * 创建异步抓取任务响应
 *
 */
public class CreateAsynchFetchJobsResult extends HeaderResponse {
    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "Wait")
    private int wait;

    public CreateAsynchFetchJobsResult() {

    }

    /**
     * 构造函数
     * 
     * @param id
     *            任务ID
     * @param wait
     *            当前任务前面的排队任务数量。0表示当前任务正在进行， -1表示任务已经至少被处理过一次（可能会进入重试逻辑）。
     */
    public CreateAsynchFetchJobsResult(String id, int wait) {
        this.setId(id);
        this.setWait(wait);
    }

    /**
     * 获取任务ID
     * 
     * @return 任务ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置任务ID
     * 
     * @param id
     *            任务ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取排队任务数
     * 
     * @return 排队任务数
     */
    public int getWait() {
        return wait;
    }

    /**
     * 设置排队任务数
     * 
     * @param wait
     *            排队任务数
     */
    public void setWait(int wait) {
        this.wait = wait;
    }

    @Override
    public String toString() {
        return "CreateAsynchFetchJobsResult [id=" + id + ", Wait=" + wait + "]";
    }
}

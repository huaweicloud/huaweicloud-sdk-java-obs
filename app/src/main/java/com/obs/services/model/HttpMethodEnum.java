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
 * HTTP/HTTPS请求方法
 */
public enum HttpMethodEnum {
    /**
     * GET方法，一般用于查询
     */
    GET("Get"),

    /**
     * PUT方法，一般用于新增、修改
     */
    PUT("Put"),

    /**
     * POST方法，一般用于新增
     */
    POST("Post"),

    /**
     * DELETE方法，一般用于删除
     */
    DELETE("Delete"),

    /**
     * HEAD方法，一般用于查询响应头
     */
    HEAD("Head"),

    /**
     * OPTIONS方法，一般用于预请求
     */
    OPTIONS("Options");

    private String operationType;

    private HttpMethodEnum(String operationType) {
        if (operationType == null) {
            throw new IllegalArgumentException("operation type code is null");
        }
        this.operationType = operationType;
    }

    public String getOperationType() {
        return this.operationType.toUpperCase();
    }

    public static HttpMethodEnum getValueFromStringCode(String operationType) {
        if (operationType == null) {
            throw new IllegalArgumentException("operation type is null");
        }

        for (HttpMethodEnum installMode : HttpMethodEnum.values()) {
            if (installMode.getOperationType().equals(operationType.toUpperCase())) {
                return installMode;
            }
        }

        throw new IllegalArgumentException("operation type is illegal");
    }
}

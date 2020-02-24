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

import com.obs.services.exception.ObsException;

/**
 * 任务执行回调
 */
public interface TaskCallback<K, V> {

    /**
     * 当任务执行成功时回调
     *
     * @param result 回调参数，通常使用具体操作的返回类型
     */
    void onSuccess(K result);

    /**
     * 当任务执行抛出异常时回调
     *
     * @param exception     异常信息
     * @param singleRequest 引发异常的单次请求
     * 
     */
    void onException(ObsException exception, V singleRequest);
}

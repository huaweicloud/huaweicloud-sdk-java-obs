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

package com.oef.services;

import java.io.IOException;
import com.obs.services.exception.ObsException;
import com.obs.services.model.HeaderResponse;
import com.oef.services.model.CreateAsyncFetchJobsRequest;
import com.oef.services.model.CreateAsynchFetchJobsResult;
import com.oef.services.model.PutExtensionPolicyRequest;
import com.oef.services.model.QueryExtensionPolicyResult;
import com.oef.services.model.QueryAsynchFetchJobsResult;

/**
 * OEF增值服务接口
 *
 */
public interface IOefClient {
    /**
     * 关闭OEF客户端，释放连接资源
     * 
     * @throws IOException
     *             IO异常，当关闭资源失败时抛出该异常
     */
    void close() throws IOException;

    /**
     * 配置异步策略
     * 
     * @param bucketName
     *            桶名
     * @param request
     *            异步策略
     * @return 公共响应头消息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse putExtensionPolicy(final String bucketName, final PutExtensionPolicyRequest request)
            throws ObsException;

    /**
     * 查询异步策略
     * 
     * @param bucketName
     *            桶名
     * @return ExtensionPolicyResult
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    QueryExtensionPolicyResult queryExtensionPolicy(final String bucketName) throws ObsException;

    /**
     * 删除异步策略
     * 
     * @param bucketName
     *            桶名
     * @return 公共响应头消息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse deleteExtensionPolicy(final String bucketName) throws ObsException;

    /**
     * 创建异步抓取任务
     * 
     * @param request
     *            异步抓取任务
     * @return CreateAsynchFetchJobsResult
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    CreateAsynchFetchJobsResult createFetchJob(final CreateAsyncFetchJobsRequest request) throws ObsException;

    /**
     * 查询异步抓取任务
     * 
     * @param bucketName
     *            桶名
     * @param jobId
     *            任务ID
     * @return QueryAsynchFetchJobsResult
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    QueryAsynchFetchJobsResult queryFetchJob(final String bucketName, final String jobId) throws ObsException;
}

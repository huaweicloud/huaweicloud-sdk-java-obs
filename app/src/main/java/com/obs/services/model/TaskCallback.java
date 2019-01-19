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

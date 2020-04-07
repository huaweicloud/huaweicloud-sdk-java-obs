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

import java.util.concurrent.atomic.AtomicInteger;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;

/**
 * 可监控子进程运行情况的数据传输监听器实现类
 * 
 * @since 3.20.3
 */
public abstract class MonitorableProgressListener implements ProgressListener {
    private static final ILogger ILOG = LoggerBuilder.getLogger(MonitorableProgressListener.class);

    // 用于记录正在运行的子任务的个数
    private AtomicInteger runningTask = new AtomicInteger(1);

    /**
     * 判断请求任务是否还在运行<br>
     * <br>
     * 该方法通常用于当前线程执行了interrupt()方法后，父线程监控当前线程是否已彻底结束运行 <br>
     * 
     * @return 如何还有子任务还在运行，则返回true；否则返回false；
     * @since 3.20.3
     */
    public final boolean isRunning() {
        return this.runningTask.get() > 0;
    }

    /**
     * 等待请求任务彻底执行结束，在任务彻底结束前，该方法会一直阻塞<br>
     * <br>
     * 该方法通常用于当前线程执行了interrupt()方法后，等待上传子任务彻底执行完成 <br>
     * 
     * @return 如果已正常结束，则返回true，否则返回false；
     * @since 3.20.3
     * @throws InterruptedException
     *             当线程在活动之前或活动期间处于正在等待、休眠或占用状态且该线程被中断时，抛出该异常
     */
    public final boolean waitingFinish() throws InterruptedException {
        return waitingFinish(-1L);
    }

    /**
     * 等待请求任务彻底执行结束，在任务彻底结束前，该方法会一直阻塞，只到超过设置的超时时间<br>
     * <br>
     * 该方法通常用于当前线程执行了interrupt()方法后，等待上传子任务彻底执行完成 <br>
     * 
     * @param timeout
     *            等待的超时时间，单位为毫秒；如果参数小于登录0，则表示永不超时
     * @return 如果已正常结束，则返回true，否则返回false；
     * @since 3.20.3
     * @throws InterruptedException
     *             当线程在活动之前或活动期间处于正在等待、休眠或占用状态且该线程被中断时，抛出该异常
     */
    public final boolean waitingFinish(long timeout) throws InterruptedException {
        long start = System.currentTimeMillis();
        if (ILOG.isDebugEnabled()) {
            ILOG.debug("this.runningTask = " + this.runningTask);
        }
        while (this.runningTask.get() > 0) {
            if (System.currentTimeMillis() - start > timeout && timeout > 0) {
                if (ILOG.isWarnEnabled()) {
                    ILOG.warn("DownloadFileReqeust is not finish. " + this.toString());
                }
                return false;
            }

            Thread.sleep(100L);
        }

        return true;
    }

    /**
     * 启动一个子任务<br>
     * <br>
     * <b>请注意：通常情况下，不建议用户自己调用该方法，如果随意调用，会导致waitingFinish、isRunning等方法失效；
     * SDK内部会通过该方法调整正在运行的子任务数，用于后继判断请求任务是否已彻底结束</b><br>
     * <br>
     * <b>参考：</b>{@link #waitingFinish(long)}、{@link #isRunning()}
     * 
     * @since 3.20.3
     */
    public final void startOneTask() {
        this.runningTask.incrementAndGet();
    }

    /**
     * 结束一个子任务<br>
     * <br>
     * <b>请注意：通常情况下，不建议用户自己调用该方法，如果随意调用，会导致waitingFinish、isRunning等方法失效；
     * SDK内部会通过该方法调整正在运行的子任务数，用于后继判断请求任务是否已彻底结束</b><br>
     * <br>
     * <b>参考：</b>{@link #waitingFinish(long)}、{@link #isRunning()}
     * 
     * @since 3.20.3
     */
    public final void finishOneTask() {
        this.runningTask.decrementAndGet();
    }

    /**
     * 重置监听器<br>
     * <br>
     * 通常在一个请求被反复使用的时候使用
     * 
     * @since 3.20.3
     */
    public final void reset() {
        this.runningTask.set(1);
    }
}

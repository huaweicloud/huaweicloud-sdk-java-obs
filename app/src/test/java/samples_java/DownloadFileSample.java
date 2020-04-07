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

package samples_java;

import java.io.IOException;
import java.util.Date;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.DownloadFileRequest;
import com.obs.services.model.MonitorableProgressListener;
import com.obs.services.model.ProgressStatus;

public class DownloadFileSample {
    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";
    
    private static ObsClient obsClient;

    private static String bucketName = "my-obs-bucket-demo";

    private static String objectKey = "my-obs-object-key-demo";

    private static String localSavePath = "local save path";
    
    public static void main(String[] args) {
        
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);

        try {
            /*
             * Constructs a obs client instance with your account for accessing OBS
             */
            obsClient = new ObsClient(ak, sk, config);

            DownloadFileRequest request = new DownloadFileRequest(bucketName, objectKey);
            // 设置下载对象的本地文件路径
            request.setDownloadFile(localSavePath);
            // 设置分段下载时的最大并发数
            request.setTaskNum(5);
            // 设置分段大小为1MB
            request.setPartSize(1 * 1024 * 1024);
            // 开启断点续传模式
            request.setEnableCheckpoint(true);
            // 每100KB触发一次监听器回调
            request.setProgressInterval(100 * 1024L);

            MonitorableProgressListener progressListener = new MonitorableProgressListener() {
                @Override
                public void progressChanged(ProgressStatus status) {
                    System.out.println(new Date() + "  TransferPercentage:" + status.getTransferPercentage());
                }
            };
            // 设置一个可监控子进程运行情况的数据传输监听器
            request.setProgressListener(progressListener);

            // 启动一个线程，用于下载对象
            DownloadFileManager downloadManager = new DownloadFileManager(obsClient, request, progressListener);
            downloadManager.download();

            // 等待3秒钟
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 中断线程，模拟暂停该下载任务
            // 注意：该方法调用后，并不意味着下载任务已立即停止，建议调用progressListener.waitingFinish方法，等待任务彻底结束
            // 另外，该方法调用后，日志中可能会出现"java.lang.RuntimeException: Abort io due to thread interrupted"异常信息，属于中断线程后的正常现象，可以忽略该异常
            downloadManager.pause();
            System.out.println(new Date() + "  download thread is stop. \n");
            
            // 等待3秒钟
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println(new Date() + "  restart request. \n");
            downloadManager.download();

            // 等待下载任务彻底结束
            downloadManager.waitingFinish();
        } catch (ObsException e) {
            System.out.println("Response Code: " + e.getResponseCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Error Code:       " + e.getErrorCode());
            System.out.println("Request ID:      " + e.getErrorRequestId());
            System.out.println("Host ID:           " + e.getErrorHostId());
        } catch (InterruptedException e) {

        } finally {
            if (obsClient != null) {
                try {
                    /*
                     * Close obs client
                     */
                    obsClient.close();
                } catch (IOException e) {
                }
            }
        }
    }
}

class DownloadFileManager {
    private DownloadFileRequest request;
    private ObsClient obsClient;
    private MonitorableProgressListener progressListener;
    private Thread currentThread;
    
    public DownloadFileManager(ObsClient obsClient, DownloadFileRequest request, MonitorableProgressListener progressListener) {
        this.obsClient = obsClient;
        this.request = request;
        this.progressListener = progressListener;
        request.setProgressListener(progressListener);
    }

    /**
     * 启动下载，新启动一个线程后台运行
     */
    public void download() {
        this.progressListener.reset();
        this.currentThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 进行断点续传下载
                    obsClient.downloadFile(request);
                } catch (ObsException e) {
                    // 发生异常时可再次调用断点续传下载接口进行重新下载
                    if (null != e.getCause()
                            && e.getCause() instanceof InterruptedException) {
                        System.out.println(new Date() + "  current thread is interrupted. \n");
                    } else {
                        e.printStackTrace();
                    }
                } 
            }
        });
        
        this.currentThread.start();
    }
    
    /**
     * 暂停任务
     * @throws InterruptedException
     */
    public void pause() throws InterruptedException {
        // 中断线程，模拟暂停该下载任务
        // 注意：该方法调用后，并不意味着下载任务已立即停止，建议调用progressListener.waitingFinish方法，等待任务彻底结束
        // 另外，该方法调用后，日志中可能会出现"java.lang.RuntimeException: Abort io due to thread interrupted"异常信息，属于中断线程后的正常现象，可以忽略该异常
        this.currentThread.interrupt();
        
        // 等待下载任务彻底结束
        this.progressListener.waitingFinish();
    }
    
    /**
     * 等待下载任务结束
     * @throws InterruptedException
     */
    public void waitingFinish() throws InterruptedException {
        this.currentThread.join();
    }
}

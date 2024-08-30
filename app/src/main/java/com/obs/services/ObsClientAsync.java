package com.obs.services;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.task.UploadFileTask;
import com.obs.services.model.CompleteMultipartUploadResult;
import com.obs.services.model.TaskCallback;
import com.obs.services.model.UploadFileRequest;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ObsClientAsync extends ObsClient implements IObsClientAsync {
    /**
     * Constructor
     *
     * @param endPoint OBS endpoint
     */
    public ObsClientAsync(String endPoint) {
        super(endPoint);
    }

    /**
     * Constructor
     *
     * @param config Configuration parameters of ObsClient
     */
    public ObsClientAsync(ObsConfiguration config) {
        super(config);
    }

    /**
     * Constructor
     *
     * @param accessKey AK in the access key
     * @param secretKey SK in the access key
     * @param endPoint  OBS endpoint
     */
    public ObsClientAsync(String accessKey, String secretKey, String endPoint) {
        super(accessKey, secretKey, endPoint);
    }

    /**
     * Constructor
     *
     * @param accessKey AK in the access key
     * @param secretKey SK in the access key
     * @param config    Configuration parameters of ObsClient
     */
    public ObsClientAsync(String accessKey, String secretKey, ObsConfiguration config) {
        super(accessKey, secretKey, config);
    }

    /**
     * Constructor
     *
     * @param accessKey     AK in the temporary access key
     * @param secretKey     SK in the temporary access key
     * @param securityToken Security token
     * @param endPoint      OBS endpoint
     */
    public ObsClientAsync(String accessKey, String secretKey, String securityToken, String endPoint) {
        super(accessKey, secretKey, securityToken, endPoint);
    }

    /**
     * Constructor
     *
     * @param accessKey     AK in the temporary access key
     * @param secretKey     SK in the temporary access key
     * @param securityToken Security token
     * @param config        Configuration parameters of ObsClient
     */
    public ObsClientAsync(String accessKey, String secretKey, String securityToken, ObsConfiguration config) {
        super(accessKey, secretKey, securityToken, config);
    }

    public ObsClientAsync(IObsCredentialsProvider provider, String endPoint) {
        super(provider, endPoint);
    }

    public ObsClientAsync(IObsCredentialsProvider provider, ObsConfiguration config) {
        super(provider, config);
    }

    private static final ILogger log = LoggerBuilder.getLogger(ObsClientAsync.class);
    private ExecutorService asyncClientExecutorService;
    private static final int DEFAULT_CLIENT_EXECUTOR_SERVICE_SIZE = 128;

    private int queryInterval = 1000;

    @Override
    public void close() throws IOException {
        log.warn("ObsClientAsync closing");
        try {
            // finishing all task
            getExecutorService().shutdown();
            log.warn("ObsClientAsync closed");
        } catch (Exception e) {
            log.warn("ObsClientAsync close failed, detail:", e);
        }
        super.close();
    }

    private static final String ASYNC_CLIENT_EXECUTOR_SERVICE_THREAD_NAME = "async-client-thread";
    protected ExecutorService getExecutorService() {
        if (asyncClientExecutorService == null) {
            asyncClientExecutorService = Executors.newFixedThreadPool(DEFAULT_CLIENT_EXECUTOR_SERVICE_SIZE,
                    r -> new Thread(r, ASYNC_CLIENT_EXECUTOR_SERVICE_THREAD_NAME));
        }
        return asyncClientExecutorService;
    }

    public void setExecutorService(ExecutorService service) {
        if (asyncClientExecutorService != null) { // wait for all task finish
            asyncClientExecutorService.shutdown();
            while (!asyncClientExecutorService.isTerminated()) {
                try {
                    Thread.sleep(queryInterval);
                } catch (InterruptedException e) {
                    log.warn("ObsClientAsync setExecutorService failed, detail:", e);
                }
            }
        }
        asyncClientExecutorService = service;
    }

    public int getQueryInterval() {
        return queryInterval;
    }

    public void setQueryInterval(int queryInterval) {
        this.queryInterval = queryInterval;
    }

    /**
     * @param uploadFileRequest
     * @param completeCallback
     * @return
     */
    @Override
    public UploadFileTask uploadFileAsync(
            UploadFileRequest uploadFileRequest,
            TaskCallback<CompleteMultipartUploadResult, UploadFileRequest> completeCallback) {
        log.debug("start uploadFileAsync");
        if (uploadFileRequest.getCancelHandler() != null) {
            uploadFileRequest.getCancelHandler().resetCancelStatus();
        }
        UploadFileTask uploadFileTask =
                new UploadFileTask(this, uploadFileRequest.getBucketName(), uploadFileRequest, completeCallback);
        Future<?> future = getExecutorService().submit((Callable<?>) uploadFileTask);

        uploadFileTask.setResultFuture(future);
        return uploadFileTask;
    }
}

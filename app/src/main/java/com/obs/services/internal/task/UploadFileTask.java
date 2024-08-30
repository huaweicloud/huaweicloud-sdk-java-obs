package com.obs.services.internal.task;

import static com.obs.services.internal.utils.ServiceUtils.changeFromThrowable;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.AbstractClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.CompleteMultipartUploadResult;
import com.obs.services.model.TaskCallback;
import com.obs.services.model.UploadFileRequest;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class UploadFileTask extends AbstractTaskCallable<CompleteMultipartUploadResult> {
    private UploadFileRequest taskRequest;
    private TaskCallback<CompleteMultipartUploadResult, UploadFileRequest> completeCallback;

    private Future<?> resultFuture;

    private static final ILogger log = LoggerBuilder.getLogger(UploadFileTask.class);

    public UploadFileTask(
            AbstractClient obsClient,
            String bucketName,
            UploadFileRequest taskRequest,
            TaskCallback<CompleteMultipartUploadResult, UploadFileRequest> completeCallback) {
        super(obsClient, bucketName);
        this.taskRequest = taskRequest;
        this.completeCallback = completeCallback;
    }

    public Optional<CompleteMultipartUploadResult> getResult() {
        try {
            Object result = resultFuture.get();
            if (result instanceof CompleteMultipartUploadResult) {
                return Optional.of((CompleteMultipartUploadResult) result);
            } else {
                String errorMsg = "UploadFileTask Error, result is " +
                        (result != null ? "not instance of CompleteMultipartUploadResult!" : "null");
                errorMsg += (taskRequest.getCancelHandler() != null && taskRequest.getCancelHandler().isCancelled()) ?
                        ", uploadFileRequest is canceled." : "";
                log.error(errorMsg);
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("UploadFileTask Error:" , e);
            return Optional.empty();
        }
    }

    public void setResultFuture(Future<?> future) {
        resultFuture = future;
    }

    public boolean cancel() {
        if (taskRequest.getCancelHandler() != null) {
            taskRequest.getCancelHandler().cancel();
            return true;
        } else {
            String errorInfo = "UploadFileTask Cancel Error: CancelHandler is null, can not cancel!";
            log.error(errorInfo);
            return false;
        }
    }

    protected CompleteMultipartUploadResult uploadFileWithCallBack() {
        try {
            CompleteMultipartUploadResult uploadFileResult = getObsClient().uploadFile(taskRequest);
            completeCallback.onSuccess(uploadFileResult);
            return uploadFileResult;
        } catch (ObsException e) {
            completeCallback.onException(e, taskRequest);
        } catch (Throwable t) {
            completeCallback.onException(changeFromThrowable(t), taskRequest);
        }
        return null;
    }

    public boolean isTaskFinished() {
        return resultFuture.isDone();
    }

    public void waitUntilFinished() {
        try {
            resultFuture.get();
        } catch (Throwable t) {
            log.warn("UploadFileTask waitUntilFinished Error:", t);
        }
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Object call() throws Exception {
        return uploadFileWithCallBack();
    }
}

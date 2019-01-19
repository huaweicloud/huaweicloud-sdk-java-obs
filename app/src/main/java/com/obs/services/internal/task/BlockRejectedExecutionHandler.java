package com.obs.services.internal.task;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import com.obs.services.exception.ObsException;
import com.obs.services.model.RestoreObjectRequest;
import com.obs.services.model.RestoreObjectResult;
import com.obs.services.model.TaskCallback;

public class BlockRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            ObsException obsException = new ObsException(e.getMessage(), e);
            if (r instanceof RestoreObjectTask) {
                RestoreObjectTask task = (RestoreObjectTask) r;
                TaskCallback<RestoreObjectResult, RestoreObjectRequest> callback = task.getCallback();
                callback.onException(obsException, task.getTaskRequest());
            }
        }
    }
}

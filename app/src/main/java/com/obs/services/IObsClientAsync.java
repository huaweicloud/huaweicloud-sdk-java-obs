package com.obs.services;

import com.obs.services.internal.task.UploadFileTask;
import com.obs.services.model.CompleteMultipartUploadResult;
import com.obs.services.model.TaskCallback;
import com.obs.services.model.UploadFileRequest;

public interface IObsClientAsync {
    UploadFileTask uploadFileAsync(
            UploadFileRequest uploadFileRequest,
            TaskCallback<CompleteMultipartUploadResult, UploadFileRequest> completeCallback);
}

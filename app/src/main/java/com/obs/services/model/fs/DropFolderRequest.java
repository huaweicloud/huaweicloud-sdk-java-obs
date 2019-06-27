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
**/
package com.obs.services.model.fs;

import com.obs.services.model.AbstractBulkRequest;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.TaskCallback;


public class DropFolderRequest extends AbstractBulkRequest {

    private String folderName;

    private TaskCallback<DeleteObjectResult, String> callback;

    public DropFolderRequest() {
    }


    public DropFolderRequest(String bucketName) {
        super(bucketName);
    }

    public DropFolderRequest(String bucketName, String folderName) {
        super(bucketName);
        this.folderName = folderName;
    }


    public String getFolderName() {
        return folderName;
    }


    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }


    public TaskCallback<DeleteObjectResult, String> getCallback() {
        return callback;
    }


    public void setCallback(TaskCallback<DeleteObjectResult, String> callback) {
        this.callback = callback;
    }

    @Override
    public String toString() {
        return "DropFolderRequest [bucketName=" + bucketName + ", folderName=" + folderName + "]";
    }
}

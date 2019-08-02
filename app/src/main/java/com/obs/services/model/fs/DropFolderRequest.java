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

/**
 * Request parameters for deleting folders. Deleting a folder will delete all sub-folders and files in the folder.
 * This function does not support buckets with versioning enabled.
 */
public class DropFolderRequest extends AbstractBulkRequest {

    private String folderName;

    private TaskCallback<DeleteObjectResult, String> callback;

    public DropFolderRequest() {
    }

    /**
     * Constructor
     * 
     * @param bucketName Bucket name
     */
    public DropFolderRequest(String bucketName) {
        super(bucketName);
    }

    public DropFolderRequest(String bucketName, String folderName) {
        super(bucketName);
        this.folderName = folderName;
    }

    /**
     * Obtain the folder name.
     * 
     * @return Folder name
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * Set the folder name.
     * 
     * @param folderName Folder name
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * Obtain the callback object of a batch task.
     * 
     * @return Callback object
     */
    public TaskCallback<DeleteObjectResult, String> getCallback() {
        return callback;
    }

    /**
     * Set the callback object of a batch task.
     * 
     * @param callback Callback object
     */
    public void setCallback(TaskCallback<DeleteObjectResult, String> callback) {
        this.callback = callback;
    }

    @Override
    public String toString() {
        return "DropFolderRequest [bucketName=" + bucketName + ", folderName=" + folderName + "]";
    }
}

/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */


package com.obs.services.model;

public class AbstractMultipartRequest extends BaseObjectRequest {

    protected String uploadId;

    public AbstractMultipartRequest() {
    }

    public AbstractMultipartRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取分段上传任务的ID号
     *
     * @return 分段上传任务的ID号
     */
    public String getUploadId() {
        return uploadId;
    }

    /**
     * 设置分段上传任务的ID号
     *
     * @param uploadId
     *            分段上传任务的ID号
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

}

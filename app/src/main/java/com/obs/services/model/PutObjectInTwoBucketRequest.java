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

public class PutObjectInTwoBucketRequest {
    private PutObjectRequest mainBucketRequest;
    private PutObjectRequest backupBucketRequest;
    private String filePath;

    public PutObjectRequest getMainBucketRequest() {
        return mainBucketRequest;
    }

    public void setMainBucketRequest(PutObjectRequest mainBucketRequest) {
        this.mainBucketRequest = mainBucketRequest;
    }

    public PutObjectRequest getBackupBucketRequest() {
        return backupBucketRequest;
    }

    public void setBackupBucketRequest(PutObjectRequest backupBucketRequest) {
        this.backupBucketRequest = backupBucketRequest;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "PutObjectInTwoBucketRequest{" +
                "mainBucketRequest=" + mainBucketRequest +
                ", backupBucketRequest=" + backupBucketRequest +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}

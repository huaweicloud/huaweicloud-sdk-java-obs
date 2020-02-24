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

/**
 * 初始化分段上传任务的响应结果
 */
public class InitiateMultipartUploadResult extends HeaderResponse
{
    private String uploadId;
    
    private String bucketName;
    
    private String objectKey;
    
    
    public InitiateMultipartUploadResult(String bucketName, String objectKey, String uploadId)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.uploadId = uploadId;
    }
    
    /**
     * 获取分段上传任务的ID号
     * 
     * @return 分段上传任务的ID号
     */
    public String getUploadId()
    {
        return uploadId;
    }
    
    
    /**
     * 获取分段上传任务所属的桶名
     * 
     * @return 分段上传任务所属的桶名
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    
    /**
     * 获取分段上传任务所属的对象名
     * 
     * @return 分段上传任务所属的对象名
     */
    public String getObjectKey()
    {
        return objectKey;
    }
    


    @Override
    public String toString()
    {
        return "InitiateMultipartUploadResult [uploadId=" + uploadId + ", bucketName=" + bucketName + ", objectKey=" + objectKey +  "]";
    }
    
}

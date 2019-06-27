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

package com.obs.services.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Parameters in a request for combining parts
 */
public class CompleteMultipartUploadRequest
{
    private String uploadId;
    
    private String bucketName;
    
    private String objectKey;
    
    private List<PartEtag> partEtag;
    
    public CompleteMultipartUploadRequest(){
        
    }
    
    /**
     * Constructor
     * @param bucketName Bucket name
     * @param objectKey Object name
     * @param uploadId Multipart upload ID
     * @param partEtag List of parts to be combined
     */
    public CompleteMultipartUploadRequest(String bucketName, String objectKey, String uploadId, List<PartEtag> partEtag)
    {
        this.uploadId = uploadId;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.partEtag = partEtag;
    }



    /**
     * Obtain the multipart upload ID.
     * 
     * @return Multipart upload ID
     */
    public String getUploadId()
    {
        return uploadId;
    }
    
    /**
     * Set the multipart upload ID.
     * 
     * @param uploadId Multipart upload ID
     */
    public void setUploadId(String uploadId)
    {
        this.uploadId = uploadId;
    }
    
    /**
     * Obtain the name of the bucket to which the multipart upload belongs.
     * 
     * @return Name of the bucket to which the multipart upload belongs
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    /**
     * Set the name for the bucket to which the multipart upload belongs.
     * 
     * @param bucketName Name of the bucket to which the multipart upload belongs
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * Obtain the name of the object involved in the multipart upload.
     * 
     * @return Name of the object involved in the multipart upload
     */
    public String getObjectKey()
    {
        return objectKey;
    }
    
    /**
     * Set the name for the object involved in the multipart upload.
     * 
     @param objectKey Name of the object involved in the multipart upload
     */
    public void setObjectKey(String objectKey)
    {
        this.objectKey = objectKey;
    }
    
    /**
     * Obtain the to-be-combined part list.
     * 
     * @return List of parts to be combined
     */
    public List<PartEtag> getPartEtag()
    {
        if(this.partEtag == null){
            this.partEtag = new ArrayList<PartEtag>();
        }
        return this.partEtag;
    }
    
    /**
     * Set the to-be-combined part list.
     * 
     * @param partEtags List of parts to be combined
     */
    public void setPartEtag(List<PartEtag> partEtags)
    {
        this.partEtag = partEtags;
    }

    @Override
    public String toString()
    {
        return "CompleteMultipartUploadRequest [uploadId=" + uploadId + ", bucketName=" + bucketName + ", objectKey=" + objectKey
            + ", partEtag=" + partEtag + "]";
    }
    
}



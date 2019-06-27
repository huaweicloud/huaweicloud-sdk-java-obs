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

/**
 * Parameters in a request for listing uploaded parts
 */
public class ListPartsRequest
{
    private String bucketName;
    
    private String key;
    
    private String uploadId;
    
    private Integer maxParts;
    
    private Integer partNumberMarker;
    
    public ListPartsRequest(){
        
    }
    
    /**
     * Constructor
     * @param bucketName Name of the bucket to which the multipart upload belongs
     * @param key Name of the object involved in the multipart upload
     */
    public ListPartsRequest(String bucketName, String key){
        this.bucketName = bucketName;
        this.key = key;
    }
    
    /**
     * Constructor
     * @param bucketName Name of the bucket to which the multipart upload belongs
     * @param key Name of the object involved in the multipart upload
     * @param uploadId Multipart upload ID
     */
    public ListPartsRequest(String bucketName, String key, String uploadId)
    {
        this.bucketName = bucketName;
        this.key = key;
        this.uploadId = uploadId;
    }

    /**
     * Constructor
     * @param bucketName Name of the bucket to which the multipart upload belongs
     * @param key Name of the object involved in the multipart upload
     * @param uploadId Multipart upload ID
     * @param maxParts Maximum number of uploaded parts that can be listed
     */
    public ListPartsRequest(String bucketName, String key, String uploadId, Integer maxParts)
    {
        this.bucketName = bucketName;
        this.key = key;
        this.uploadId = uploadId;
        this.maxParts = maxParts;
    }

    
    /**
     * Constructor
     * @param bucketName Name of the bucket to which the multipart upload belongs
     * @param key Name of the object involved in the multipart upload
     * @param uploadId Multipart upload ID
     * @param maxParts Maximum number of uploaded parts that can be listed
     * @param partNumberMarker Start position for listing parts
     */
    public ListPartsRequest(String bucketName, String key, String uploadId, Integer maxParts, Integer partNumberMarker)
    {
        this.bucketName = bucketName;
        this.key = key;
        this.uploadId = uploadId;
        this.maxParts = maxParts;
        this.partNumberMarker = partNumberMarker;
    }



    /**
     * Obtain the name of the bucket to which the multipart upload belongs.
     * @return Name of the bucket to which the multipart upload belongs
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    /**
     * Set the name for the bucket to which the multipart upload belongs.
     * @param bucketName Name of the bucket to which the multipart upload belongs
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * Obtain the name of the bucket to which the multipart upload belongs.
     * @return Name of the bucket to which the multipart upload belongs
     */
    public String getKey()
    {
        return key;
    }
    
    /**
     * Set the name for the bucket to which the multipart upload belongs.
     * @param key Name of the bucket to which the multipart upload belongs
     */
    public void setKey(String key)
    {
        this.key = key;
    }
    
    /**
     * Obtain the multipart upload ID.
     * @return Multipart upload ID
     */
    public String getUploadId()
    {
        return uploadId;
    }
    
    /**
     * Set the multipart upload ID.
     * @param uploadId Multipart upload ID
     */
    public void setUploadId(String uploadId)
    {
        this.uploadId = uploadId;
    }
    
    /**
     * Obtain the maximum number of uploaded parts that can be listed.
     * @return Maximum number of uploaded parts that can be listed
     */
    public Integer getMaxParts()
    {
        return maxParts;
    }
    
    /**
     * Set the maximum number of uploaded parts that can be listed.
     * @param maxParts Maximum number of uploaded parts that can be listed
     */
    public void setMaxParts(Integer maxParts)
    {
        this.maxParts = maxParts;
    }
    
    /**
     * Obtain the start position for listing parts.
     * @return Start position for listing parts
     */
    public Integer getPartNumberMarker()
    {
        return partNumberMarker;
    }
    
    /**
     * Set the start position for listing parts.
     * @param partNumberMarker Start position for listing parts
     */
    public void setPartNumberMarker(Integer partNumberMarker)
    {
        this.partNumberMarker = partNumberMarker;
    }
    
    @Override
    public String toString()
    {
        return "ListPartsRequest [bucketName=" + bucketName + ", key=" + key + ", uploadId=" + uploadId + ", maxParts=" + maxParts
            + ", partNumberMarker=" + partNumberMarker + "]";
    }
}



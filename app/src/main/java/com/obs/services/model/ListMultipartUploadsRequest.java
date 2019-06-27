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
 * Parameters in a request for listing multipart uploads
 */
public class ListMultipartUploadsRequest
{
    private String bucketName;
    
    private String prefix;
    
    private String delimiter;
    
    private Integer maxUploads;
    
    private String keyMarker;
    
    private String uploadIdMarker;
    
    public ListMultipartUploadsRequest(){
        
    }
    
    /**
     * Constructor
     * @param bucketName Bucket name
     */
    public ListMultipartUploadsRequest(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    
    /**
     * Constructor
     * @param bucketName Bucket name
     * @param maxUploads Maximum number of listed multipart uploads
     */
    public ListMultipartUploadsRequest(String bucketName, Integer maxUploads)
    {
        this.bucketName = bucketName;
        this.maxUploads = maxUploads;
    }

    
    /**
     * Constructor
     * @param bucketName Bucket name
     * @param prefix Prefix of names of the returned objects involved in the multipart uploads
     * @param delimiter Character used for sorting objects involved in the multipart uploads into different groups
     * @param maxUploads Maximum number of listed multipart uploads
     * @param keyMarker Start position for the query
     * @param uploadIdMarker Start position of the return result. This parameter is valid only when used together with "keyMarker". Only multipart uploads after "uploadIdMarker" of the specified "keyMarker" will be returned.
     */
    public ListMultipartUploadsRequest(String bucketName, String prefix, String delimiter, Integer maxUploads, String keyMarker,
        String uploadIdMarker)
    {
        this.bucketName = bucketName;
        this.prefix = prefix;
        this.delimiter = delimiter;
        this.maxUploads = maxUploads;
        this.keyMarker = keyMarker;
        this.uploadIdMarker = uploadIdMarker;
    }


    /**
     * Obtain the prefix of names of the returned objects involved in the multipart uploads.
     * 
     * @return Object name prefix
     */
    public String getPrefix()
    {
        return prefix;
    }
    
    /**
     * Set the prefix of names of the returned objects involved in the multipart uploads.
     * 
     * @param prefix Object name prefix
     */
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
    
    /**
     * Obtain the character used for sorting objects involved in the multipart uploads into different groups.
     * 
     * @return Character for grouping object names
     */
    public String getDelimiter()
    {
        return delimiter;
    }
    
    /**
     * Set the character used for sorting objects involved in the multipart uploads into different groups
     * 
     * @param delimiter Character for grouping object names
     */
    public void setDelimiter(String delimiter)
    {
        this.delimiter = delimiter;
    }
    
    /**
     * Obtain the start position for query (sorted by object name).
     * 
     * @return Start position for query
     */
    public String getKeyMarker()
    {
        return keyMarker;
    }
    
    /**
     * Set the start position for query (sorted by object name).
     * 
     * @param keyMarker Start position for query
     */
    public void setKeyMarker(String keyMarker)
    {
        this.keyMarker = keyMarker;
    }
    
    /**
     * Obtain the start position for query (sorted by multipart upload ID). This parameter is valid when used together with "keyMarker" and it specifies the start position of the returned result.
     * 
     * @return Start position for query
     */
    public String getUploadIdMarker()
    {
        return uploadIdMarker;
    }
    
    /**
     * Set the start position for query (sorted by multipart upload ID). This parameter is valid when used together with "keyMarker" and it specifies the start position of the returned result.
     * 
     * @param uploadIdMarker Start position for query
     */
    public void setUploadIdMarker(String uploadIdMarker)
    {
        this.uploadIdMarker = uploadIdMarker;
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
     * Obtain the maximum number of listed multipart uploads.
     * 
     * @return Maximum number of listed multipart uploads
     */
    public Integer getMaxUploads()
    {
        return maxUploads;
    }
    
    /**
     * Set the maximum number of listed multipart uploads.
     * 
     * @param maxUploads Maximum number of listed multipart uploads
     */
    public void setMaxUploads(Integer maxUploads)
    {
        this.maxUploads = maxUploads;
    }

    @Override
    public String toString()
    {
        return "ListMultipartUploadsRequest [bucketName=" + bucketName + ", prefix=" + prefix + ", delimiter=" + delimiter + ", maxUploads="
            + maxUploads + ", keyMarker=" + keyMarker + ", uploadIdMarker=" + uploadIdMarker + "]";
    }
    
}



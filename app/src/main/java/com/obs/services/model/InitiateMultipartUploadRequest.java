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
 * Parameters in a request for initializing a multipart upload
 */
public class InitiateMultipartUploadRequest extends PutObjectBasicRequest
{
    private ObjectMetadata metadata;
    
    private int expires;
    
    public InitiateMultipartUploadRequest(){
        
    }
    
    /**
     * Constructor
     * @param bucketName Name of the bucket to which the multipart upload belongs
     @param objectKey Name of the object involved in the multipart upload
     */
    public InitiateMultipartUploadRequest(String bucketName, String objectKey)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    /**
     * Obtain the expiration time of the object generated after the multipart upload is complete.
     * @return Expiration time of the object
     */
	public int getExpires() {
		return expires;
	}

	/**
	 * Set the expiration time of the object generated after the multipart upload is complete. The value must be an integer.
	 * @param expires Expiration time of the object
	 */
	public void setExpires(int expires) {
		this.expires = expires;
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
     * Set the redirection link which can redirect the request to another object in the bucket or to an external URL. 
     * 
     * @return Redirection link
     */
    @Deprecated
    public String getWebSiteRedirectLocation()
    {
        return this.metadata != null ? this.metadata.getWebSiteRedirectLocation() : null;
    }
    
    /**
     * Obtain the redirection link which can redirect the request to another object in the bucket or to an external URL. 
     * 
     * @param webSiteRedirectLocation Redirection link
     */
    @Deprecated
    public void setWebSiteRedirectLocation(String webSiteRedirectLocation)
    {
        if(this.metadata != null) {
        	this.metadata.setWebSiteRedirectLocation(webSiteRedirectLocation);
        }
    }
    
	/**
     * Set object properties, including customized metadata. "content-type" is supported.
     * @return Object properties
     */
    public ObjectMetadata getMetadata()
    {
        return metadata;
    }

    /**
     * Obtain object properties, including customized metadata. "content-type" is supported.
     * @param metadata Object properties
     */
    public void setMetadata(ObjectMetadata metadata)
    {
        this.metadata = metadata;
    }

	@Override
	public String toString() {
		return "InitiateMultipartUploadRequest [bucketName=" + bucketName + ", objectKey=" + objectKey + ", acl=" + acl
				+ ", sseKmsHeader=" + sseKmsHeader + ", sseCHeader=" + sseCHeader + ", metadata=" + metadata
				+ ", expires=" + expires + "]";
	}
    
}



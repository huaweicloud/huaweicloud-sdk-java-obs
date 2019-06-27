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

/**
 * Parameters in a request for renaming a file or folder
 *
 */
public class RenameRequest {
	
	private String bucketName;
	
	private String objectKey;
	
	private String newObjectKey;
	
	
	public RenameRequest() {
		
	}
	
	/**
	 * Constructor
	 * @param bucketName Bucket name
	 * @param objectKey File or folder name
	 * @param newObjectKey New file or folder name
	 */
	public RenameRequest(String bucketName, String objectKey, String newObjectKey) {
		super();
		this.bucketName = bucketName;
		this.objectKey = objectKey;
		this.newObjectKey = newObjectKey;
	}

    /**
     * Obtain the bucket name.
     * 
     * @return Bucket name
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    /**
     * Set the bucket name.
     * 
     * @param bucketName Bucket name
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * Obtain the file or folder name.
     * 
     * @return File or folder name
     */
    public String getObjectKey()
    {
        return objectKey;
    }
    
    /**
     * Set the file or folder name.
     * 
     * @param objectKey File or folder name
     *           
     */
    public void setObjectKey(String objectKey)
    {
        this.objectKey = objectKey;
    }

    /**
     * Obtain the new file or folder name.
     * @return New file or folder name
     */
	public String getNewObjectKey() {
		return newObjectKey;
	}

	 /**
     * Set the new file or folder name.
     * @param newObjectKey New file or folder name
     */
	public void setNewObjectKey(String newObjectKey) {
		this.newObjectKey = newObjectKey;
	}
	
	
}



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
 * Parameters in a request for truncating a file
 *
 */
public class TruncateFileRequest {
	
	private String bucketName;

	private String objectKey;
	
	private long newLength;
	
	public TruncateFileRequest() {
		
	}
	
	/**
	 * Constructor
	 * @param bucketName Bucket name
	 * @param objectKey File name
	 * @param newLength File size after the truncation
	 */
	public TruncateFileRequest(String bucketName, String objectKey, long newLength) {
		super();
		this.bucketName = bucketName;
		this.objectKey = objectKey;
		this.newLength = newLength;
	}



	/**
	 * Obtain the bucket name.
	 * 
	 * @return Bucket name
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Set the bucket name.
	 * 
	 * @param bucketName
	 *            Bucket name
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Obtain the file name.
	 * 
	 * @return File name
	 */
	public String getObjectKey() {
		return objectKey;
	}

	/**
	 * Set the file name.
	 * 
	 * @param objectKey
	 *            File name
	 * 
	 */
	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}
	
	/**
	 * Obtain the file size after the truncation.
	 * @return File size after the truncation
	 */
	public long getNewLength() {
		return newLength;
	}

	/**
	 * Set the post-truncation file size.
	 * @param newLength File size after the truncation
	 */
	public void setNewLength(long newLength) {
		this.newLength = newLength;
	}
}



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

import java.util.Date;

/**
 * Versioning object information
 */
public class VersionOrDeleteMarker
{
	private String bucketName;
	
    private String key;
    
    private String versionId;
    
    private boolean isLatest;
    
    private Date lastModified;
    
    private Owner owner;
    
    private String etag;
    
    private long size;
    
    private StorageClassEnum storageClass;
    
    private boolean isDeleteMarker;
    
    private boolean appendable;
    
    
    /**
     * 
     * Constructor
     * @param bucketName Bucket name
     * @param key Object name
     * @param versionId Version ID
     * @param isLatest Identifier indicating whether the object is of the current version
     * @param lastModified Last modification date
     * @param owner Owner
     * @param etag ETag of the object 
     * @param size Object size (in bytes)
     * @param storageClass Storage class of the object
     * @param isDeleteMarker Whether the versioning object has been deleted
     * @param appendable Whether object is appendable
     */
    public VersionOrDeleteMarker(String bucketName, String key, String versionId, boolean isLatest, Date lastModified, Owner owner, String etag, long size,
            StorageClassEnum storageClass, boolean isDeleteMarker, boolean appendable)
    {
		this.bucketName = bucketName;
        this.key = key;
        this.versionId = versionId;
        this.isLatest = isLatest;
        this.lastModified = lastModified;
        this.owner = owner;
        this.etag = etag;
        this.size = size;
        this.storageClass = storageClass;
        this.isDeleteMarker = isDeleteMarker;
        this.appendable = appendable;
    }

    /**
     * Obtain the object name.
     * @return Object name
     */
    public String getKey()
    {
        return key;
    }
    
    /**
     * Obtain the object name.
     * @return Object name
     */
    public String getObjectKey()
    {
        return key;
    }
    
    /**
     * Obtain the object version ID.
     * @return Version ID of the object
     */
    public String getVersionId()
    {
        return versionId;
    }
    
    /**
     * Check whether the object is of the current version.
     * @return Identifier indicating whether the object is of the current version
     */
    public boolean isLatest()
    {
        return isLatest;
    }
    
    
    /**
     * Obtain the last modification date of the object.
     * @return Last modification date of the object
     */
    public Date getLastModified()
    {
        return lastModified;
    }
    
    
    /**
     * Obtain the owner of the object. 
     * @return Owner of the object 
     */
    public Owner getOwner()
    {
        return owner;
    }
    
    /**
     * Obtain the storage class of the object.
     * @return Storage class of the object
     */
    @Deprecated
    public String getStorageClass()
    {
        return this.storageClass != null ? this.storageClass.getCode() : null;
    }
    

    /**
     * Obtain the storage class of the object.
     * @return Storage class of the object
     */
    public StorageClassEnum getObjectStorageClass()
    {
        return storageClass;
    }
    
    /**
     * Obtain ETag of the object.
     * 
     * @return Object ETag
     */
    public String getEtag()
    {
        return etag;
    }


    /**
     * Obtain the object size (in bytes). 
     * @return Object size
     */
    public long getSize()
    {
        return size;
    }

    
    /**
     * Check whether the versioning object has been deleted.
     * @return Identifier indicating whether the versioning object has been deleted
     */
    public boolean isDeleteMarker()
    {
        return isDeleteMarker;
    }
    

    /**
     * Identify whether an object is appendable.
     * @return Identifier specifying whether the object is an appendable object
     */
	public boolean isAppendable() {
		return appendable;
	}
    
    /**
     * Obtain the name of the bucket to which the versioning object belongs.
     * @return Name of the versioning object-residing bucket
     */
	public String getBucketName() {
		return bucketName;
	}

	@Override
	public String toString() {
		return "VersionOrDeleteMarker [bucketName=" + bucketName + ", key=" + key + ", versionId=" + versionId
				+ ", isLatest=" + isLatest + ", lastModified=" + lastModified + ", owner=" + owner + ", etag=" + etag
				+ ", size=" + size + ", storageClass=" + storageClass + ", isDeleteMarker=" + isDeleteMarker
				+ ", appendable=" + appendable + "]";
	}

}



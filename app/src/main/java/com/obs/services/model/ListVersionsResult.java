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
import java.util.Arrays;
import java.util.List;

/**
 * Response to a request for listing versioning objects in a bucket
 */
public class ListVersionsResult extends HeaderResponse
{
    private String bucketName;
    
    private String prefix;
    
    private String keyMarker;
    
    private String nextKeyMarker;
    
    private String versionIdMarker;
    
    private String nextVersionIdMarker;
    
    private String maxKeys;
    
    private boolean isTruncated;
    
    private VersionOrDeleteMarker[] versions;
    
    private List<String> commonPrefixes;
    
    private String location;
    
    private String delimiter;
    
    private boolean isAppendable;
    
    public ListVersionsResult(String bucketName, String prefix, String keyMarker, String nextKeyMarker,
			String versionIdMarker, String nextVersionIdMarker, String maxKeys, boolean isTruncated,
			VersionOrDeleteMarker[] versions, List<String> commonPrefixes, String location, String delimiter) {
		super();
		this.bucketName = bucketName;
		this.prefix = prefix;
		this.keyMarker = keyMarker;
		this.nextKeyMarker = nextKeyMarker;
		this.versionIdMarker = versionIdMarker;
		this.nextVersionIdMarker = nextVersionIdMarker;
		this.maxKeys = maxKeys;
		this.isTruncated = isTruncated;
		this.versions = versions;
		this.commonPrefixes = commonPrefixes;
		this.location = location;
		this.delimiter = delimiter;
	}


	/**
     * Obtain the bucket name.
     * @return Bucket name
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    
    /**
     * Obtain the object name prefix used for listing versioning objects.
     * @return Object name prefix used for listing versioning objects
     */
    public String getPrefix()
    {
        return prefix;
    }
    
    
    /**
     * Obtain the start position for listing versioning objects (sorted by object name).
     * @return Start position for listing objects
     */
    public String getKeyMarker()
    {
        return keyMarker;
    }
    
    
    /**
     * Obtain the start position for listing versioning objects (sorted by version ID).
     * @return Start position for listing objects
     */
    public String getVersionIdMarker()
    {
        return versionIdMarker;
    }
    
    /**
     * Obtain the maximum number of versioning objects to be listed. 
     * @return Maximum number of versioning objects to be listed
     */
    public String getMaxKeys()
    {
        return maxKeys;
    }
    
    
    /**
     * Check whether the query result list is truncated. Value "true" indicates that the results are incomplete while value "false" indicates that the results are complete.
     * @return Truncation identifier
     */
    public boolean isTruncated()
    {
        return isTruncated;
    }
    
    
    /**
     * Obtain the versioning object array in the bucket.
     * @return Versioning object array in the bucket. For details, see {@link VersionOrDeleteMarker}.
     */
    public VersionOrDeleteMarker[] getVersions()
    {
        return versions;
    }
    
    
    /**
     * Start position for next listing (sorted by object name)
     * @return Start position for next listing
     */
    public String getNextKeyMarker()
    {
        return nextKeyMarker;
    }


    /**
     * Obtain the start position for next listing (sorted by version ID).
     * @return Start position for next listing
     */
    public String getNextVersionIdMarker()
    {
        return nextVersionIdMarker;
    }

    
    /**
     * Obtain the list of prefixes to the names of grouped objects.
     * 
     * @return List of prefixes to the names of grouped objects
     */
    public List<String> getCommonPrefixes()
    {
    	if(commonPrefixes == null) {
    		commonPrefixes = new ArrayList<String>();
    	}
        return commonPrefixes;
    }
    
    /**
     * Obtain the bucket location.
     * @return Bucket location
     */
    public String getLocation()
    {
        return location;
    }



    /**
     * Obtain the character for grouping object names.
     * 
     * @return Character for grouping object names
     */
    public String getDelimiter()
    {
        return delimiter;
    }
    
	public boolean isAppendable() {
		return isAppendable;
	}

    

	@Override
	public String toString() {
		return "ListVersionsResult [bucketName=" + bucketName + ", prefix=" + prefix + ", keyMarker=" + keyMarker
				+ ", nextKeyMarker=" + nextKeyMarker + ", versionIdMarker=" + versionIdMarker + ", nextVersionIdMarker="
				+ nextVersionIdMarker + ", maxKeys=" + maxKeys + ", isTruncated=" + isTruncated + ", versions="
				+ Arrays.toString(versions) + ", commonPrefixes=" + commonPrefixes + ", location=" + location
				+ ", delimiter=" + delimiter + "]";
	}


	
    
}



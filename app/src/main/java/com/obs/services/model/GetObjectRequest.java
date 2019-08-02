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
import java.util.Map;

import com.obs.services.internal.ObsConstraint;

/**
 * Parameters in an object download request
 */
public class GetObjectRequest
{
    private String bucketName;
    
    private String objectKey;
    
    private Long rangeStart;
    
    private Long rangeEnd;
    
    private String versionId;
    
    private ObjectRepleaceMetadata replaceMetadata;
    
    private SseCHeader sseCHeader;
    
    private Date ifModifiedSince;
    
    private Date ifUnmodifiedSince;
    
    private String ifMatchTag;
    
    private String ifNoneMatchTag;
    
    private String imageProcess;
    
    private ProgressListener progressListener;
    
    private long progressInterval = ObsConstraint.DEFAULT_PROGRESS_INTERVAL;
    
    private CacheOptionEnum cacheOption;
    
    private long ttl;
    
    private Map<String, String> requestParameters;
    
    public GetObjectRequest(){
        
    }
    
    /**
     * Constructor
     * @param bucketName Bucket name
     * @param objectKey Object name
     */
    public GetObjectRequest(String bucketName, String objectKey)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }
    
    /**
     * Constructor
     * @param bucketName Bucket name
     * @param objectKey Object name
     * @param versionId Version ID of the object
     */
    public GetObjectRequest(String bucketName, String objectKey, String versionId)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.versionId = versionId;
    }

    /**
     * Obtain the request headers that need to be rewritten during object download.
     * @return Rewritten response headers
     */
    public ObjectRepleaceMetadata getReplaceMetadata()
    {
        return replaceMetadata;
    }
    
    /**
     * Set the request headers that need to be rewritten during object download.
     * @param replaceMetadata Rewritten response headers
     */
    public void setReplaceMetadata(ObjectRepleaceMetadata replaceMetadata)
    {
        this.replaceMetadata = replaceMetadata;
    }
    
    /**
     * Obtain SSE-C decryption headers. 
     * 
     * @return SSE-C decryption headers
     */
    public SseCHeader getSseCHeader()
    {
        return sseCHeader;
    }
    
    /**
     * Set SSE-C decryption headers. 
     * 
     * @param sseCHeader SSE-C decryption headers
     */
    public void setSseCHeader(SseCHeader sseCHeader)
    {
        this.sseCHeader = sseCHeader;
    }
    
    
    /**
     * Obtain the start position for object download.
     * 
     * @return Start position for object download
     */
    public Long getRangeStart()
    {
        return rangeStart;
    }
    
    /**
     * Set the start position for object download.
     * 
     * @param rangeStart Start position for object download
     */
    public void setRangeStart(Long rangeStart)
    {
        this.rangeStart = rangeStart;
    }
    
    /**
     * Obtain the end position for object download.
     * 
     * @return End position for object download
     */
    public Long getRangeEnd()
    {
        return rangeEnd;
    }
    
    /**
     * Set the end position for object download.
     * 
     * @param rangeEnd End position for object download
     * 
     */
    public void setRangeEnd(Long rangeEnd)
    {
        this.rangeEnd = rangeEnd;
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
     * 
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * Obtain the object name.
     * 
     * @return Object name
     */
    public String getObjectKey()
    {
        return objectKey;
    }
    
    /**
     * Set the object name.
     * 
     * @param objectKey Object name
     * 
     */
    public void setObjectKey(String objectKey)
    {
        this.objectKey = objectKey;
    }
    
    /**
     * Obtain the object version ID.
     * 
     * @return Version ID of the object
     */
    public String getVersionId()
    {
        return versionId;
    }
    
    /**
     * Set the version ID of the object. 
     * 
     * @param versionId Version ID of the object
     * 
     */
    public void setVersionId(String versionId)
    {
        this.versionId = versionId;
    }

    /**
     * Obtain the time condition set for downloading the object. Only when the object is modified after the point in time specified by this parameter, it will be downloaded. Otherwise, "304 Not Modified" will be returned.
     * 
     * @return Time condition set for downloading the object
     */
    public Date getIfModifiedSince()
    {
        return ifModifiedSince;
    }
    
    /**
     * Set the time condition set for downloading the object. Only when the object is modified after the point in time specified by this parameter, it will be downloaded. Otherwise, "304 Not Modified" will be returned.
     * 
     * @param ifModifiedSince Time condition set for downloading the object
     */
    public void setIfModifiedSince(Date ifModifiedSince)
    {
        this.ifModifiedSince = ifModifiedSince;
    }
    
    /**
     * Obtain the time condition for downloading the object. Only when the object remains unchanged after the point in time specified by this parameter, it will be downloaded; otherwise, "412 Precondition Failed" will be returned.
     * 
     * @return Time condition set for downloading the object
     */
    public Date getIfUnmodifiedSince()
    {
        return ifUnmodifiedSince;
    }
    
    /**
     * Set the time condition for downloading the object. Only when the object remains unchanged after the point in time specified by this parameter, it will be downloaded; otherwise, "412 Precondition Failed" will be returned.
     * 
     * @param ifUnmodifiedSince Time condition set for downloading the object
     */
    public void setIfUnmodifiedSince(Date ifUnmodifiedSince)
    {
        this.ifUnmodifiedSince = ifUnmodifiedSince;
    }
    
    /**
     * Obtain the ETag verification condition for downloading the object. Only when the ETag of the object is the same as that specified by this parameter, the object will be downloaded. Otherwise, "412 Precondition Failed" will be returned.
     * 
     * @return ETag verification condition set for downloading the object
     */
    public String getIfMatchTag()
    {
        return ifMatchTag;
    }
    
    /**
     * Set the ETag verification condition for downloading the object. Only when the ETag of the object is the same as that specified by this parameter, the object will be downloaded. Otherwise, "412 Precondition Failed" will be returned.
     * 
     * @param ifMatchTag ETag verification condition set for downloading the object
     */
    public void setIfMatchTag(String ifMatchTag)
    {
        this.ifMatchTag = ifMatchTag;
    }
    
    /**
     * Obtain the ETag verification condition for downloading the object. Only when the ETag of the object is different from that specified by this parameter, the object will be downloaded. Otherwise, "304 Not Modified" will be returned.
     * 
     * @return ETag verification condition set for downloading the object
     */
    public String getIfNoneMatchTag()
    {
        return ifNoneMatchTag;
    }
    
    /**
     * Set the ETag verification condition for downloading the object. Only when the ETag of the object is different from that specified by this parameter, the object will be downloaded. Otherwise, "304 Not Modified" will be returned.
     * 
     * @param ifNoneMatchTag ETag verification condition set for downloading the object
     * 
     */
    public void setIfNoneMatchTag(String ifNoneMatchTag)
    {
        this.ifNoneMatchTag = ifNoneMatchTag;
    }

    /**
     * Obtain image processing parameters.
     * @return Image processing parameters
     */
    public String getImageProcess()
    {
        return imageProcess;
    }

    /**
     * Set image processing parameters.
     * @param imageProcess Image processing parameters
     */
    public void setImageProcess(String imageProcess)
    {
        this.imageProcess = imageProcess;
    }
    
    /**
	 * Obtain the data transfer listener.
	 * @return Data transfer listener
	 */
	public ProgressListener getProgressListener() {
		return progressListener;
	}

	/**
	 * Set the data transfer listener.
	 * @param progressListener Data transfer listener
	 */
	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}
	
	/**
	 * Obtain the callback threshold of the data transfer listener. The default value is 100 KB.
	 * @return Callback threshold of the data transfer listener
	 */
	public long getProgressInterval() {
		return progressInterval;
	}
	
	/**
	 * Set the callback threshold of the data transfer listener. The default value is 100 KB.
	 * @param progressInterval Callback threshold of the data transfer listener
	 */
	public void setProgressInterval(long progressInterval) {
		this.progressInterval = progressInterval;
	}
	
	public Map<String, String> getRequestParameters() {
	    return this.requestParameters;
	}
	
	/**
	 * Obtain the control option of the read-ahead cache.
	 * @return Control option of the read-ahead cache
	 */
	public CacheOptionEnum getCacheOption() {
		return cacheOption;
	}

	/**
	 * Set the control option of the read-ahead cache.
	 * @param cacheOption Control option of the read-ahead cache
	 */
	public void setCacheOption(CacheOptionEnum cacheOption) {
		this.cacheOption = cacheOption;
	}

	/**
	 * Obtain the cache data expiration time.
	 * @return Cache data expiration time
	 */
	public long getTtl() {
		return ttl;
	}

	/**
	 * Set the cache data expiration time.
	 * @param ttl Cache data expiration time
	 */
	public void setTtl(long ttl) {
		if(ttl < 0 || ttl > 259200) {
			ttl = 60 * 60 * 24L;
		}
		this.ttl = ttl;
	}

	public void setRequestParameters(Map<String, String> requestParameters) {
	    this.requestParameters = requestParameters;
	}

    @Override
    public String toString()
    {
        return "GetObjectRequest [bucketName=" + bucketName + ", objectKey=" + objectKey + ", rangeStart=" + rangeStart + ", rangeEnd="
            + rangeEnd + ", versionId=" + versionId + ", replaceMetadata=" + replaceMetadata + ", sseCHeader=" + sseCHeader
            + ", ifModifiedSince=" + ifModifiedSince + ", ifUnmodifiedSince=" + ifUnmodifiedSince + ", ifMatchTag=" + ifMatchTag
            + ", ifNoneMatchTag=" + ifNoneMatchTag + ", imageProcess=" + imageProcess + "]";
    }
    
}

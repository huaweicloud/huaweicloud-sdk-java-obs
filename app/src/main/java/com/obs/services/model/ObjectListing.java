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

import java.util.ArrayList;
import java.util.List;

/**
 * 列举桶内对象的响应结果
 */
public class ObjectListing extends HeaderResponse
{
    private List<ObsObject> objectSummaries;
    
    private List<String> commonPrefixes;
    
    private String bucketName;
    
    private boolean truncated;
    
    private String prefix;
    
    private String marker;
    
    private int maxKeys;
    
    private String delimiter;
    
    private String nextMarker;
    
    private String location;
    
    
    public ObjectListing(List<ObsObject> objectSummaries, List<String> commonPrefixes, String bucketName,
			boolean truncated, String prefix, String marker, int maxKeys, String delimiter, String nextMarker,
			String location) {
		super();
		this.objectSummaries = objectSummaries;
		this.commonPrefixes = commonPrefixes;
		this.bucketName = bucketName;
		this.truncated = truncated;
		this.prefix = prefix;
		this.marker = marker;
		this.maxKeys = maxKeys;
		this.delimiter = delimiter;
		this.nextMarker = nextMarker;
		this.location = location;
	}

	/**
     * 获取下次请求的起始位置
     * @return 下次请求的起始位置标识
     */
    public String getNextMarker()
    {
        return nextMarker;
    }
    

    /**
     * 获取桶内对象列表
     * @return 桶内对象列表
     */
	public List<ObsObject> getObjects()
    {
		if(this.objectSummaries == null) {
			this.objectSummaries = new ArrayList<ObsObject>();
		}
        return objectSummaries;
    }
	
    @Deprecated
    public List<S3Object> getObjectSummaries()
    {
    	List<S3Object> objects = new ArrayList<S3Object>(this.objectSummaries.size());
    	objects.addAll(this.objectSummaries);
        return objects;
    }
    
    /**
     * 获取分组后的对象名前缀列表
     * 
     * @return 分组后的对象名前缀列表
     */
    public List<String> getCommonPrefixes()
    {
    	if(this.commonPrefixes == null) {
    		this.commonPrefixes = new ArrayList<String>();
    	}
        return commonPrefixes;
    }
    
    
    /**
     * 获取桶名
     * 
     * @return 桶名
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    
    /**
     * 判断查询结果列表是否被截断。true表示截断，本次没有返回全部结果；false表示未截断，本次已经返回了全部结果。
     * @return 截断标识
     */
    public boolean isTruncated()
    {
        return truncated;
    }
    
    
    /**
     * 获取列举对象请求中的对象名前缀
     * @return 请求中的对象名前缀
     */
    public String getPrefix()
    {
        return prefix;
    }
    
    
    /**
     * 获取列举对象请求中的起始位置
     * @return 请求中的起始位置标识
     */
    public String getMarker()
    {
        return marker;
    }
    
    
    /**
     * 获取列举对象的最大条目数
     * @return 列举对象的最大条目数
     */
    public int getMaxKeys()
    {
        return maxKeys;
    }
    
    
    /**
     * 获取列举对象时请求中的分组字符
     * 
     * @return 分组字符
     */
    public String getDelimiter()
    {
        return delimiter;
    }
    
    
    /**
     * 获取桶的区域位置
     * @return 桶的区域位置
     */
    public String getLocation()
    {
        return location;
    }


    @Override
    public String toString()
    {
        return "ObjectListing [objectSummaries=" + objectSummaries + ", commonPrefixes=" + commonPrefixes + ", bucketName=" + bucketName
            + ", truncated=" + truncated + ", prefix=" + prefix + ", marker=" + marker + ", maxKeys=" + maxKeys + ", delimiter=" + delimiter
            + ", nextMarker=" + nextMarker + ", location=" + location + "]";
    }
    
    
}

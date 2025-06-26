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
 * Response to a request for listing objects in a bucket
 */
public class ObjectListing extends HeaderResponse {
    private List<ObsObject> objectSummaries;

    private List<String> commonPrefixes;

    private List<ObsObject> extenedCommonPrefixes;

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

    public ObjectListing(List<ObsObject> objectSummaries, List<String> commonPrefixes, String bucketName,
            boolean truncated, String prefix, String marker, int maxKeys, String delimiter, String nextMarker,
            String location, List<ObsObject> extenedCommonPrefixes) {
        this(objectSummaries, commonPrefixes, bucketName, truncated, prefix, marker, maxKeys, delimiter, nextMarker,
                location);
        this.extenedCommonPrefixes = extenedCommonPrefixes;
    }

    /**
     * Obtain the start position for next listing.
     * 
     * @return Start position for next listing
     */
    public String getNextMarker() {
        return nextMarker;
    }

    /**
     * Obtain the list of objects in the bucket.
     * 
     * @return List of objects in the bucket
     */
    public List<ObsObject> getObjects() {
        if (this.objectSummaries == null) {
            this.objectSummaries = new ArrayList<ObsObject>();
        }
        return objectSummaries;
    }

    @Deprecated
    public List<S3Object> getObjectSummaries() {
        List<S3Object> objects = new ArrayList<S3Object>(this.objectSummaries.size());
        objects.addAll(this.objectSummaries);
        return objects;
    }

    /**
     * Obtain the list of prefixes to the names of grouped objects.
     * 
     * @return List of prefixes to the names of grouped objects
     */
    public List<String> getCommonPrefixes() {
        if (this.commonPrefixes == null) {
            this.commonPrefixes = new ArrayList<String>();
        }
        return commonPrefixes;
    }

    /**
     * Obtain the list of prefixes to the names of grouped objects.
     *
     * @return List of prefixes to the names of grouped objects
     */
    public List<ObsObject> getExtenedCommonPrefixes() {
        if (this.extenedCommonPrefixes == null) {
            this.extenedCommonPrefixes = new ArrayList<ObsObject>();
        }
        return extenedCommonPrefixes;
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
     * Check whether the query result list is truncated. Value "true" indicates
     * that the results are incomplete while value "false" indicates that the
     * results are complete.
     * 
     * @return Truncation identifier
     */
    public boolean isTruncated() {
        return truncated;
    }

    /**
     * Obtain the object name prefix used for filtering objects to be listed.
     * 
     * @return Object name prefix used for listing versioning objects
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Obtain the start position for listing objects.
     * 
     * @return Start position for listing objects
     */
    public String getMarker() {
        return marker;
    }

    /**
     * Obtain the maximum number of objects to be listed.
     * 
     * @return Maximum number of objects to be listed
     */
    public int getMaxKeys() {
        return maxKeys;
    }

    /**
     * Obtain the character for grouping object names.
     * 
     * @return Character for grouping object names
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Obtain the bucket location.
     * 
     * @return Bucket location
     */
    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "ObjectListing [objectSummaries=" + objectSummaries + ", commonPrefixes=" + commonPrefixes
                + ", bucketName=" + bucketName + ", truncated=" + truncated + ", prefix=" + prefix + ", marker="
                + marker + ", maxKeys=" + maxKeys + ", delimiter=" + delimiter + ", nextMarker=" + nextMarker
                + ", location=" + location + "]";
    }

}

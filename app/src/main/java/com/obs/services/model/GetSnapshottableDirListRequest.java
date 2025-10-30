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

import static com.obs.services.internal.ObsConstraint.SNAPSHOT_MAX_KEYS;

/**
 * Request parameters for getting a list of directories under a bucket that have been set to allow snapshots.
 *
 * @since 3.20.3
 */
public class GetSnapshottableDirListRequest extends GenericRequest {
    {
        httpMethod = HttpMethodEnum.GET;
    }

    private String marker;

    private int maxKeys;

    /**
     * Constructor
     *
     * @param bucketName
     *            Bucket name
     */
    public GetSnapshottableDirListRequest(String bucketName) {
        this(bucketName, null, SNAPSHOT_MAX_KEYS);
    }

    /**
     * Constructor
     *
     * @param bucketName
     *            Bucket name
     * @param marker
     *            Marker
     */
    public GetSnapshottableDirListRequest(String bucketName, String marker) {
        this(bucketName, marker, SNAPSHOT_MAX_KEYS);
    }

    /**
     * Constructor
     *
     * @param bucketName
     *            Bucket name
     * @param maxKeys
     *            maxKeys
     */
    public GetSnapshottableDirListRequest(String bucketName, int maxKeys) {
        this(bucketName, null, maxKeys);
    }

    /**
     * Constructor
     *
     * @param bucketName
     *            Bucket name
     * @param marker
     *            Marker
     * @param maxKeys
     *            Max keys
     */
    public GetSnapshottableDirListRequest(String bucketName, String marker, int maxKeys) {
        this.bucketName = bucketName;
        this.marker = marker;
        this.maxKeys = maxKeys;
    }

    /**
     * Obtain the marker that will be used as an identifier as the starting position for listing.
     *
     * @return Marker
     */
    public String getMarker() {
        return marker;
    }

    /**
     * Obtain the maximum number of snapshot directories to list.
     *
     * @return Max Keys
     */
    public int getMaxKeys() {
        return maxKeys;
    }

    @Override
    public String toString() {
        return "GetSnapshottableDirListRequest [bucketName=" + bucketName
                + ", marker=" + marker + ", maxKeys=" + maxKeys + "]";
    }
}
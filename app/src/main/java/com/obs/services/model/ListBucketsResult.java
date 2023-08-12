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
 * Response to a request for listing buckets
 *
 */
public class ListBucketsResult extends HeaderResponse {
    private List<ObsBucket> buckets;

    private Owner owner;

    private boolean truncated;

    private String marker;

    private int maxKeys;

    private String nextMarker;

    public ListBucketsResult(List<ObsBucket> buckets, Owner owner, boolean truncated, String marker, int maxKeys,
                             String nextMarker) {
        this.buckets = buckets;
        this.owner = owner;
        this.truncated = truncated;
        this.marker = marker;
        this.maxKeys = maxKeys;
        this.nextMarker = nextMarker;
    }

    public List<ObsBucket> getBuckets() {
        if (buckets == null) {
            buckets = new ArrayList<>();
        }
        return buckets;
    }

    public Owner getOwner() {
        return owner;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public String getMarker() {
        return marker;
    }

    public int getMaxKeys() {
        return maxKeys;
    }

    public String getNextMarker() {
        return nextMarker;
    }

    @Override
    public String toString() {
        return "ListBucketsResult [buckets=" + buckets + ", owner=" + owner + "]";
    }

}

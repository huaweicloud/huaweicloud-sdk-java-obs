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

import com.obs.services.model.BucketTagInfo.TagSet;
/**
 * Object tagging configuration
 *
 */
public class ObjectTagResult extends HeaderResponse {
    private TagSet tagSet;

    public ObjectTagResult() {

    }

    /**
     * Constructor
     * 
     * @param tagSet
     *            Object tag set
     */
    public ObjectTagResult(TagSet tagSet) {
        this.tagSet = tagSet;
    }

    /**
     * Obtain the tag set of an Object.
     * 
     * @return Tag set
     */
    public TagSet getTagSet() {
        if (tagSet == null) {
            tagSet = new TagSet();
        }
        return tagSet;
    }

    /**
     * Configure the tag set for an Object.
     * 
     * @param tagSet
     *            Tag set
     */
    public void setTagSet(TagSet tagSet) {
        this.tagSet = tagSet;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        if (tagSet != null) {
            int i = 0;
            for (TagSet.Tag t : tagSet.getTags()) {
                s.append("[").append("key=").append(t.getKey()).append(",").append("value=").append(t.getValue())
                        .append("]");
                if (i++ != tagSet.getTags().size() - 1) {
                    s.append(",");
                }
            }
        }
        s.append("]");
        return "ObjectTagResult [tagSet=[tags=" + s + "]";
    }
}

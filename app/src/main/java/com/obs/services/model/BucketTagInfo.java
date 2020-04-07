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
 * 桶的标签配置
 *
 */
public class BucketTagInfo extends HeaderResponse {
    private TagSet tagSet;

    public BucketTagInfo() {

    }

    /**
     * 构造函数
     * 
     * @param tagSet
     *            桶标签集合
     */
    public BucketTagInfo(TagSet tagSet) {
        this.tagSet = tagSet;
    }

    /**
     * 桶标签集合
     *
     */
    public static class TagSet {
        private List<Tag> tags;

        /**
         * 桶标签
         *
         */
        public static class Tag {
            private String key;

            private String value;

            public Tag() {

            }

            /**
             * 构造函数
             * 
             * @param key
             *            标签键
             * @param value
             *            标签值
             */
            public Tag(String key, String value) {
                this.key = key;
                this.value = value;
            }

            /**
             * 获取标签键
             * 
             * @return 标签键
             */
            public String getKey() {
                return key;
            }

            /**
             * 设置标签键
             * 
             * @param key
             *            标签键
             */
            public void setKey(String key) {
                this.key = key;
            }

            /**
             * 获取标签值
             * 
             * @return 获取标签值
             */
            public String getValue() {
                return value;
            }

            /**
             * 设置标签值
             * 
             * @param value
             *            标签值
             */
            public void setValue(String value) {
                this.value = value;
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((key == null) ? 0 : key.hashCode());
                result = prime * result + ((value == null) ? 0 : value.hashCode());
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                Tag other = (Tag) obj;
                if (key == null) {
                    if (other.key != null)
                        return false;
                } else if (!key.equals(other.key))
                    return false;
                if (value == null) {
                    if (other.value != null)
                        return false;
                } else if (!value.equals(other.value))
                    return false;
                return true;
            }

        }

        /**
         * 获取标签列表
         * 
         * @return 标签列表
         */
        public List<Tag> getTags() {
            if (tags == null) {
                tags = new ArrayList<Tag>();
            }
            return tags;
        }

        /**
         * 新增标签
         * 
         * @param key
         *            标签键
         * @param value
         *            标签值
         * @return 新增的标签
         */
        public Tag addTag(String key, String value) {
            Tag t = new Tag(key, value);
            this.getTags().add(t);
            return t;
        }

        /**
         * 删除标签
         * 
         * @param key
         *            标签键
         * @param value
         *            标签值
         * @return 删除的标签
         */
        public Tag removeTag(String key, String value) {
            Tag t = new Tag(key, value);
            this.getTags().remove(t);
            return t;
        }

        /**
         * 删除标签
         * 
         * @param key
         *            标签键
         * @return 删除的标签
         */
        public Tag removeTagByKey(String key) {
            for (Tag t : this.tags) {
                if (t.getKey().equals(key)) {
                    this.removeTag(t.getKey(), t.getValue());
                    return t;
                }
            }
            return null;
        }
    }

    /**
     * 获取桶标签集合
     * 
     * @return 标签集合
     */
    public TagSet getTagSet() {
        if (tagSet == null) {
            tagSet = new TagSet();
        }
        return tagSet;
    }

    /**
     * 设置桶标签集合
     * 
     * @param tagSet
     *            标签集合
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
        return "BucketTagInfo [tagSet=[tags=" + s.toString() + "]";
    }
}

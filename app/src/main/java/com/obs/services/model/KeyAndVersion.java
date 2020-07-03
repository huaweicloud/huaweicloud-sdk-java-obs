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

/**
 * 待删除的多版本对象信息
 */
public class KeyAndVersion {
    private String key;
    private String version;

    /**
     * 构造方法
     * 
     * @param key
     *            对象名
     * @param version
     *            对象版本号
     */
    public KeyAndVersion(String key, String version) {
        this.key = key;
        this.version = version;
    }

    /**
     * 构造方法
     * 
     * @param key
     *            对象名
     */
    public KeyAndVersion(String key) {
        this(key, null);
    }

    /**
     * 获取对象名
     * 
     * @return 对象名
     */
    public String getKey() {
        return key;
    }

    /**
     * 获取对象版本号
     * 
     * @return 对象版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置对象名
     * 
     * @param key
     *            对象名
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 设置对象版本号
     * 
     * @param version
     *            对象版本号
     */
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "KeyAndVersion [key=" + key + ", version=" + version + "]";
    }

}

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
 * 桶的存量信息
 */
public class BucketStorageInfo extends HeaderResponse {
    private long size;

    private long objectNum;

    /**
     * 获取桶的空间大小，单位：字节
     * 
     * @return 桶的空间大小
     */
    public long getSize() {
        return size;
    }

    /**
     * 设置桶的空间大小 ，单位：字节
     * 
     * @param storageSize
     *            桶的空间大小
     */
    public void setSize(long storageSize) {
        this.size = storageSize;
    }

    /**
     * 获取桶的对象个数
     * 
     * @return 桶的对象个数
     */
    public long getObjectNumber() {
        return objectNum;
    }

    /**
     * 设置桶的对象个数
     * 
     * @param objectNumber
     *            桶的对象个数
     */
    public void setObjectNumber(long objectNumber) {
        this.objectNum = objectNumber;
    }

    @Override
    public String toString() {
        return "BucketStorageInfo [size=" + size + ", objectNum=" + objectNum + "]";
    }

}

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

/**
 * Bucket storage information
 */
public class BucketStorageInfo extends HeaderResponse {
    private long size;

    private long objectNum;

    private long standardSize;

    private long standardObjectNumber;

    private long warmSize;

    private long warmObjectNumber;

    private long coldSize;

    private long coldObjectNumber;

    private long deepArchiveSize;

    private long deepArchiveObjectNumber;

    private long highPerformanceSize;

    private long highPerformanceObjectNumber;

    private long standard_IASize;

    private long standard_IAObjectNumber;

    private long glacierSize;

    private long glacierObjectNumber;

    /**
     * Obtain the bucket quota (in bytes).
     * 
     * @return Bucket size
     */
    public long getSize() {
        return size;
    }

    /**
     * Set the bucket size (in bytes).
     * 
     * @param storageSize
     *            Bucket size
     */
    public void setSize(long storageSize) {
        this.size = storageSize;
    }

    /**
     * Obtain the number of objects in the bucket.
     * 
     * @return Number of objects in the bucket
     */
    public long getObjectNumber() {
        return objectNum;
    }

    /**
     * Set the number of objects in the bucket.
     * 
     * @param objectNumber
     *            Number of objects in the bucket
     */
    public void setObjectNumber(long objectNumber) {
        this.objectNum = objectNumber;
    }

    public long getStandardSize() {
        return standardSize;
    }

    public void setStandardSize(long standardSize) {
        this.standardSize = standardSize;
    }

    public long getStandardObjectNumber() {
        return standardObjectNumber;
    }

    public void setStandardObjectNumber(long standardObjectNumber) {
        this.standardObjectNumber = standardObjectNumber;
    }

    public long getWarmSize() {
        return warmSize;
    }

    public void setWarmSize(long warmSize) {
        this.warmSize = warmSize;
    }

    public long getWarmObjectNumber() {
        return warmObjectNumber;
    }

    public void setWarmObjectNumber(long warmObjectNumber) {
        this.warmObjectNumber = warmObjectNumber;
    }

    public long getColdSize() {
        return coldSize;
    }

    public void setColdSize(long coldSize) {
        this.coldSize = coldSize;
    }

    public long getColdObjectNumber() {
        return coldObjectNumber;
    }

    public void setColdObjectNumber(long coldObjectNumber) {
        this.coldObjectNumber = coldObjectNumber;
    }

    public long getDeepArchiveSize() {
        return deepArchiveSize;
    }

    public void setDeepArchiveSize(long deepArchiveSize) {
        this.deepArchiveSize = deepArchiveSize;
    }

    public long getDeepArchiveObjectNumber() {
        return deepArchiveObjectNumber;
    }

    public void setDeepArchiveObjectNumber(long deepArchiveObjectNumber) {
        this.deepArchiveObjectNumber = deepArchiveObjectNumber;
    }

    public long getHighPerformanceSize() {
        return highPerformanceSize;
    }

    public void setHighPerformanceSize(long highPerformanceSize) {
        this.highPerformanceSize = highPerformanceSize;
    }

    public long getHighPerformanceObjectNumber() {
        return highPerformanceObjectNumber;
    }

    public void setHighPerformanceObjectNumber(long highPerformanceObjectNumber) {
        this.highPerformanceObjectNumber = highPerformanceObjectNumber;
    }

    public long getStandard_IASize() {
        return standard_IASize;
    }

    public void setStandard_IASize(long standard_IASize) {
        this.standard_IASize = standard_IASize;
    }

    public long getStandard_IAObjectNumber() {
        return standard_IAObjectNumber;
    }

    public void setStandard_IAObjectNumber(long standard_IAObjectNumber) {
        this.standard_IAObjectNumber = standard_IAObjectNumber;
    }

    public long getGlacierSize() {
        return glacierSize;
    }

    public void setGlacierSize(long glacierSize) {
        this.glacierSize = glacierSize;
    }

    public long getGlacierObjectNumber() {
        return glacierObjectNumber;
    }

    public void setGlacierObjectNumber(long glacierObjectNumber) {
        this.glacierObjectNumber = glacierObjectNumber;
    }

    @Override
    public String toString() {
        return "BucketStorageInfo{" +
                "size=" + size +
                ", objectNum=" + objectNum +
                ", standardSize=" + standardSize +
                ", standardObjectNumber=" + standardObjectNumber +
                ", warmSize=" + warmSize +
                ", warmObjectNumber=" + warmObjectNumber +
                ", coldSize=" + coldSize +
                ", coldObjectNumber=" + coldObjectNumber +
                ", deepArchiveSize=" + deepArchiveSize +
                ", deepArchiveObjectNumber=" + deepArchiveObjectNumber +
                ", highPerformanceSize=" + highPerformanceSize +
                ", highPerformanceObjectNumber=" + highPerformanceObjectNumber +
                ", standard_IASize=" + standard_IASize +
                ", standard_IAObjectNumber=" + standard_IAObjectNumber +
                ", glacierSize=" + glacierSize +
                ", glacierObjectNumber=" + glacierObjectNumber +
                '}';
    }
}

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
 * 上传段的响应结果
 */
public class UploadPartResult extends HeaderResponse {
    private int partNumber;

    private String etag;

    /**
     * 获取分段号
     * 
     * @return 分段号
     */
    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    /**
     * 获取段的etag校验值
     * 
     * @return 段的etag校验值
     */
    public String getEtag() {
        return etag;
    }

    public void setEtag(String objEtag) {
        this.etag = objEtag;
    }

    @Override
    public String toString() {
        return "UploadPartResult [partNumber=" + partNumber + ", etag=" + etag + "]";
    }
}

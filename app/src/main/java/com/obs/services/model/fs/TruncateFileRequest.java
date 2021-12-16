/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.obs.services.model.fs;

import com.obs.services.model.BaseObjectRequest;
import com.obs.services.model.HttpMethodEnum;

/**
 * 截断文件的请求参数
 *
 */
public class TruncateFileRequest extends BaseObjectRequest {

    {
        httpMethod = HttpMethodEnum.PUT;
    }

    private long newLength;

    public TruncateFileRequest() {
    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            桶名
     * @param objectKey
     *            文件名
     * @param newLength
     *            文件截断后的大小
     */
    public TruncateFileRequest(String bucketName, String objectKey, long newLength) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.newLength = newLength;
    }

    /**
     * 获取文件截断后的大小
     *
     * @return 文件截断后的大小
     */
    public long getNewLength() {
        return newLength;
    }

    /**
     * 设置文件截断后的大小
     *
     * @param newLength
     *            文件截断后的大小
     */
    public void setNewLength(long newLength) {
        this.newLength = newLength;
    }
}

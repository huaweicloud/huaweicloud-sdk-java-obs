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

package com.obs.services.model.fs;

import java.io.File;
import java.io.InputStream;

import com.obs.services.model.PutObjectRequest;

/**
 * 创建文件请求参数
 *
 */
public class NewFileRequest extends PutObjectRequest {

    public NewFileRequest() {
        super();
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            文件名
     * @param file
     *            本地文件路径
     */
    public NewFileRequest(String bucketName, String objectKey, File file) {
        super(bucketName, objectKey, file);
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            文件名
     * @param input
     *            待上传的数据流
     */
    public NewFileRequest(String bucketName, String objectKey, InputStream input) {
        super(bucketName, objectKey, input);
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            文件名
     */
    public NewFileRequest(String bucketName, String objectKey) {
        super(bucketName, objectKey);
    }

}

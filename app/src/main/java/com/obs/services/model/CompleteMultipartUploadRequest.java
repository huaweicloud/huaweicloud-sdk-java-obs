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
 * 合并段的请求参数
 */
public class CompleteMultipartUploadRequest extends AbstractMultipartRequest {

    {
        httpMethod = HttpMethodEnum.POST;
    }

    private List<PartEtag> partEtag;

    private String encodingType;

    public CompleteMultipartUploadRequest() {
    }

    public CompleteMultipartUploadRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param uploadId
     *            分段上传任务的ID号
     * @param partEtag
     *            待合并的段列表
     */
    public CompleteMultipartUploadRequest(String bucketName, String objectKey, String uploadId,
                                          List<PartEtag> partEtag) {
        super();
        this.setUploadId(uploadId);
        this.setBucketName(bucketName);
        this.setObjectKey(objectKey);
        this.partEtag = partEtag;
    }

    /**
     * 构造函数
     *
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param uploadId
     *            分段上传任务的ID号
     * @param partEtag
     *            待合并的段列表
     * @param encodingType
     *            对响应中的 Key 进行指定类型的编码。如果 Key 包含 xml 1.0标准不支持的控制字符，
     *            可通过设置 encoding-type 对响应中的Key进行编码，可选值 "url"
     */
    public CompleteMultipartUploadRequest(String bucketName, String objectKey, String uploadId,
                                          List<PartEtag> partEtag, String encodingType) {
        super();
        this.setUploadId(uploadId);
        this.setBucketName(bucketName);
        this.setObjectKey(objectKey);
        this.partEtag = partEtag;
        this.encodingType = encodingType;
    }

    /**
     * 获取待合并的段列表
     *
     * @return 待合并的段列表
     */
    public List<PartEtag> getPartEtag() {
        if (this.partEtag == null) {
            this.partEtag = new ArrayList<PartEtag>();
        }
        return this.partEtag;
    }

    /**
     * 设置待合并的段列表
     * 
     * @param partEtags
     *            待合并的段列表
     */
    public void setPartEtag(List<PartEtag> partEtags) {
        this.partEtag = partEtags;
    }

    /**
     * 对 key 进行 url 编码，处理 xml 1.0 不支持的字符
     *
     * @param encodingType
     *            元素指定 key 的编码类型，可选 url
     */
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * 获取 key 编码类型
     * @return key 编码类型
     */
    public String getEncodingType() {
        return encodingType;
    }

    @Override
    public String toString() {
        return "CompleteMultipartUploadRequest [uploadId=" + this.getUploadId()
                + ", bucketName=" + this.getBucketName() + ", objectKey="
                + this.getObjectKey() + ", partEtag=" + partEtag + ", encodingType=" + encodingType + "]";
    }

}

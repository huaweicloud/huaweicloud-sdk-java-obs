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
    private List<PartEtag> partEtag;

    public CompleteMultipartUploadRequest() {
        super();
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

    @Override
    public String toString() {
        return "CompleteMultipartUploadRequest [uploadId=" + this.getUploadId() 
                + ", bucketName=" + this.getBucketName() + ", objectKey="
                + this.getObjectKey() + ", partEtag=" + partEtag + "]";
    }

}

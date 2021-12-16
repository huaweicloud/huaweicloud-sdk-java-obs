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
import java.util.Arrays;
import java.util.List;

/**
 * 批量删除对象请求参数
 */
public class DeleteObjectsRequest extends GenericRequest {

    {
        httpMethod = HttpMethodEnum.POST;
    }

    private boolean quiet;

    private List<KeyAndVersion> keyAndVersions;

    private String encodingType;

    public DeleteObjectsRequest() {
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     */
    public DeleteObjectsRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param quiet
     *            删除响应方式，false时使用verbose模式,true时使用quiet模式
     * @param keyAndVersions
     *            待删除对象数组
     */
    public DeleteObjectsRequest(String bucketName, boolean quiet, KeyAndVersion[] keyAndVersions) {
        this.bucketName = bucketName;
        this.quiet = quiet;
        this.setKeyAndVersions(keyAndVersions);
    }

    /**
     * 构造函数
     * @param bucketName
     *            桶名
     * @param quiet
     *            用于指定使用quiet模式，只返回删除失败的对象结果
     * @param keyAndVersions
     *            待删除的对象 key 与版本号
     * @param encodingType
     *            对响应中的 Key 进行指定类型的编码。如果 Key 包含 xml 1.0标准不支持的控制字符，
     *            可通过设置 encoding-type 对响应中的Key进行编码，可选值 "url"
     */
    public DeleteObjectsRequest(String bucketName, boolean quiet, KeyAndVersion[] keyAndVersions, String encodingType) {
        this.bucketName = bucketName;
        this.quiet = quiet;
        this.setKeyAndVersions(keyAndVersions);
        this.setEncodingType(encodingType);
    }


    /**
     * 获取批量删除对象的响应模式，false时使用verbose模式, true时使用quiet模式
     * 
     * @return 批量删除对象的响应模式
     */
    public boolean isQuiet() {
        return quiet;
    }

    /**
     * 设置批量删除对象的响应模式，false时使用verbose模式, true时使用quiet模式
     * 
     * @param quiet
     *            批量删除对象的响应模式
     */
    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
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

    /**
     * 获取待删除对象列表
     * 
     * @return 待删除对象列表
     */
    public List<KeyAndVersion> getKeyAndVersionsList() {
        if (this.keyAndVersions == null) {
            this.keyAndVersions = new ArrayList<>();
        }
        return this.keyAndVersions;
    }

    /**
     * 新增待删除的对象
     * 
     * @param objectKey
     *            对象名
     * @param versionId
     *            对象版本号
     * @return 新增的待删除对象
     */
    public KeyAndVersion addKeyAndVersion(String objectKey, String versionId) {
        KeyAndVersion kv = new KeyAndVersion(objectKey, versionId);
        this.getKeyAndVersionsList().add(kv);
        return kv;
    }

    /**
     * 新增待删除的对象
     * 
     * @param objectKey
     *            对象名
     * @return 新增的待删除对象
     */
    public KeyAndVersion addKeyAndVersion(String objectKey) {
        return this.addKeyAndVersion(objectKey, null);
    }

    /**
     * 获取待删除对象数组
     * 
     * @return 待删除对象数组
     */
    public KeyAndVersion[] getKeyAndVersions() {
        return this.getKeyAndVersionsList().toArray(new KeyAndVersion[this.getKeyAndVersionsList().size()]);
    }

    /**
     * 设置待删除对象数组
     * 
     * @param keyAndVersions
     *            待删除对象数组
     */
    public void setKeyAndVersions(KeyAndVersion[] keyAndVersions) {
        if (keyAndVersions != null && keyAndVersions.length > 0) {
            this.keyAndVersions = new ArrayList<>(Arrays.asList(keyAndVersions));
        }
    }

    @Override
    public String toString() {
        return "DeleteObjectsRequest [bucketName=" + bucketName + ", quiet=" + quiet + ", encodingType=" + encodingType
                + ", keyAndVersions=" + this.keyAndVersions + "]";
    }

}

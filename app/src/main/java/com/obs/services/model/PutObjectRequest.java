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

import java.io.File;
import java.io.InputStream;

import com.obs.services.internal.ObsConstraint;

/**
 * 上传对象的请求参数
 */
public class PutObjectRequest extends PutObjectBasicRequest {
    protected File file;

    protected InputStream input;

    protected ObjectMetadata metadata;

    protected int expires = -1;

    protected long offset;

    private boolean autoClose = true;

    private ProgressListener progressListener;

    private long progressInterval = ObsConstraint.DEFAULT_PROGRESS_INTERVAL;

    public PutObjectRequest() {

    }

    public PutObjectRequest(PutObjectBasicRequest request) {
        if (request != null) {
            this.bucketName = request.getBucketName();
            this.objectKey = request.getObjectKey();
            this.acl = request.getAcl();
            this.extensionPermissionMap = request.getExtensionPermissionMap();
            this.sseCHeader = request.getSseCHeader();
            this.sseKmsHeader = request.getSseKmsHeader();
            this.successRedirectLocation = request.getSuccessRedirectLocation();
            this.setRequesterPays(request.isRequesterPays());
        }
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     */
    public PutObjectRequest(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param file
     *            待上传文件
     */
    public PutObjectRequest(String bucketName, String objectKey, File file) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.file = file;
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param input
     *            待上传的数据流
     */
    public PutObjectRequest(String bucketName, String objectKey, InputStream input) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.input = input;
    }

    /**
     * 获取待上传内容在本地文件中的起始位置，仅在设置了本地上传文件路径时有效
     * 
     * @return 待上传内容在本地文件中的起始位置
     */
    public long getOffset() {
        return offset;
    }

    /**
     * 设置待上传内容在本地文件中的起始位置，仅在设置了本地上传文件路径时有效，单位：字节，默认为0
     * 
     * @param offset
     *            待上传内容在本地文件中的起始位置
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    /**
     * 获取待上传的数据流，不可与待上传的文件一起使用
     * 
     * @return 待上传的数据流
     */
    public InputStream getInput() {
        return input;
    }

    /**
     * 设置待上传的数据流，不可与待上传的文件一起使用
     * 
     * @param input
     *            待上传的数据流
     * 
     */
    public void setInput(InputStream input) {
        this.input = input;
        this.file = null;
    }

    /**
     * 获取对象属性，支持content-type，content-length，content-md5，自定义元数据
     * 
     * @return 对象属性
     */
    public ObjectMetadata getMetadata() {
        return metadata;
    }

    /**
     * 设置对象属性，支持content-type，content-length，自定义元数据
     * 
     * @param metadata
     *            对象属性
     */
    public void setMetadata(ObjectMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * 获取待上传的文件，不可与待上传的数据流一起使用
     * 
     * @return 待上传的文件
     */
    public File getFile() {
        return file;
    }

    /**
     * 设置待上传的文件，不可与待上传的数据流一起使用
     * 
     * @param file
     *            待上传的文件
     */
    public void setFile(File file) {
        this.file = file;
        this.input = null;
    }

    /**
     * 获取对象的过期时间
     * 
     * @return 对象的过期时间
     */
    public int getExpires() {
        return expires;
    }

    /**
     * 设置对象的过期时间，正整数
     * 
     * @param expires
     *            对象的过期时间
     */
    public void setExpires(int expires) {
        this.expires = expires;
    }

    /**
     * 获取是否自动关闭输入流标识，默认为true
     * 
     * @return 是否自动关闭输入流标识
     */
    public boolean isAutoClose() {
        return autoClose;
    }

    /**
     * 设置是否自动关闭输入流标识，默认为true
     * 
     * @param autoClose
     *            是否自动关闭输入流标识
     */
    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    /**
     * 获取数据传输监听器
     * 
     * @return 数据传输监听器
     */
    public ProgressListener getProgressListener() {
        return progressListener;
    }

    /**
     * 设置数据传输监听器
     * 
     * @param progressListener
     *            数据传输监听器
     */
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * 获取数据传输监听器回调的阈值，默认为100KB
     * 
     * @return 数据传输监听器回调的阈值
     */
    public long getProgressInterval() {
        return progressInterval;
    }

    /**
     * 设置数据传输监听器回调的阈值，默认为100KB
     * 
     * @param progressInterval
     *            数据传输监听器回调的阈值
     */
    public void setProgressInterval(long progressInterval) {
        this.progressInterval = progressInterval;
    }

    @Override
    public String toString() {
        return "PutObjectRequest [file=" + file + ", input=" + input + ", metadata=" + metadata
                + ", isEncodeHeaders=" + encodeHeaders + ", expires=" + expires
                + ", offset=" + offset + ", autoClose=" + autoClose + ", progressListener=" + progressListener
                + ", progressInterval=" + progressInterval + ", getBucketName()=" + getBucketName()
                + ", getObjectKey()=" + getObjectKey() + ", getSseKmsHeader()=" + getSseKmsHeader()
                + ", getSseCHeader()=" + getSseCHeader() + ", getAcl()=" + getAcl() + ", getSuccessRedirectLocation()="
                + getSuccessRedirectLocation() + ", getAllGrantPermissions()=" + getAllGrantPermissions()
                + ", getExtensionPermissionMap()=" + getExtensionPermissionMap() + ", isRequesterPays()="
                + isRequesterPays() + "]";
    }
}

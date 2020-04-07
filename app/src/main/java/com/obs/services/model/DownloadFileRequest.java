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

import java.util.Date;

import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.utils.ServiceUtils;

/**
 * 下载文件的请求参数
 */
public class DownloadFileRequest extends GenericRequest {
    // 桶名
    private String bucketName;
    // 对象名
    private String objectKey;
    // 本地文件
    private String downloadFile;
    // 分片大小，单位字节，默认9MB
    private long partSize = 1024 * 1024 * 9l;
    // 分片上传线程数，默认1
    private int taskNum = 1;
    // 开启断点续传时记录下载信息的本地文件
    private String checkpointFile;
    // 是否开启断点续传，默认为false
    private boolean enableCheckpoint;
    // 限定条件下载对应的If-Modified-Since参数,如果指定的时间早于对象的实际修改时间,正常传送文件返回文件内容
    private Date ifModifiedSince;
    // If-Unmodified-Since参数,如果传入参数中的时间等于或者晚于文件实际修改时间,则传送文件
    private Date ifUnmodifiedSince;
    // If-Match参数,如果对象的ETag值与该参数值相同，则返回对象内容,目前只支持单一形式.
    private String ifMatchTag;
    // If-None-Match参数,如果对象的ETag值与该参数值不相同，则返回对象内容,目前只支持单一形式.
    private String ifNoneMatchTag;
    // 开启多版本，指定版本号下载
    private String versionId;

    private ProgressListener progressListener;

    private long progressInterval = ObsConstraint.DEFAULT_PROGRESS_INTERVAL;

    private CacheOptionEnum cacheOption;

    private long ttl;

    /**
     * 构造参数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     */
    public DownloadFileRequest(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.downloadFile = objectKey;
    }

    /**
     * 构造参数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param downloadFile
     *            下载文件的目标路径
     */
    public DownloadFileRequest(String bucketName, String objectKey, String downloadFile) {
        this(bucketName, objectKey);
        this.downloadFile = downloadFile;
    }

    /**
     * 构造参数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param downloadFile
     *            下载文件的目标路径
     * @param partSize
     *            下载时的分段大小
     */
    public DownloadFileRequest(String bucketName, String objectKey, String downloadFile, long partSize) {
        this(bucketName, objectKey);
        this.downloadFile = downloadFile;
        this.partSize = partSize;
    }

    /**
     * 构造参数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param downloadFile
     *            下载文件的目标路径
     * @param partSize
     *            下载时的分段大小
     * @param taskNum
     *            用于并发执行下载任务的最大线程数
     */
    public DownloadFileRequest(String bucketName, String objectKey, String downloadFile, long partSize, int taskNum) {
        this(bucketName, objectKey, downloadFile, partSize, taskNum, false);
    }

    /**
     * 构造参数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param downloadFile
     *            下载文件的目标路径
     * @param partSize
     *            下载时的分段大小
     * @param taskNum
     *            用于并发执行下载任务的最大线程数
     * @param enableCheckpoint
     *            是否开启断点续传模式
     * 
     */
    public DownloadFileRequest(String bucketName, String objectKey, String downloadFile, long partSize, int taskNum,
            boolean enableCheckpoint) {
        this(bucketName, objectKey, downloadFile, partSize, taskNum, enableCheckpoint, null);
    }

    /**
     * 构造参数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param downloadFile
     *            下载文件的目标路径
     * @param partSize
     *            下载时的分段大小
     * @param taskNum
     *            用于并发执行下载任务的最大线程数
     * @param enableCheckpoint
     *            是否开启断点续传模式
     * @param checkpointFile
     *            断点续传模式下，记录下载进度的文件
     * 
     */
    public DownloadFileRequest(String bucketName, String objectKey, String downloadFile, long partSize, int taskNum,
            boolean enableCheckpoint, String checkpointFile) {
        this(bucketName, objectKey);
        this.partSize = partSize;
        this.taskNum = taskNum;
        this.downloadFile = downloadFile;
        this.enableCheckpoint = enableCheckpoint;
        this.checkpointFile = checkpointFile;
    }

    /**
     * 构造参数
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param downloadFile
     *            下载文件的目标路径
     * @param partSize
     *            下载时的分段大小
     * @param taskNum
     *            用于并发执行下载任务的最大线程数
     * @param enableCheckpoint
     *            是否开启断点续传模式
     * @param checkpointFile
     *            断点续传模式下，记录下载进度的文件
     * @param versionId
     *            对象的版本号
     * 
     */
    public DownloadFileRequest(String bucketName, String objectKey, String downloadFile, long partSize, int taskNum,
            boolean enableCheckpoint, String checkpointFile, String versionId) {
        this(bucketName, objectKey);
        this.partSize = partSize;
        this.taskNum = taskNum;
        this.downloadFile = downloadFile;
        this.enableCheckpoint = enableCheckpoint;
        this.checkpointFile = checkpointFile;
        this.versionId = versionId;
    }

    /**
     * 获取桶名
     * 
     * @return 桶名
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置桶名
     * 
     * @param bucketName
     *            桶名
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取对象名
     * 
     * @return 对象名
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * 设置对象名
     * 
     * @param objectKey
     *            对象名
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    /**
     * 获取下载文件的目标路径
     * 
     * @return 下载文件的目标路径
     */
    public String getDownloadFile() {
        return downloadFile;
    }

    /**
     * 设置下载文件的目标路径
     * 
     * @param downloadFile
     *            下载文件的目标路径
     */
    public void setDownloadFile(String downloadFile) {
        this.downloadFile = downloadFile;
    }

    /**
     * 获取下载时的分段大小
     * 
     * @return 下载时的分段大小
     */
    public long getPartSize() {
        return partSize;
    }

    /**
     * 设置下载时的分段大小
     * 
     * @param partSize
     *            下载时的分段大小
     */
    public void setPartSize(long partSize) {
        this.partSize = partSize;
    }

    /**
     * 获取用于并发执行下载任务的最大线程数
     * 
     * @return 用于并发执行下载任务的最大线程数
     */
    public int getTaskNum() {
        return taskNum;
    }

    /**
     * 设置用于并发执行下载任务的最大线程数
     * 
     * @param taskNum
     *            用于并发执行下载任务的最大线程数
     */
    public void setTaskNum(int taskNum) {
        if (taskNum < 1) {
            this.taskNum = 1;
        } else if (taskNum > 1000) {
            this.taskNum = 1000;
        } else {
            this.taskNum = taskNum;
        }
    }

    /**
     * 判断是否开启断点续传模式
     * 
     * @return 是否开启断点续传模式标识
     */
    public boolean isEnableCheckpoint() {
        return enableCheckpoint;
    }

    /**
     * 设置是否开启断点续传模式
     * 
     * @param enableCheckpoint
     *            是否开启断点续传模式标识
     */
    public void setEnableCheckpoint(boolean enableCheckpoint) {
        this.enableCheckpoint = enableCheckpoint;
    }

    /**
     * 获取断点续传模式下，记录下载进度的文件
     * 
     * @return 记录下载进度的文件
     */
    public String getCheckpointFile() {
        return checkpointFile;
    }

    /**
     * 设置断点续传模式下，记录下载进度的文件
     * 
     * @param checkpointFile
     *            记录下载进度的文件
     */
    public void setCheckpointFile(String checkpointFile) {
        this.checkpointFile = checkpointFile;
    }

    /**
     * 获取下载时的临时文件
     * 
     * @return 下载时的临时文件
     */
    public String getTempDownloadFile() {
        return downloadFile + ".tmp";
    }

    /**
     * 获取下载对象的时间条件（修改则下载），如果对象在此参数指定的时间之后有修改则进行下载，否则返回304（Not Modified）
     * 
     * @return 下载对象的时间条件
     */
    public Date getIfModifiedSince() {
        return ServiceUtils.cloneDateIgnoreNull(this.ifModifiedSince);
    }

    /**
     * 设置下载对象的时间条件（修改则下载），如果对象在此参数指定的时间之后有修改则进行下载，否则返回304（Not Modified）
     * 
     * @param ifModifiedSince
     *            下载对象的时间条件
     */
    public void setIfModifiedSince(Date ifModifiedSince) {
        this.ifModifiedSince = ServiceUtils.cloneDateIgnoreNull(ifModifiedSince);
    }

    /**
     * 获取下载对象的时间条件（未修改则下载），如果对象在此参数指定的时间之后没有修改则进行下载，否则返回412（ 前置条件不满足）
     * 
     * @return 下载对象的时间条件
     */
    public Date getIfUnmodifiedSince() {
        return ServiceUtils.cloneDateIgnoreNull(this.ifUnmodifiedSince);
    }

    /**
     * 设置下载对象的时间条件（未修改则下载），如果对象在此参数指定的时间之后没有修改则进行下载，否则返回412（ 前置条件不满足）
     * 
     * @param ifUnmodifiedSince
     *            下载对象的时间条件
     */
    public void setIfUnmodifiedSince(Date ifUnmodifiedSince) {
        this.ifUnmodifiedSince = ServiceUtils.cloneDateIgnoreNull(ifUnmodifiedSince);
    }

    /**
     * 获取下载对象的校验值条件（相等则下载），如果对象的etag校验值与此参数指定的值相等则进行下载。否则返回412（前置条件不满足）
     * 
     * @return 下载对象的校验值条件
     */
    public String getIfMatchTag() {
        return ifMatchTag;
    }

    /**
     * 设置下载对象的校验值条件（相等则下载），如果对象的etag校验值与此参数指定的值相等则进行下载。否则返回412（前置条件不满足）
     * 
     * @param ifMatchTag
     *            下载对象的校验值条件
     */
    public void setIfMatchTag(String ifMatchTag) {
        this.ifMatchTag = ifMatchTag;
    }

    /**
     * 获取下载对象的校验值条件（不相等则下载），如果对象的etag校验值与此参数指定的值不相等则进行下载。否则返回304（Not Modified）
     * 
     * @return 下载对象的校验值条件
     */
    public String getIfNoneMatchTag() {
        return ifNoneMatchTag;
    }

    /**
     * 设置下载对象的校验值条件（不相等则下载），如果对象的etag校验值与此参数指定的值不相等则进行下载。否则返回304（Not Modified）
     * 
     * @param ifNoneMatchTag
     *            下载对象的校验值条件
     * 
     */
    public void setIfNoneMatchTag(String ifNoneMatchTag) {
        this.ifNoneMatchTag = ifNoneMatchTag;
    }

    /**
     * 获取对象的版本号
     * 
     * @return 对象的版本号
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * 设置对象的版本号
     * 
     * @param versionId
     *            对象的版本号
     * 
     */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
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

    /**
     * 获取预读缓存的控制选项
     * 
     * @return 预读缓存的控制选项
     */
    public CacheOptionEnum getCacheOption() {
        return cacheOption;
    }

    /**
     * 设置预读缓存的控制选项
     * 
     * @param cacheOption
     *            预读缓存的控制选项
     */
    public void setCacheOption(CacheOptionEnum cacheOption) {
        this.cacheOption = cacheOption;
    }

    /**
     * 获取缓存数据过期时间
     * 
     * @return 缓存数据过期时间
     */
    public long getTtl() {
        return ttl;
    }

    /**
     * 设置缓存数据过期时间
     * 
     * @param ttl
     *            缓存数据过期时间
     */
    public void setTtl(long ttl) {
        if (ttl < 0 || ttl > 259200) {
            ttl = 60 * 60 * 24L;
        }
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return "DownloadFileRequest [bucketName=" + bucketName + ", objectKey=" + objectKey + ", downloadFile="
                + downloadFile + ", partSize=" + partSize + ", taskNum=" + taskNum + ", checkpointFile="
                + checkpointFile + ", enableCheckpoint=" + enableCheckpoint + ", ifModifiedSince=" + ifModifiedSince
                + ", ifUnmodifiedSince=" + ifUnmodifiedSince + ", ifMatchTag=" + ifMatchTag + ", ifNoneMatchTag="
                + ifNoneMatchTag + ", versionId=" + versionId + "]";
    }
}

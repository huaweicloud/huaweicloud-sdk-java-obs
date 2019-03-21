/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.obs.services.model;

import com.obs.services.internal.ObsConstraint;
import com.obs.services.model.ObjectMetadata;

/**
 * 上传文件的请求参数
 */
public class UploadFileRequest {
	
    private String bucketName;
    private String objectKey;
    // 分片大小，单位字节，默认5MB
    private long partSize = 1024 * 1024 * 5l;
    // 分片上传线程数，默认1
    private int taskNum = 1;
    // 需要上传的本地文件
    private String uploadFile;
    // 是否开启断点续传
    private boolean enableCheckpoint = false;
    // 断点续传时保存分片上传的信息的本地文件
    private String checkpointFile;
    // 上传对象的元数据
    private ObjectMetadata objectMetadata;
    // 是否checkSum 保证数据一致性
    private boolean enableCheckSum = false;
    
    private ProgressListener progressListener;
    
    private long progressInterval = ObsConstraint.DEFAULT_PROGRESS_INTERVAL;
    
    /**
     * 构造函数
     * @param bucketName 桶名
     * @param objectKey 对象名
     */
	public UploadFileRequest(String bucketName, String objectKey) {
		this.bucketName = bucketName;
		this.objectKey = objectKey;
	}

	/**
	 * 构造函数
	 * 
	 * @param bucketName 桶名
	 * @param objectKey 对象名
	 * @param uploadFile 待上传的本地文件
	 */
	public UploadFileRequest(String bucketName, String objectKey, String uploadFile) {
		this(bucketName, objectKey);
		this.uploadFile = uploadFile;
	}

	/**
	 * 构造函数
	 * 
	 * @param bucketName 桶名
	 * @param objectKey 对象名
	 * @param uploadFile 待上传的本地文件
	 * @param partSize 上传时的分段大小
	 */
	public UploadFileRequest(String bucketName, String objectKey, String uploadFile, long partSize) {
		this(bucketName, objectKey, uploadFile);
		this.partSize = partSize;
	}
	
	/**
	 * 构造函数
	 * 
	 * @param bucketName 桶名
	 * @param objectKey 对象名
	 * @param uploadFile 待上传的本地文件
	 * @param partSize 上传时的分段大小
	 * @param taskNum 用于并发执行上传任务的最大线程数
	 */
	public UploadFileRequest(String bucketName, String objectKey, String uploadFile, long partSize,int taskNum) {
		this(bucketName, objectKey, uploadFile, partSize);
		this.taskNum = taskNum;
	}
	
	/**
	 * 构造函数
	 * 
	 * @param bucketName 桶名
	 * @param objectKey 对象名
	 * @param uploadFile 待上传的本地文件
     * @param partSize 上传时的分段大小
     * @param taskNum 用于并发执行上传任务的最大线程数
	 * @param enableCheckpoint 是否开启断点续传模式
	 */
	public UploadFileRequest(String bucketName, String objectKey, String uploadFile, long partSize, int taskNum,
			boolean enableCheckpoint) {
		this(bucketName, objectKey, uploadFile, partSize, taskNum, enableCheckpoint, null);
		
	}

	/**
	 * 构造函数
	 * 
	 * @param bucketName 桶名
	 * @param objectKey 对象名
	 * @param uploadFile 待上传的本地文件
     * @param partSize 上传时的分段大小
     * @param taskNum 用于并发执行上传任务的最大线程数
     * @param enableCheckpoint 是否开启断点续传模式
	 * @param checkpointFile 断点续传模式下，记录上传进度的文件
	 */
	public UploadFileRequest(String bucketName, String objectKey, String uploadFile, long partSize, int taskNum,
			boolean enableCheckpoint, String checkpointFile) {
		this(bucketName, objectKey);
		this.partSize = partSize;
		this.taskNum = taskNum;
		this.uploadFile = uploadFile;
		this.enableCheckpoint = enableCheckpoint;
		this.checkpointFile = checkpointFile;
	}

	/**
	 * 构造函数
	 * 
	 * @param bucketName 桶名
	 * @param objectKey 对象名
	 * @param uploadFile 需上传的本地文件
     * @param partSize 上传时的分段大小
     * @param taskNum 用于并发执行上传任务的最大线程数
     * @param enableCheckpoint 是否开启断点续传模式
     * @param checkpointFile 断点续传模式下，记录上传进度的文件
	 * @param enableCheckSum 断点续传模式下，非首次上传时是否校验待上传文件的内容
	 */
	public UploadFileRequest(String bucketName, String objectKey, String uploadFile, long partSize, int taskNum,
			boolean enableCheckpoint, String checkpointFile, boolean enableCheckSum) {
		this(bucketName, objectKey,uploadFile,partSize,taskNum,enableCheckpoint,checkpointFile);
		this.enableCheckSum = enableCheckSum;
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
	 * @param bucketName 桶名
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
	 * @param objectKey 对象名
	 */
	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}
	
	/**
	 * 获取上传时的分段大小
	 * 
	 * @return 上传时的分段大小
	 */ 
	public long getPartSize() {
		return partSize;
	}

	/**
	 * 设置上传时的分段大小
	 * 
	 * @param partSize 上传时的分段大小
	 */
	public void setPartSize(long partSize) {
		if (partSize < 5 * 1024 * 1024l) {
            this.partSize = 5 * 1024 * 1024l;
        } else if (partSize > 5 * 1024 * 1024 * 1024l) {
        	this.partSize = 5 * 1024 * 1024 * 1024l;
		}else {
            this.partSize = partSize;
        }
	}

	/**
	 * 获取用于并发执行上传任务的最大线程数
	 * 
	 * @return 用于并发执行上传任务的最大线程数
	 */
	public int getTaskNum() {
		return taskNum;
	}

	/**
	 * 设置用于并发执行上传任务的最大线程数
	 * 
	 * @param taskNum 用于并发执行上传任务的最大线程数
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
	 * 获取待上传的本地文件
	 * 
	 * @return 待上传的本地文件
	 */
	public String getUploadFile() {
		return uploadFile;
	}

	/**
	 * 设置待上传的本地文件
	 * 
	 * @param uploadFile 待上传的本地文件
	 */
	public void setUploadFile(String uploadFile) {
		this.uploadFile = uploadFile;
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
	 * @param enableCheckpoint 是否开启断点续传模式标识
	 */
	public void setEnableCheckpoint(boolean enableCheckpoint) {
		this.enableCheckpoint = enableCheckpoint;
	}

	/**
	 * 获取断点续传模式下，记录上传进度的文件
	 * 
	 * @return 记录上传进度的文件
	 */
	public String getCheckpointFile() {
		return checkpointFile;
	}

	/**
	 * 设置断点续传模式下，记录上传进度的文件
	 * 
	 * @param checkpointFile 记录上传进度的文件
	 */
	public void setCheckpointFile(String checkpointFile) {
		this.checkpointFile = checkpointFile;
	}

	/**
	 * 获取对象的属性
	 * 
	 * @return 对象的属性
	 */
	public ObjectMetadata getObjectMetadata() {
		return objectMetadata;
	}
	
	/**
	 * 设置对象的属性
	 * 
	 * @param objectMetadata 对象的属性
	 */
	public void setObjectMetadata(ObjectMetadata objectMetadata) {
		this.objectMetadata = objectMetadata;
	}

	/**
	 * 判断断点续传模式下，是否校验待上传文件的内容
	 * 
	 * @return 是否校验待上传文件的内容标识
	 */
	public boolean isEnableCheckSum() {
		return enableCheckSum;
	}

	/**
	 * 设置断点续传模式下，是否校验待上传文件的内容
	 * 
	 * @param enableCheckSum 是否校验待上传文件的内容标识
	 */
	public void setEnableCheckSum(boolean enableCheckSum) {
		this.enableCheckSum = enableCheckSum;
	}
	
	/**
	 * 获取数据传输监听器
	 * @return 数据传输监听器
	 */
	public ProgressListener getProgressListener() {
		return progressListener;
	}

	/**
	 * 设置数据传输监听器
	 * @param progressListener 数据传输监听器
	 */
	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}
	
	/**
	 * 获取数据传输监听器回调的阈值，默认为100KB
	 * @return 数据传输监听器回调的阈值
	 */
	public long getProgressInterval() {
		return progressInterval;
	}
	
	/**
	 * 设置数据传输监听器回调的阈值，默认为100KB
	 * @param progressInterval 数据传输监听器回调的阈值
	 */
	public void setProgressInterval(long progressInterval) {
		this.progressInterval = progressInterval;
	}

    @Override
    public String toString()
    {
        return "UploadFileRequest [bucketName=" + bucketName + ", objectKey=" + objectKey + ", partSize=" + partSize + ", taskNum="
            + taskNum + ", uploadFile=" + uploadFile + ", enableCheckpoint=" + enableCheckpoint + ", checkpointFile=" + checkpointFile
            + ", objectMetadata=" + objectMetadata + ", enableCheckSum=" + enableCheckSum + "]";
    }
	
}

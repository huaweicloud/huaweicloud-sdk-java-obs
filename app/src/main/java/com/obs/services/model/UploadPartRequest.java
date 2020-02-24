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
 * 上传段的请求参数
 * 
 */
public class UploadPartRequest
{
    private String uploadId;
    
    private String bucketName;
    
    private String objectKey;
    
    private int partNumber;
    
    private Long partSize;
    
    private long offset;
    
    private SseCHeader sseCHeader;
    
    private String contentMd5;
    
    private boolean attachMd5 = false;
    
    private File file;
    
    private InputStream input;
    
    private boolean autoClose = true;
    
    private ProgressListener progressListener;
    
    private long progressInterval = ObsConstraint.DEFAULT_PROGRESS_INTERVAL;
    
    public UploadPartRequest()
    {
    }
    
    /**
     * 构造函数
     * @param bucketName 分段上传任务所属的桶名
     * @param objectKey 分段上传任务所属的对象名
     */
    public UploadPartRequest(String bucketName, String objectKey)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }
    
    /**
     * 构造函数
     * 
     * @param bucketName 分段上传任务所属的桶名
     * @param objectKey 分段上传任务所属的对象名
     * @param fileName 待上传的文件名
     */
    public UploadPartRequest(String bucketName, String objectKey, String fileName)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.file = new File(fileName);
    }
    
    /**
     * 构造函数
     * 
     * @param bucketName 分段上传任务所属的桶名
     * @param objectKey 分段上传任务所属的对象名
     * @param file 待上传的文件
     */
    public UploadPartRequest(String bucketName, String objectKey, File file)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.file = file;
    }
    
    /**
     * 构造函数
     * @param bucketName 分段上传任务所属的桶名
     * @param objectKey 分段上传任务所属的对象名
     * @param partSize 分段大小，单位：字节
     * @param input 待上传的数据流
     */
    public UploadPartRequest(String bucketName, String objectKey, Long partSize, InputStream input)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.partSize = partSize;
        this.input = input;
    }
    
    /**
     * 构造函数
     * @param bucketName 分段上传任务所属的桶名
     * @param objectKey 分段上传任务所属的对象名
     * @param partSize 分段大小，单位：字节
     * @param offset 分段在本地文件中的起始位置，单位：字节，默认为0
     * @param file 待上传的文件
     */
    public UploadPartRequest(String bucketName, String objectKey, Long partSize, long offset, File file)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.partSize = partSize;
        this.offset = offset;
        this.file = file;
    }
    
    /**
     * 获取SSE-C加密方式头域信息
     * 
     * @return SSE-C加密方式头域信息
     */
    public SseCHeader getSseCHeader()
    {
        return sseCHeader;
    }
    
    /**
     * 设置SSE-C加密方式头域信息
     * 
     * @param sseCHeader SSE-C加密方式头域信息
     */
    public void setSseCHeader(SseCHeader sseCHeader)
    {
        this.sseCHeader = sseCHeader;
    }
    
    /**
     * 获取分段在本地文件中的起始位置，单位：字节，默认为0
     * 
     * @return 分段在本地文件中的起始位置
     */
    public long getOffset()
    {
        return offset;
    }
    
    /**
     * 设置分段在本地文件中的起始位置，仅在设置了本地上传文件路径时有效，单位：字节，默认为0
     * 
     * @param offset 分段在本地文件中的起始位置
     */
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    /**
     * 获取分段号
     * 
     * @return 分段号
     */
    public int getPartNumber()
    {
        return partNumber;
    }
    
    /**
     * 设置分段号
     * 
     * @param partNumber 分段号
     */
    public void setPartNumber(int partNumber)
    {
        this.partNumber = partNumber;
    }
    
    /**
     * 获取分段上传任务的ID号
     * 
     * @return 分段上传任务的ID号
     */
    public String getUploadId()
    {
        return uploadId;
    }
    
    /**
     * 设置分段上传任务的ID号
     * 
     * @param uploadId 分段上传任务的ID号
     */
    public void setUploadId(String uploadId)
    {
        this.uploadId = uploadId;
    }
    
    /**
     * 获取分段上传任务所属的桶名
     * 
     * @return 分段上传任务所属的桶名
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    /**
     * 设置分段上传任务所属的桶名
     * 
     * @param bucketName 分段上传任务所属的桶名
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * 获取分段上传任务所属的对象名
     * 
     * @return 分段上传任务所属的对象名
     */
    public String getObjectKey()
    {
        return objectKey;
    }
    
    /**
     * 设置分段上传任务所属的对象名
     * 
     * @param objectKey 分段上传任务所属的对象名
     */
    public void setObjectKey(String objectKey)
    {
        this.objectKey = objectKey;
    }
    
    /**
     * 设置分段大小，单位：字节
     * 
     * @param partSize 分段大小
     */
    public void setPartSize(Long partSize)
    {
        this.partSize = partSize;
    }
    
    /**
     * 获取分段大小，单位：字节
     * 
     * @return 分段大小
     */
    public Long getPartSize()
    {
        return partSize;
    }
    
    /**
     * 获取待上传的文件，不可与待上传的数据流一起使用
     * 
     * @return 待上传的文件
     */
    public File getFile()
    {
        return file;
    }
    
    /**
     * 设置待上传的文件，不可与待上传的数据流一起使用
     * 
     * @param file 待上传的文件
     */
    public void setFile(File file)
    {
        this.file = file;
        this.input = null;
    }
    
    /**
     * 获取待上传的数据流，不可与待上传的文件一起使用
     * 
     * @return 待上传的数据流
     */
    public InputStream getInput()
    {
        return input;
    }
    
    /**
     * 设置待上传的数据流，不可与待上传的文件一起使用
     * 
     * @param input 待上传的数据流
     */
    public void setInput(InputStream input)
    {
        this.input = input;
        this.file = null;
    }
    
    /**
     * 判断是否自动计算待上传数据的MD5值，当设置了MD5值忽略该参数
     * @return 是否计算待上传数据的MD5值标识
     */
    public boolean isAttachMd5()
    {
        return attachMd5;
    }

    /**
     * 设置是否自动计算待上传数据的MD5值，当设置了MD5值忽略该参数
     * @param attachMd5 是否计算待上传数据的MD5值标识
     */
    public void setAttachMd5(boolean attachMd5)
    {
        this.attachMd5 = attachMd5;
    }

    /**
     * 设置待上传数据的MD5值
     * @return 待上传数据的MD5值
     */
    public String getContentMd5()
    {
        return contentMd5;
    }

    /**
     * 获取待上传数据的MD5值
     * @param contentMd5 待上传数据的MD5值
     */
    public void setContentMd5(String contentMd5)
    {
        this.contentMd5 = contentMd5;
    }
    
    /**
     * 获取是否自动关闭输入流标识，默认为true
     * @return 是否自动关闭输入流标识
     */
    public boolean isAutoClose() {
		return autoClose;
	}

    /**
     * 设置是否自动关闭输入流标识，默认为true
     * @param autoClose 是否自动关闭输入流标识
     */
	public void setAutoClose(boolean autoClose) {
		this.autoClose = autoClose;
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
        return "UploadPartRequest [uploadId=" + uploadId + ", bucketName=" + bucketName + ", objectKey=" + objectKey + ", partNumber="
            + partNumber + ", partSize=" + partSize + ", offset=" + offset + ", sseCHeader=" + sseCHeader + ", contentMd5=" + contentMd5
            + ", attachMd5=" + attachMd5 + ", file=" + file + ", input=" + input + "]";
    }

    

}

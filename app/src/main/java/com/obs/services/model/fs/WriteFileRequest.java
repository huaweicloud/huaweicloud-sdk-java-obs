package com.obs.services.model.fs;

import java.io.File;
import java.io.InputStream;

import com.obs.services.model.AppendObjectRequest;

/**
 * 写文件内容的请求参数
 *
 */
public class WriteFileRequest extends AppendObjectRequest{

	public WriteFileRequest() {
		super();
	}
	
	 /**
     * 构造函数
     * @param bucketName 桶名
     * @param objectKey 文件名
     */
    public WriteFileRequest(String bucketName, String objectKey)
    {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }
    
    /**
     * 构造函数
     * @param bucketName 桶名
     * @param objectKey 文件名
     * @param file 本地文件路径
     */
    public WriteFileRequest(String bucketName, String objectKey, File file)
    {
        this(bucketName, objectKey);
        this.file = file;
    }
    
    /**
     * 构造函数
     * @param bucketName 桶名
     * @param objectKey 文件名
     * @param file 本地文件路径
     * @param position 写文件的起始位置
     */
    public WriteFileRequest(String bucketName, String objectKey, File file, long position)
    {
    	 this(bucketName, objectKey, file);
    	 this.position = position;
    }
    
    /**
     * 构造函数
     * @param bucketName 桶名
     * @param objectKey 文件名
     * @param input 待上传的数据流
     */
    public WriteFileRequest(String bucketName, String objectKey, InputStream input)
    {
    	this(bucketName, objectKey);
    	this.input = input;
    }
    
    /**
     * 构造函数
     * @param bucketName 桶名
     * @param objectKey 文件名
     * @param input 待上传的数据流
     * @param position 写文件的起始位置
     */
    public WriteFileRequest(String bucketName, String objectKey, InputStream input, long position)
    {
    	this(bucketName, objectKey, input);
    	this.position = position;
    }
}

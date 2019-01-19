package com.obs.services.model.fs;

import java.io.File;
import java.io.InputStream;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.ObjectMetadata;

/**
 * 支持文件接口的桶
 *
 */
public class ObsFSBucket{
	
	protected ObsClient innerClient;
	
    private String bucketName;
    
    private String location;
    
	public ObsFSBucket(String bucketName, String location) {
		super();
		this.bucketName = bucketName;
		this.location = location;
	}
	
	/**
	 * 设置桶的文件网关特性状态
	 * @param status 桶的文件网关特性状态
	 * @return 设置桶的文件网关特性状态的响应结果
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public HeaderResponse setFSStatus(FSStatusEnum status) throws ObsException{
		this.checkInternalClient();
		return this.innerClient.setBucketFSStatus(new SetBucketFSStatusRequest(this.bucketName, status));
	}
	
	/**
	 * 创建文件夹
	 * @param folderName 文件夹名
	 * @return 代表支持文件接口的桶中的文件夹
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ObsFSFolder newFolder(String folderName) throws ObsException{
		this.checkInternalClient();
		return this.innerClient.newFolder(new NewFolderRequest(this.bucketName, folderName));
	}
	
	/**
	 * 创建文件
	 * @param fileName 文件名
	 * @param input 文件输入流
	 * @param metadata 文件的属性
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ObsFSFile newFile(String fileName, InputStream input, ObjectMetadata metadata) throws ObsException{
		this.checkInternalClient();
		NewFileRequest request = new NewFileRequest(this.bucketName, fileName);
		request.setInput(input);
		return this.innerClient.newFile(request);
	}
	
	/**
	 * 创建文件
	 * @param fileName 文件名
	 * @param input 文件输入流
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ObsFSFile newFile(String fileName, InputStream input) throws ObsException{
		return this.newFile(fileName, input, null);
	}
	
	/**
	 * 创建文件
	 * @param fileName 文件名
	 * @param file 本地文件路径
	 * @param metadata 文件的属性
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ObsFSFile newFile(String fileName, File file, ObjectMetadata metadata) throws ObsException{
		this.checkInternalClient();
		NewFileRequest request = new NewFileRequest(this.bucketName, fileName);
		request.setFile(file);
		return this.innerClient.newFile(request);
	}
	
	/**
	 * 创建文件
	 * @param fileName 文件名
	 * @param file 本地文件路径
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ObsFSFile newFile(String fileName, File file) throws ObsException{
		return this.newFile(fileName, file, null);
	}

    /**
     * 获取桶名
     * 
     * @return 桶名
     */
    public String getBucketName()
    {
        return bucketName;
    }

    /**
     * 获取桶的区域位置
     * @return 桶的区域位置
     */
    public String getLocation()
    {
        return location;
    }

    protected void checkInternalClient() {
		ServiceUtils.asserParameterNotNull(this.innerClient, "ObsClient is null");
	}
    
    @Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.innerClient = null;
	}

}

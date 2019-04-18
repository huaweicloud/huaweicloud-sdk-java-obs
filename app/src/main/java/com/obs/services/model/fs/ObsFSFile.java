package com.obs.services.model.fs;

import java.io.File;
import java.io.InputStream;

import com.obs.services.exception.ObsException;
import com.obs.services.model.StorageClassEnum;

/**
 * 支持文件接口的桶中的文件
 *
 */
public class ObsFSFile extends ObsFSFolder{

	public ObsFSFile(String bucketName, String objectKey, String etag, String versionId, StorageClassEnum storageClass,
			String objectUrl) {
		super(bucketName, objectKey, etag, versionId, storageClass, objectUrl);
	}
	
	/**
	 * 获取文件的属性
	 * @return 文件的属性
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ObsFSAttribute attribute() throws ObsException {
		return super.attribute();
	}
	
	/**
	 * 读取文件内容
	 * @return 读取文件内容的响应结果
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ReadFileResult read() throws ObsException{
		this.checkInternalClient();
		ReadFileRequest request = new ReadFileRequest(this.getBucketName(), this.getObjectKey());
		return this.innerClient.readFile(request);
	}
	
	/**
	 * 读取文件内容
	 * @param rangeStart 读取文件内容的起始位置
	 * @param rangeEnd 读取文件内容的结束位置
	 * @return ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ReadFileResult read(long rangeStart, long rangeEnd) throws ObsException{
		this.checkInternalClient();
		ReadFileRequest request = new ReadFileRequest(this.getBucketName(), this.getObjectKey());
		request.setRangeStart(rangeStart);
		request.setRangeEnd(rangeEnd);
		return this.innerClient.readFile(request);
	}
	
	/**
	 * 写文件内容
	 * @param file 本地文件路径
	 * @param position 写文件的起始位置
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ObsFSFile write(File file, long position) throws ObsException {
		this.checkInternalClient();
		WriteFileRequest request = new WriteFileRequest(this.getBucketName(), this.getObjectKey(), file, position);
		return this.innerClient.writeFile(request);
	}
	
	/**
	 * 写文件内容
	 * @param file 本地文件路径
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ObsFSFile write(File file) throws ObsException{
		return this.write(file, 0);
	}
	
	/**
	 * 写文件内容
	 * @param input 待上传的数据流
	 * @param position 写文件的起始位置
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ObsFSFile write(InputStream input, long position) throws ObsException {
		this.checkInternalClient();
		WriteFileRequest request = new WriteFileRequest(this.getBucketName(), this.getObjectKey(), input, position);
		return this.innerClient.writeFile(request);
	}
	
	/**
	 * 在文件末尾追加内容
	 * @param file 本地文件路径
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ObsFSFile append(File file) throws ObsException{
		this.checkInternalClient();
		WriteFileRequest request = new WriteFileRequest(this.getBucketName(), this.getObjectKey(), file);
		return this.innerClient.appendFile(request);
	}
	
	/**
	 * 在文件末尾追加内容
	 * @param input 待上传的数据流
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ObsFSFile append(InputStream input) throws ObsException{
		this.checkInternalClient();
		WriteFileRequest request = new WriteFileRequest(this.getBucketName(), this.getObjectKey(), input);
		return this.innerClient.appendFile(request);
	}
	
	/**
	 * 写文件内容
	 * @param input 待上传的数据流
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public ObsFSFile write(InputStream input) throws ObsException{
		return this.write(input, 0);
	}

	/**
	 * 重命名文件
	 * @param newName 新的文件名
	 * @return 重命名文件响应结果
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public RenameResult rename(String newName) throws ObsException{
		this.checkInternalClient();
		RenameRequest request = new RenameRequest(this.getBucketName(), this.getObjectKey(), newName);
		return this.innerClient.renameFile(request);
	}

	/**
	 * 截断文件
	 * @param newLength 文件截断后的大小
	 * @return 截断文件的响应结果
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public TruncateFileResult truncate(long newLength) throws ObsException{
		this.checkInternalClient();
		TruncateFileRequest request = new TruncateFileRequest(this.getBucketName(), this.getObjectKey(), newLength);
		return this.innerClient.truncateFile(request);
	}
	
	/**
	 * 删除文件
	 * @return 删除文件的响应结果
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	public DropFileResult drop() throws ObsException {
	    this.checkInternalClient();
	    DropFileRequest request = new DropFileRequest(this.getBucketName(), this.getObjectKey(), this.getVersionId());
	    return this.innerClient.dropFile(request);
	}

}

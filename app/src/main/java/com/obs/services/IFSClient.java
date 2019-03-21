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
package com.obs.services;

import java.io.IOException;

import com.obs.services.exception.ObsException;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.fs.ObsFSAttribute;
import com.obs.services.model.fs.GetAttributeRequest;
import com.obs.services.model.fs.GetBucketFSStatusRequest;
import com.obs.services.model.fs.GetBucketFSStatusResult;
import com.obs.services.model.fs.NewBucketRequest;
import com.obs.services.model.fs.NewFileRequest;
import com.obs.services.model.fs.NewFolderRequest;
import com.obs.services.model.fs.ObsFSBucket;
import com.obs.services.model.fs.ObsFSFile;
import com.obs.services.model.fs.ObsFSFolder;
import com.obs.services.model.fs.ReadFileRequest;
import com.obs.services.model.fs.ReadFileResult;
import com.obs.services.model.fs.RenameRequest;
import com.obs.services.model.fs.RenameResult;
import com.obs.services.model.fs.SetBucketFSStatusRequest;
import com.obs.services.model.fs.TruncateFileRequest;
import com.obs.services.model.fs.TruncateFileResult;
import com.obs.services.model.fs.WriteFileRequest;

/**
 * OBS文件网关接口
 */
public interface IFSClient {
	
    /**
     * 关闭OBS客户端，释放连接资源
     * @throws IOException IO异常，当关闭资源失败时抛出该异常
     */
	void close() throws IOException;
	
	/**
	 * 创建桶
	 * @param request 创建桶请求参数
	 * @return 代表支持文件接口的桶
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	ObsFSBucket newBucket(NewBucketRequest request) throws ObsException;
	
	/**
	 * 设置桶的文件网关特性状态
	 * @param request 设置桶的文件网关特性状态的请求参数
	 * @return 公共响应头信息
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	HeaderResponse setBucketFSStatus(SetBucketFSStatusRequest request) throws ObsException; 
	
	/**
	 * 获取桶的文件网关特性状态
	 * @param request 获取桶的文件网关特性状态的请求参数
	 * @return 获取桶的文件网关特性状态的响应结果
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	GetBucketFSStatusResult getBucketFSStatus(GetBucketFSStatusRequest request) throws ObsException;
	
	/**
	 * 创建文件
	 * @param request 创建文件请求参数
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	ObsFSFile newFile(NewFileRequest request) throws ObsException;
	
	/**
	 * 创建文件夹
	 * @param request 创建文件夹请求参数
	 * @return 代表支持文件接口的桶中的文件夹
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	ObsFSFolder newFolder(NewFolderRequest request) throws ObsException;
	
	/**
	 * 获取文件/文件夹属性
	 * @param request 获取文件/文件夹属性请求参数
	 * @return 文件/文件夹属性
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	ObsFSAttribute getAttribute(GetAttributeRequest request) throws ObsException;
	
	
	/**
	 * 读取文件内容
	 * @param request 读取文件内容的请求参数
	 * @return 读取文件内容的响应结果
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	ReadFileResult readFile(ReadFileRequest request) throws ObsException;
	
	/**
	 * 写文件内容
	 * @param request 写文件内容的请求参数
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常 
	 */
	ObsFSFile writeFile(WriteFileRequest request) throws ObsException;
	
	/**
	 * 在文件末尾追加内容
	 * @param request 写文件内容的请求参数
	 * @return 代表支持文件接口的桶中的文件
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常 
	 */
	ObsFSFile appendFile(WriteFileRequest request) throws ObsException;
	
	/**
	 * 重命名文件
	 * @param request 重命名文件请求参数
	 * @return 重命名文件响应结果
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	RenameResult renameFile(RenameRequest request) throws ObsException;
	
	/**
	 * 重命名文件夹
	 * @param request 重命名文件夹请求参数
	 * @return 重命名文件夹响应结果
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	RenameResult renameFolder(RenameRequest request) throws ObsException;
	
	/**
	 * 截断文件
	 * @param request 截断文件请求参数
	 * @return 截断文件响应结果
	 * @throws ObsException OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
	 */
	TruncateFileResult truncateFile(TruncateFileRequest request) throws ObsException;
	
}

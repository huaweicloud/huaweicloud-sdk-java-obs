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

import java.util.concurrent.ConcurrentHashMap;

/*
 * 批量上传对象状态信息
 */
public interface UploadProgressStatus extends TaskProgressStatus {
    
    /**
     * 获取总上传对象大小
     * @return 总上传对象大小 -1表示总大小还没计算完毕
     */
    public long getTotalSize();
    
    /**
     * 获取已上传的字节数
     * @return 已上传的字节数
     */
    public long getTransferredSize();
    
    /**
	 * 获取瞬时速率
	 * @return 瞬时速率
	 */
	public double getInstantaneousSpeed();
	
	/**
	 * 获取平均速率
	 * @return 平均速率
	 */
	public double getAverageSpeed();
	
	/**
	 * 获取正在上传的对象进度信息
	 * @return taskTable 正在上传的对象进度信息
	 */
	public ConcurrentHashMap<String, ProgressStatus> getTaskTable();
	
	/**
	 * 获取指定对象的上传进度信息
	 * @param key 对象名
	 * @return 指定对象的上传进度信息
	 */
	public ProgressStatus getTaskStatus(String key);
}

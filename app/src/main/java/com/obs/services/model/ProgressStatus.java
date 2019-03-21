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

/**
 * 数据传输状态 
 *
 */
public interface ProgressStatus {
	
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
	 * 获取传输进度
	 * @return 传输进度
	 */
	public int getTransferPercentage();
	
	/**
	 * 获取新增的字节数
	 * @return 新增的字节数
	 */
	public long getNewlyTransferredBytes();
	
	/**
	 * 获取已传输的字节数
	 * @return 已传输的字节数
	 */
	public long getTransferredBytes();
	
	/**
	 * 获取待传输的总字节数
	 * @return 待传输的总字节数
	 */
	public long getTotalBytes();
}

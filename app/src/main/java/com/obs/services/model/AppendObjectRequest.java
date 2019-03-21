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
 * 
 * 追加上传请求参数
 * 
 *
 */
public class AppendObjectRequest extends PutObjectRequest{
	
	protected long position;
	
	/**
	 * 获取追加上传位置
	 * @return 追加上传位置
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * 设置追加上传位置
	 * @param position 追加上传位置
	 */
	public void setPosition(long position) {
		this.position = position;
	}
	
}

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
 * 删除对象的响应结果
 */
public class DeleteObjectResult extends HeaderResponse
{
    private boolean deleteMarker;
    
    private String versionId;
    
    
    public DeleteObjectResult(boolean deleteMarker, String versionId) {
		this.deleteMarker = deleteMarker;
		this.versionId = versionId;
	}

	/**
     * 判断多版本对象是否已被删除
     * @return 对象是否被删除标识
     */
	public boolean isDeleteMarker() {
		return deleteMarker;
	}

	/**
	 * 获取被删除对象的版本号
	 * @return 对象的版本号
	 */
	public String getVersionId() {
		return versionId;
	}


	@Override
	public String toString() {
		return "DeleteObjectResult [deleteMarker=" + deleteMarker + ", versionId=" + versionId + "]";
	}
    
}

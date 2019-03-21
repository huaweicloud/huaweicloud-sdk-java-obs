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

import com.obs.services.internal.utils.ServiceUtils;

/**
 * 事件类型
 *
 */
public enum EventTypeEnum {
	
	/**
	 * 所有创建对象事件。
	 */
	OBJECT_CREATED_ALL,

	/**
	 * PUT上传对象事件。
	 */
	OBJECT_CREATED_PUT,

	/**
	 * POST上传对象事件。
	 */
	OBJECT_CREATED_POST,

	/**
	 * 复制对象事件。
	 */
	OBJECT_CREATED_COPY,

	/**
	 * 合并段事件。
	 */
	OBJECT_CREATED_COMPLETE_MULTIPART_UPLOAD,

	/**
	 * 所有删除对象事件。
	 */
	OBJECT_REMOVED_ALL,

	/**
	 * 指定对象版本号删除对象事件。
	 */
	OBJECT_REMOVED_DELETE,

	/**
	 * 多版本开启后，不指定对象版本号删除对象事件。
	 */
	OBJECT_REMOVED_DELETE_MARKER_CREATED;

	public static EventTypeEnum getValueFromCode(String code) {
		if (ServiceUtils.isValid(code)) {
			if (code.indexOf("ObjectCreated:*") >= 0) {
				return OBJECT_CREATED_ALL;
			} else if (code.indexOf("ObjectCreated:Put") >= 0) {
				return OBJECT_CREATED_PUT;
			} else if (code.indexOf("ObjectCreated:Post") >= 0) {
				return OBJECT_CREATED_POST;
			} else if (code.indexOf("ObjectCreated:Copy") >= 0) {
				return OBJECT_CREATED_COPY;
			} else if (code.indexOf("ObjectCreated:CompleteMultipartUpload") >= 0) {
				return OBJECT_CREATED_COMPLETE_MULTIPART_UPLOAD;
			} else if (code.indexOf("ObjectRemoved:*") >= 0) {
				return OBJECT_REMOVED_ALL;
			} else if (code.indexOf("ObjectRemoved:Delete") >= 0) {
				return OBJECT_REMOVED_DELETE;
			} else if (code.indexOf("ObjectRemoved:DeleteMarkerCreated") >= 0) {
				return OBJECT_REMOVED_DELETE_MARKER_CREATED;
			}
		}
		return null;
	}

}

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

import java.util.ArrayList;
import java.util.List;

/**
 * 批量删除对象的响应结果
 */
public class DeleteObjectsResult extends HeaderResponse
{
    private List<DeleteObjectResult> deletedObjectResults;
    
    private List<ErrorResult> errorResults;
    
    public DeleteObjectsResult() {
    	
    }
    
    public DeleteObjectsResult(List<DeleteObjectResult> deletedObjectResults, List<ErrorResult> errorResults) {
		this.deletedObjectResults = deletedObjectResults;
		this.errorResults = errorResults;
	}

	/**
     * 获取删除成功的对象信息列表
     * 
     * @return 删除成功的对象信息列表
     */
    public List<DeleteObjectResult> getDeletedObjectResults()
    {
    	if(this.deletedObjectResults == null) {
    		this.deletedObjectResults = new ArrayList<DeleteObjectResult>();
    	}
        return deletedObjectResults;
    }
    
    /**
     * 获取删除失败的对象信息列表
     * 
     * @return 删除失败的对象信息列表
     */
    public List<ErrorResult> getErrorResults()
    {
    	if(this.errorResults == null) {
    		this.errorResults = new ArrayList<ErrorResult>();
    	}
        return errorResults;
    }
    
    /**
     * 批量删除对象成功后的返回结果
     */
    public class DeleteObjectResult
    {
        private String objectKey;
        
        private String version;
        
        private boolean deleteMarker;
        
        private String deleteMarkerVersion;
        
        
        public DeleteObjectResult(String objectKey, String version, boolean deleteMarker, String deleteMarkerVersion) {
			super();
			this.objectKey = objectKey;
			this.version = version;
			this.deleteMarker = deleteMarker;
			this.deleteMarkerVersion = deleteMarkerVersion;
		}

		/**
         * 获取对象名称
         * 
         * @return 对象名称
         */
        public String getObjectKey()
        {
            return objectKey;
        }
        
        /**
         * 获取对象版本号
         * 
         * @return 对象版本号
         */
        public String getVersion()
        {
            return version;
        }
        
        /**
         * 判断删除的对象是否是删除标记
         * @return 对象是否是删除标记
         */
		public boolean isDeleteMarker() {
			return deleteMarker;
		}

		/**
		 * 获取删除标记的版本号
		 * @return 删除标记的版本号
		 */
		public String getDeleteMarkerVersion() {
			return deleteMarkerVersion;
		}


		@Override
		public String toString() {
			return "DeleteObjectResult [objectKey=" + objectKey + ", version=" + version + ", deleteMarker="
					+ deleteMarker + ", deleteMarkerVersion=" + deleteMarkerVersion + "]";
		}
        
    }
    
    /**
     * 批量删除对象失败的返回结果
     */
    public class ErrorResult
    {
        private String objectKey;
        
        private String version;
        
        private String errorCode;
        
        private String message;
        
        /**
         * 构造函数
         *
         * @param objectKey 删除失败的对象名称
         * @param version 删除失败的对象版本号
         * @param errorCode 删除失败的错误码
         * @param message 删除失败的错误描述
         */
        public ErrorResult(String objectKey, String version, String errorCode, String message)
        {
            this.objectKey = objectKey;
            this.version = version;
            this.errorCode = errorCode;
            this.message = message;
        }
        
        /**
         * 获取删除失败的对象名称
         * 
         * @return 删除失败的对象名称
         */
        public String getObjectKey()
        {
            return objectKey;
        }
        
        /**
         * 获取删除失败的对象版本号
         * 
         * @return 删除失败的对象版本号
         */
        public String getVersion()
        {
            return version;
        }
        
        /**
         * 获取删除失败的错误码
         * 
         * @return 删除失败的错误码
         */
        public String getErrorCode()
        {
            return errorCode;
        }
        
        /**
         * 获取删除失败的错误描述
         * 
         * @return 删除失败的错误描述
         */
        public String getMessage()
        {
            return message;
        }

        @Override
        public String toString()
        {
            return "ErrorResult [objectKey=" + objectKey + ", version=" + version + ", errorCode=" + errorCode + ", message=" + message
                + "]";
        }
        
    }

    @Override
    public String toString()
    {
        return "DeleteObjectsResult [deletedObjectResults=" + deletedObjectResults + ", errorResults=" + errorResults + "]";
    }
    
    
}

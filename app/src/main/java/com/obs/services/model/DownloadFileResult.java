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

import com.obs.services.model.ObjectMetadata;

/**
 * 下载文件的响应结果
 */
public class DownloadFileResult {
	/**
     * 获取对象的属性
     * 
     * @return 对象的属性
     */
    public ObjectMetadata getObjectMetadata() {
        return objectMetadata;
    }

    /**
     * 设置对象的属性
     * 
     * @param objectMetadata 对象的属性
     */
    public void setObjectMetadata(ObjectMetadata objectMetadata) {
        this.objectMetadata = objectMetadata;
    }
    
    private ObjectMetadata objectMetadata;

    @Override
    public String toString()
    {
        return "DownloadFileResult [objectMetadata=" + objectMetadata + "]";
    }
    
}
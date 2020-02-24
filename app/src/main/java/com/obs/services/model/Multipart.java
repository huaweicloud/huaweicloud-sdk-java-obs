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

import java.util.Date;

import com.obs.services.internal.utils.ServiceUtils;

/**
 * 分段上传任务中的段信息
 */
public class Multipart extends HeaderResponse
{
    private Integer partNumber;
    
    private Date lastModified;
    
    private String etag;
    
    private Long size;
    
    public Multipart(){
        
    }
    
    /**
     * 构造函数
     * @param partNumber 分段号
     * @param lastModified 分段最后修改时间
     * @param etag 分段的etag校验值
     * @param size 分段的大小，单位：字节
     */
    public Multipart(Integer partNumber, Date lastModified, String etag, Long size)
    {
        this.partNumber = partNumber;
        this.lastModified = ServiceUtils.cloneDateIgnoreNull(lastModified);
        this.etag = etag;
        this.size = size;
    }

    /**
     * 获取分段号
     * 
     * @return 分段号
     */
    public Integer getPartNumber()
    {
        return partNumber;
    }
    
    
    /**
     * 获取分段的最后修改时间
     * 
     * @return 分段的最后修改时间
     */
    public Date getLastModified()
    {
        return ServiceUtils.cloneDateIgnoreNull(this.lastModified);
    }
    
    
    /**
     * 获取分段的etag校验值
     * 
     * @return 分段的etag校验值
     */
    public String getEtag()
    {
        return etag;
    }
    
    
    /**
     * 获取分段的大小，单位：字节
     * 
     * @return 分段的大小
     */
    public Long getSize()
    {
        return size;
    }
    

    @Override
    public String toString()
    {
        return "Multipart [partNumber=" + partNumber + ", lastModified=" + lastModified + ", etag=" + etag + ", size=" + size + "]";
    }
}

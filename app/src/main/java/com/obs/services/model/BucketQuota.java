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

/**
 * 桶配额信息
 */
public class BucketQuota extends HeaderResponse
{

    private long bucketQuota;
    
    public BucketQuota(){
        
    }
    
    /**
     * 构造函数
     * @param bucketQuota 桶配额大小
     */
    public BucketQuota(long bucketQuota)
    {
        this.bucketQuota = bucketQuota;
    }
    
    /**
     * 获取桶配额大小，单位：字节
     * 
     * @return 桶配额大小
     */
    public long getBucketQuota()
    {
        return bucketQuota;
    }
    
    /**
     * 设置配额大小，单位：字节
     * 
     * @param quota 桶配额大小
     */
    public void setBucketQuota(long quota)
    {
        this.bucketQuota = quota;
    }

    @Override
    public String toString()
    {
        return "BucketQuota [bucketQuota=" + bucketQuota + "]";
    }
    
}

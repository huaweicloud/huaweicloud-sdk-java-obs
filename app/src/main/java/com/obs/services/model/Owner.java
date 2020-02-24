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
 * 桶或对象的所有者
 */
public class Owner
{
    private String displayName;
    
    private String id;
    
    /**
     * 获取所有者的名称
     * 
     * @return 所有者的名称
     */
    @Deprecated
    public String getDisplayName()
    {
        return displayName;
    }
    
    /**
     * 设置所有者的名称
     * 
     * @param displayName 所有者的名称
     */
    @Deprecated
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
    
    /**
     * 获取所有者的DomainId
     * 
     * @return 所有者的DomainId
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * 设置所有者的DomainId
     * 
     * @param id 所有者的DomainId
     */
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "Owner [displayName=" + displayName + ", id=" + id + "]";
    }
    
}

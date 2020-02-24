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
 * 表示ACL中被授权的用户/用户组的接口抽象，{@link AccessControlList}
 */
public interface GranteeInterface
{
    /**
     * 设置被授权用户/用户组的标识
     * 
     * @param id 被授权用户/用户组的标识
     */
    public void setIdentifier(String id);
    
    /**
     * 获取被授权用户/用户组的标识
     * 
     * @return 被授权用户/用户组的标识
     */
    public String getIdentifier();
	
	
}

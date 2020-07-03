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
 * 
 * OBS对象扩展权限
 *
 */
public enum ExtensionObjectPermissionEnum {
    /**
     * 授予domainId下的所有用户读权限，允许读对象、获取对象元数据
     */
    GRANT_READ("grantReadHeader"), /**
                                    * 授予domainId下的所有用户读ACP权限，允许读对象的ACL信息
                                    */
    GRANT_READ_ACP("grantReadAcpHeader"), /**
                                           * 授予domainId下的所有用户写ACP权限，允许修改对象的ACL信息
                                           */
    GRANT_WRITE_ACP("grantWriteAcpHeader"), /**
                                             * 授予domainId下的所有用户完全控制权限，允许读对象、
                                             * 获取对象元数据、获取对象ACL信息、写对象ACL信息
                                             */
    GRANT_FULL_CONTROL("grantFullControlHeader");

    private String code;

    private ExtensionObjectPermissionEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.obs.services.model;

/**
 *
 * OBS桶扩展权限
 *
 */
public enum ExtensionBucketPermissionEnum {
    /**
     * 授予domainId下的所有用户读权限，列举对象、列举多段任务、列举桶多版本、获取桶元数据
     */
    GRANT_READ("grantReadHeader"),
    /**
     * 授予domainId下的所有用户写权限，允许创建、删除、覆盖桶内所有对象，
     * 允许初始化段、上传段、拷贝段、合并段、取消多段上传任务
     */
    GRANT_WRITE("grantWriteHeader"),
    /**
     * 授予domainId下的所有用户读ACP权限，允许读桶的ACL信息
     */
    GRANT_READ_ACP("grantReadAcpHeader"),
    /**
     * 授予domainId下的所有用户写ACP权限，允许修改桶的ACL信息
     */
    GRANT_WRITE_ACP("grantWriteAcpHeader"),
    /**
     * 授予domainId下的所有用户完全控制权限
     */
    GRANT_FULL_CONTROL("grantFullControlHeader"),
    /**
     * 授予domainId下的所有用户读权限，且在默认情况下
     * ，该读权限将传递给桶内所有对象
     */
    GRANT_READ_DELIVERED("grantReadDeliveredHeader"),
    /**
     * 授予domainId下的所有用户完全控制权限，且在默认情况下，
     * 该完全控制权限将传递给桶内所有对象
     */
    GRANT_FULL_CONTROL_DELIVERED("grantFullControlDeliveredHeader");

    private String code;

    private ExtensionBucketPermissionEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
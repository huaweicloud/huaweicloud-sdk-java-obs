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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.obs.services.internal.utils.ServiceUtils;

public abstract class PutObjectBasicRequest extends GenericRequest {

    protected String bucketName;

    protected String objectKey;

    protected Map<ExtensionObjectPermissionEnum, Set<String>> extensionPermissionMap;

    protected AccessControlList acl;

    protected String successRedirectLocation;

    protected SseKmsHeader sseKmsHeader;

    protected SseCHeader sseCHeader;

    /**
     * 获取桶名
     * 
     * @return 桶名
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置桶名
     * 
     * @param bucketName
     *            桶名
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取对象名
     * 
     * @return 对象名
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * 设置对象名
     * 
     * @param objectKey
     *            对象名
     * 
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    /**
     * 获取对象SSE-KMS加密头域信息
     * 
     * @return SSE-KMS加密头域信息
     */
    public SseKmsHeader getSseKmsHeader() {
        return sseKmsHeader;
    }

    /**
     * 设置对象SSE-KMS加密头域信息
     * 
     * @param sseKmsHeader
     *            SSE-KMS加密头域信息
     */
    public void setSseKmsHeader(SseKmsHeader sseKmsHeader) {
        this.sseKmsHeader = sseKmsHeader;
    }

    /**
     * 获取对象SSE-C加密头域信息
     * 
     * @return SSE-C加密头域信息
     */
    public SseCHeader getSseCHeader() {
        return sseCHeader;
    }

    /**
     * 设置对象SSE-C加密头域信息
     * 
     * @param sseCHeader
     *            SSE-C加密头域信息
     */
    public void setSseCHeader(SseCHeader sseCHeader) {
        this.sseCHeader = sseCHeader;
    }

    /**
     * 获取对象的访问权限
     * 
     * @return 对象的访问权限
     */
    public AccessControlList getAcl() {
        return acl;
    }

    /**
     * 设置对象的访问权限
     * 
     * @param acl
     *            对象的访问权限
     */
    public void setAcl(AccessControlList acl) {
        this.acl = acl;
    }

    /**
     * 获取请求操作响应成功后的重定向地址
     * 
     * @return 重定向地址
     */
    public String getSuccessRedirectLocation() {
        return successRedirectLocation;
    }

    /**
     * 设置请求操作响应成功后的重定向地址
     * 
     * @param successRedirectLocation
     *            重定向地址
     */
    public void setSuccessRedirectLocation(String successRedirectLocation) {
        this.successRedirectLocation = successRedirectLocation;
    }

    /**
     * 为用户授予OBS扩展权限
     * 
     * @param domainId
     *            用户的domainId
     * @param extensionPermissionEnum
     *            OBS扩展权限
     */
    public void grantExtensionPermission(String domainId, ExtensionObjectPermissionEnum extensionPermissionEnum) {
        if (extensionPermissionEnum == null || !ServiceUtils.isValid(domainId)) {
            return;
        }
        Set<String> users = getExtensionPermissionMap().get(extensionPermissionEnum);
        if (users == null) {
            users = new HashSet<String>();
            getExtensionPermissionMap().put(extensionPermissionEnum, users);
        }
        users.add(domainId.trim());
    }

    /**
     * 撤回用户的OBS扩展权限
     * 
     * @param domainId
     *            用户的domainId
     * @param extensionPermissionEnum
     *            OBS扩展权限
     */
    public void withdrawExtensionPermission(String domainId, ExtensionObjectPermissionEnum extensionPermissionEnum) {
        if (extensionPermissionEnum == null || !ServiceUtils.isValid(domainId)) {
            return;
        }
        domainId = domainId.trim();
        Set<String> domainIds = getExtensionPermissionMap().get(extensionPermissionEnum);
        if (domainIds != null && domainIds.contains(domainId)) {
            domainIds.remove(domainId);
        }
    }

    /**
     * 撤回用户的所有OBS扩展权限
     * 
     * @param domainId
     *            用户的domainId
     */
    public void withdrawExtensionPermissions(String domainId) {
        if (ServiceUtils.isValid(domainId)) {
            for (Map.Entry<ExtensionObjectPermissionEnum, Set<String>> entry : this.getExtensionPermissionMap()
                    .entrySet()) {
                if (entry.getValue().contains(domainId.trim())) {
                    entry.getValue().remove(domainId);
                }
            }
        }
    }

    public Set<ExtensionObjectPermissionEnum> getAllGrantPermissions() {
        return this.getExtensionPermissionMap().keySet();
    }

    public Set<String> getDomainIdsByGrantPermission(ExtensionObjectPermissionEnum extensionPermissionEnum) {
        Set<String> domainIds = getExtensionPermissionMap().get(extensionPermissionEnum);
        if (domainIds == null) {
            domainIds = new HashSet<String>();
        }
        return domainIds;
    }

    public Set<ExtensionObjectPermissionEnum> getGrantPermissionsByDomainId(String domainId) {
        Set<ExtensionObjectPermissionEnum> grantPermissions = new HashSet<ExtensionObjectPermissionEnum>();
        if (ServiceUtils.isValid(domainId)) {
            domainId = domainId.trim();
            for (Map.Entry<ExtensionObjectPermissionEnum, Set<String>> entry : this.getExtensionPermissionMap()
                    .entrySet()) {
                if (entry.getValue().contains(domainId)) {
                    grantPermissions.add(entry.getKey());
                }
            }
        }
        return grantPermissions;
    }

    public Map<ExtensionObjectPermissionEnum, Set<String>> getExtensionPermissionMap() {
        if (extensionPermissionMap == null) {
            extensionPermissionMap = new HashMap<ExtensionObjectPermissionEnum, Set<String>>();
        }
        return extensionPermissionMap;
    }

    public void setExtensionPermissionMap(Map<ExtensionObjectPermissionEnum, Set<String>> extensionPermissionMap) {
        if (extensionPermissionMap == null) {
            return;
        }
        this.extensionPermissionMap = extensionPermissionMap;
    }
}

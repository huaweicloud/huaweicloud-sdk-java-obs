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
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.utils.ServiceUtils;

/**
 * 批量上传对象的请求参数
 *
 */
public class PutObjectsRequest extends AbstractBulkRequest {
    private String folderPath;

    private String prefix;

    private List<String> filePaths;

    private TaskCallback<PutObjectResult, PutObjectBasicRequest> callback;

    // 分片大小，单位字节，默认5MB
    private long partSize = 1024 * 1024 * 5L;

    // 启用分段上传的文件大小限制，单位字节，默认100MB
    private long bigfileThreshold = 1024 * 1024 * 100L;

    // 分片上传线程数，默认1
    private int taskNum = 1;

    private UploadObjectsProgressListener listener;

    // 详细信息刷间隔，默认为500K
    private long taskProgressInterval = 5 * ObsConstraint.DEFAULT_PROGRESS_INTERVAL;

    // 各对象数据传输监听器回调的阈值，默认为100KB
    private long detailProgressInterval = ObsConstraint.DEFAULT_PROGRESS_INTERVAL;

    private Map<ExtensionObjectPermissionEnum, Set<String>> extensionPermissionMap;

    private AccessControlList acl;

    private String successRedirectLocation;

    private SseKmsHeader sseKmsHeader;

    private SseCHeader sseCHeader;

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param folderPath
     *            上传文件夹的本地路径
     */
    public PutObjectsRequest(String bucketName, String folderPath) {
        super(bucketName);
        this.folderPath = folderPath;
    }

    /**
     * 构造函数
     * 
     * @param bucketName
     *            桶名
     * @param filePaths
     *            批量上传文件的本地路径列表
     */
    public PutObjectsRequest(String bucketName, List<String> filePaths) {
        super(bucketName);
        this.filePaths = filePaths;
    }

    /**
     * 获取上传文件夹的本地路径
     * 
     * @return folderPath 上传文件夹的本地路径
     */
    public String getFolderPath() {
        return folderPath;
    }

    /**
     * 获取批量上传文件的本地路径列表
     * 
     * @return filePaths 批量上传文件的本地路径列表
     */
    public List<String> getFilePaths() {
        return filePaths;
    }

    /**
     * 获取文件上传任务的回调对象
     * 
     * @return callback 回调对象
     */
    public TaskCallback<PutObjectResult, PutObjectBasicRequest> getCallback() {
        return callback;
    }

    /**
     * 设置文件上传任务的回调对象
     * 
     * @param callback
     *            回调对象
     */
    public void setCallback(TaskCallback<PutObjectResult, PutObjectBasicRequest> callback) {
        this.callback = callback;
    }

    /**
     * 获取上传时的分段大小
     * 
     * @return partSize 上传时的分段大小
     */
    public long getPartSize() {
        return partSize;
    }

    /**
     * 设置上传时的分段大小
     * 
     * @param partSize
     *            上传时的分段大小
     */
    public void setPartSize(long partSize) {
        this.partSize = partSize;
    }

    /**
     * 获取启用分段上传的文件临界大小
     * 
     * @return bigfileThreshold 启用分段上传的文件临界大小
     */
    public long getBigfileThreshold() {
        return bigfileThreshold;
    }

    /**
     * 设置启用分段上传的文件临界大小
     * 
     * @param bigfileThreshold
     *            启用分段上传的文件临界大小
     */
    public void setBigfileThreshold(long bigfileThreshold) {
        if (bigfileThreshold < 100 * 1024L) {
            this.bigfileThreshold = 100 * 1024L;
        } else if (bigfileThreshold > 5 * 1024 * 1024 * 1024L) {
            this.bigfileThreshold = 5 * 1024 * 1024 * 1024L;
        } else {
            this.bigfileThreshold = bigfileThreshold;
        }
    }

    /**
     * 获取用于并发执行上传任务的最大线程数
     * 
     * @return 用于并发执行上传任务的最大线程数
     */
    public int getTaskNum() {
        return taskNum;
    }

    /**
     * 设置用于并发执行上传任务的最大线程数
     * 
     * @param taskNum
     *            用于并发执行上传任务的最大线程数
     */
    public void setTaskNum(int taskNum) {
        if (taskNum < 1) {
            this.taskNum = 1;
        } else if (taskNum > 1000) {
            this.taskNum = 1000;
        } else {
            this.taskNum = taskNum;
        }
    }

    /**
     * 获取将文件上传到指定文件夹
     * 
     * @return prefix 文件夹名
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置将文件上传到指定文件夹
     * 
     * @param prefix
     *            文件夹名
     */
    public void setPrefix(String prefix) {
        if (null == prefix) {
            return;
        } else if (prefix.endsWith("/")) {
            this.prefix = prefix;
        } else {
            this.prefix = prefix + "/";
        }

    }

    /**
     * 获取批量任务的进度监听器
     * 
     * @return 进度监听器
     */
    public UploadObjectsProgressListener getUploadObjectsProgressListener() {
        return listener;
    }

    /**
     * 设置批量任务的进度监听器
     * 
     * @param listener
     *            进度监听器
     */
    public void setUploadObjectsProgressListener(UploadObjectsProgressListener listener) {
        this.listener = listener;
    }

    /**
     * 获取详细信息刷间隔
     * 
     * @return taskProgressInterval 详细信息刷间隔
     */
    public long getTaskProgressInterval() {
        return taskProgressInterval;
    }

    /**
     * 设置详细信息刷间隔
     * 
     * @param taskProgressInterval
     *            详细信息刷间隔
     */
    public void setTaskProgressInterval(long taskProgressInterval) {
        if (taskProgressInterval < this.detailProgressInterval) {
            this.taskProgressInterval = this.detailProgressInterval;
        } else {
            this.taskProgressInterval = taskProgressInterval;
        }
    }

    /**
     * 获取数据传输监听器回调的阈值，默认为100KB
     * 
     * @return 数据传输监听器回调的阈值
     */
    public long getDetailProgressInterval() {
        return detailProgressInterval;
    }

    /**
     * 设置数据传输监听器回调的阈值，默认为100KB
     * 
     * @param detailProgressInterval
     *            数据传输监听器回调的阈值
     */
    public void setDetailProgressInterval(long detailProgressInterval) {
        this.detailProgressInterval = detailProgressInterval;
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

    /**
     * 获取所有OBS扩展权限
     * 
     * @return OBS扩展权限列表
     */
    public Set<ExtensionObjectPermissionEnum> getAllGrantPermissions() {
        return this.getExtensionPermissionMap().keySet();
    }

    /**
     * 获取具有指定OBS扩展权限的用户ID集合
     * 
     * @param extensionPermissionEnum
     *            OBS扩展权限
     * @return 用户ID集合
     */
    public Set<String> getDomainIdsByGrantPermission(ExtensionObjectPermissionEnum extensionPermissionEnum) {
        Set<String> domainIds = getExtensionPermissionMap().get(extensionPermissionEnum);
        if (domainIds == null) {
            domainIds = new HashSet<String>();
        }
        return domainIds;
    }

    /**
     * 获取指定用户的OBS扩展权限
     * 
     * @param domainId
     *            用户ID
     * @return OBS扩展权限集合
     */
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

    /**
     * 获取用户与OBS扩展权限关系集合
     * 
     * @return 用户与OBS扩展权限关系集合
     */
    public Map<ExtensionObjectPermissionEnum, Set<String>> getExtensionPermissionMap() {
        if (extensionPermissionMap == null) {
            extensionPermissionMap = new HashMap<ExtensionObjectPermissionEnum, Set<String>>();
        }
        return extensionPermissionMap;
    }

    /**
     * 设置用户与OBS扩展权限关系集合
     * 
     * @param extensionPermissionMap
     *            用户与OBS扩展权限关系集合
     */
    public void setExtensionPermissionMap(Map<ExtensionObjectPermissionEnum, Set<String>> extensionPermissionMap) {
        if (extensionPermissionMap == null) {
            return;
        }
        this.extensionPermissionMap = extensionPermissionMap;
    }

    @Override
    public String toString() {
        return "PutObjectsRequest [folderPath=" + folderPath + ", prefix=" + prefix + ", filePaths=" + filePaths
                + ", callback=" + callback + ", partSize=" + partSize + ", bigfileThreshold=" + bigfileThreshold
                + ", taskNum=" + taskNum + ", listener=" + listener + ", taskProgressInterval=" + taskProgressInterval
                + ", detailProgressInterval=" + detailProgressInterval + ", extensionPermissionMap="
                + extensionPermissionMap + ", acl=" + acl + ", getBucketName()=" + getBucketName()
                + ", isRequesterPays()=" + isRequesterPays() + "]";
    }
}

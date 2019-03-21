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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.obs.services.internal.utils.ServiceUtils;

/**
 * 创建桶的请求参数 
 *
 */
public class CreateBucketRequest {
	
	private String bucketName;

	private String location;
	
	private String epid;

	private StorageClassEnum storageClass;

	private AccessControlList acl;
	
	private Map<ExtensionBucketPermissionEnum, Set<String>> extensionPermissionMap;
	
	private AvailableZoneEnum availableZone;
	
	private Map<String, String> extensionHeaderMap;
	
	public CreateBucketRequest() {
		
	}
	
	/**
	 * 构造函数
	 * @param bucketName 桶名
	 */
	public CreateBucketRequest(String bucketName) {
		this.bucketName = bucketName;
	}
	
	/**
	 * 构造函数
	 * @param bucketName 桶名
	 * @param location 桶的区域位置
	 */
	public CreateBucketRequest(String bucketName, String location) {
		this.bucketName = bucketName;
		this.location = location;
	}
	
	/**
	 * 为用户授予OBS扩展权限
	 * @param domainId 用户的domainId
	 * @param extensionPermissionEnum OBS扩展权限
	 */
	public void grantExtensionPermission(String domainId, ExtensionBucketPermissionEnum extensionPermissionEnum) {
		if(extensionPermissionEnum == null || !ServiceUtils.isValid(domainId)) {
			return;
		}
		Set<String> users = getExtensionPermissionMap().get(extensionPermissionEnum);
		if(users == null) {
			users = new HashSet<String>();
			getExtensionPermissionMap().put(extensionPermissionEnum, users);
		}
		users.add(domainId.trim());
	}
	
	/**
	 * 撤回用户的OBS扩展权限
	 * @param domainId 用户的domainId
	 * @param extensionPermissionEnum OBS扩展权限
	 */
	public void withdrawExtensionPermission(String domainId, ExtensionBucketPermissionEnum extensionPermissionEnum) {
		if(extensionPermissionEnum == null || !ServiceUtils.isValid(domainId)) {
			return;
		}
		domainId = domainId.trim();
		Set<String> domainIds = getExtensionPermissionMap().get(extensionPermissionEnum);
		if(domainIds != null && domainIds.contains(domainId)) {
			domainIds.remove(domainId);
		}
	}
	
	/**
	 * 撤回用户的所有OBS扩展权限
	 * @param domainId 用户的domainId
	 */
	public void withdrawExtensionPermissions(String domainId) {
		if(ServiceUtils.isValid(domainId)) {
			domainId = domainId.trim();
			for(Map.Entry<ExtensionBucketPermissionEnum, Set<String>> entry : this.getExtensionPermissionMap().entrySet()) {
				if(entry.getValue().contains(domainId)) {
					entry.getValue().remove(domainId);
				}
			}
		}
	}
	
	public Set<ExtensionBucketPermissionEnum> getAllGrantPermissions(){
		return this.getExtensionPermissionMap().keySet();
	}
	
	
	public Set<String> getDomainIdsByGrantPermission(ExtensionBucketPermissionEnum extensionPermissionEnum) {
		Set<String> domainIds = getExtensionPermissionMap().get(extensionPermissionEnum);
		if(domainIds == null) {
			domainIds = new HashSet<String>();
		}
		return domainIds;
	}
	
	public Set<ExtensionBucketPermissionEnum> getGrantPermissionsByDomainId(String domainId) {
		Set<ExtensionBucketPermissionEnum> grantPermissions = new HashSet<ExtensionBucketPermissionEnum>();
		if(ServiceUtils.isValid(domainId)) {
			for(Map.Entry<ExtensionBucketPermissionEnum, Set<String>> entry : this.getExtensionPermissionMap().entrySet()) {
				if(entry.getValue().contains(domainId.trim())) {
					grantPermissions.add(entry.getKey());
				}
			}
		}
		return grantPermissions;
	}
	
	
    /**
     * 获取桶名
     * 
     * @return 桶名
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    /**
     * 设置桶名
     * 只能包含小写字母、数字、 "-"、 "."
     * @param bucketName 桶名
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * 获取桶的区域位置
     * @return 桶的区域位置
     */
    public String getLocation()
    {
        return location;
    }
    
    /**
     * 设置桶的区域位置
     * @param location 桶的区域位置，如果使用的终端节点归属于默认区域，可以不携带此参数；如果使用的终端节点归属于其他区域，则必须携带此参数
     */
    public void setLocation(String location)
    {
        this.location = location;
    }
    
    /**
     * 获取桶的企业ID
     * @return 桶的企业ID
     */
    public String getEpid()
    {
        return epid;
    }
    
    /**
     * 设置桶的企业ID
     * @param epid 企业ID
     */
    public void setEpid(String epid)
    {
        this.epid = epid;
    }
    
    public AccessControlList getAcl()
    {
        return acl;
    }
    
    /**
     * 设置桶的访问权限
     * @param acl 桶的访问权限
     */
    public void setAcl(AccessControlList acl)
    {
        this.acl = acl;
    }
    
    /**
     * 获取桶的存储类型
     * @return 桶存储类型
     */
    public StorageClassEnum getBucketStorageClass()
    {
        return storageClass;
    }

    /**
     * 设置桶的存储类型
     * @param storageClass 桶存储类型
     */
    public void setBucketStorageClass(StorageClassEnum storageClass)
    {
        this.storageClass = storageClass;
    }
    
    /**
     * 获取桶的集群类型
     * @return 桶的集群类型
     */
	public AvailableZoneEnum getAvailableZone() {
		return availableZone;
	}

	/**
	 * 设置桶的集群类型
	 * @param availableZone 桶的集群类型
	 */
	public void setAvailableZone(AvailableZoneEnum availableZone) {
		this.availableZone = availableZone;
	}

	Map<ExtensionBucketPermissionEnum, Set<String>> getExtensionPermissionMap() {
		if(extensionPermissionMap == null) {
			extensionPermissionMap = new HashMap<ExtensionBucketPermissionEnum, Set<String>>();
		}
		return extensionPermissionMap;
	}
	
	private void setExtensionHeaderMap(Map<String, String> extensionHeaderMap) {
        this.extensionHeaderMap = extensionHeaderMap;
    }
	
	public Map<String, String> getExtensionHeaderMap() {
        return extensionHeaderMap;
    }

	@Override
	public String toString() {
		return "CreateBucketRequest [bucketName=" + bucketName + ", location=" + location + ", storageClass=" 
		        + storageClass + ", acl=" + acl + ", extensionPermissionMap=" + extensionPermissionMap
				+ ", availableZone=" + availableZone + ",epid=" + epid + "]";
	}

	
}

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
**/
package com.obs.services.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.obs.services.internal.utils.ServiceUtils;

/**
 * Parameters in a bucket creation request 
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
	 * Constructor
	 * @param bucketName Bucket name
	 */
	public CreateBucketRequest(String bucketName) {
		this.bucketName = bucketName;
	}
	
	/**
	 * Constructor
	 * @param bucketName Bucket name
	 * @param location Bucket location
	 */
	public CreateBucketRequest(String bucketName, String location) {
		this.bucketName = bucketName;
		this.location = location;
	}
	
	/**
	 * Grant the OBS extension permission to users.
	 * @param domainId ID of the domain to which the user belongs
	 * @param extensionPermissionEnum OBS extension permission
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
	 * Withdraw the OBS extension permission. 
	 * @param domainId ID of the domain to which the user belongs
	 * @param extensionPermissionEnum OBS extension permission
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
	 * Withdraw all OBS extension permissions. 
	 * @param domainId ID of the domain to which the user belongs
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
     * Obtain the bucket name.
     * 
     * @return Bucket name
     */
    public String getBucketName()
    {
        return bucketName;
    }
    
    /**
     * Set the bucket name.
     * The value can contain only lowercase letters, digits, hyphens (-), and periods (.).
     * @param bucketName Bucket name
     */
    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
    
    /**
     * Obtain the bucket location.
     * @return Bucket location
     */
    public String getLocation()
    {
        return location;
    }
    
    /**
     * Set the bucket location.
     * @param location Bucket location. This parameter is mandatory unless the endpoint belongs to the default region. 
     */
    public void setLocation(String location)
    {
        this.location = location;
    }
    
    /**
     * Obtain the enterprise ID of a bucket.
     * @return Enterprise ID of the bucket
     */
    public String getEpid()
    {
        return epid;
    }
    
    /**
     * Set the enterprise ID of a bucket.
     * @param epid Enterprise ID
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
     * Set the bucket ACL.
     * @param acl Bucket ACL
     */
    public void setAcl(AccessControlList acl)
    {
        this.acl = acl;
    }
    
    /**
     * Obtain the bucket storage class. 
     * @return Bucket storage class
     */
    public StorageClassEnum getBucketStorageClass()
    {
        return storageClass;
    }

    /**
     * Set the bucket storage class. 
     * @param storageClass Bucket storage class
     */
    public void setBucketStorageClass(StorageClassEnum storageClass)
    {
        this.storageClass = storageClass;
    }
    
    /**
     * Obtain the bucket cluster type.
     * @return Bucket cluster type
     */
	public AvailableZoneEnum getAvailableZone() {
		return availableZone;
	}

	/**
	 * Set bucket cluster type.
	 * @param availableZone Bucket cluster type
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

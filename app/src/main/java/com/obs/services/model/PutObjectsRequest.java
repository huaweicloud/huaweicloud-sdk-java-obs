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

public class PutObjectsRequest extends AbstractBulkRequest {
	private String folderPath;
	
	private String prefix;
	
	private List<String> filePaths;

	private TaskCallback<PutObjectResult, PutObjectBasicRequest> callback;

    private long partSize = 1024 * 1024 * 5l;
    
    private long bigfileThreshold = 1024 * 1024 * 100l;
    
    private int taskNum = 1;
	
    private UploadObjectsProgressListener listener;
    
    private long taskProgressInterval = 5 * ObsConstraint.DEFAULT_PROGRESS_INTERVAL;
    
    private long detailProgressInterval = ObsConstraint.DEFAULT_PROGRESS_INTERVAL;
    
    private Map<ExtensionObjectPermissionEnum, Set<String>> extensionPermissionMap;
    
    private AccessControlList acl;
    
    private String successRedirectLocation;
    
    private SseKmsHeader sseKmsHeader;
    
    private SseCHeader sseCHeader;
    
	
	public PutObjectsRequest(String bucketName, String folderPath) {
		super(bucketName);
		this.folderPath = folderPath;
	}
	
	public PutObjectsRequest(String bucketName, List<String> filePaths) {
		super(bucketName);
		this.filePaths = filePaths;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public List<String> getFilePaths() {
		return filePaths;
	}

	public TaskCallback<PutObjectResult, PutObjectBasicRequest> getCallback() {
		return callback;
	}

	public void setCallback(TaskCallback<PutObjectResult, PutObjectBasicRequest> callback) {
		this.callback = callback;
	}

	public long getPartSize() {
		return partSize;
	}

	public void setPartSize(long partSize) {
		this.partSize = partSize;
	}

	public long getBigfileThreshold() {
		return bigfileThreshold;
	}

	public void setBigfileThreshold(long bigfileThreshold) {
		if(bigfileThreshold < 100 * 1024l) {
			bigfileThreshold = 100 * 1024l;
		}else if (bigfileThreshold > 5 * 1024 * 1024 * 1024l) {
        	this.bigfileThreshold = 5 * 1024 * 1024 * 1024l;
		}else {
			this.bigfileThreshold = bigfileThreshold;
		}
	}
	
	public int getTaskNum() {
		return taskNum;
	}

	public void setTaskNum(int taskNum) {
		if (taskNum < 1) {
			this.taskNum = 1;
		} else if (taskNum > 1000) {
			this.taskNum = 1000;
		} else {
			this.taskNum = taskNum;
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		if(null == prefix) {
			return;
		}else if(prefix.endsWith("/")) {
			this.prefix = prefix;
		}else {
			this.prefix = prefix + "/";
		}
		
	}
	
    public UploadObjectsProgressListener getUploadObjectsProgressListener() {
        return listener;
    }

    public void setUploadObjectsProgressListener(UploadObjectsProgressListener listener) {
        this.listener = listener;
    }

	public long getTaskProgressInterval() {
		return taskProgressInterval;
	}

	public void setTaskProgressInterval(long taskProgressInterval) {
		if(taskProgressInterval < this.detailProgressInterval)
		{
			this.taskProgressInterval = this.detailProgressInterval;
		}else {
			this.taskProgressInterval = taskProgressInterval;
		}
	}
	
	public long getDetailProgressInterval() {
		return detailProgressInterval;
	}
	
	public void setDetailProgressInterval(long detailProgressInterval) {
		this.detailProgressInterval = detailProgressInterval;
	}
    
    public SseKmsHeader getSseKmsHeader()
    {
        return sseKmsHeader;
    }
    
    public void setSseKmsHeader(SseKmsHeader sseKmsHeader)
    {
        this.sseKmsHeader = sseKmsHeader;
    }
    
    public SseCHeader getSseCHeader()
    {
        return sseCHeader;
    }
    
    public void setSseCHeader(SseCHeader sseCHeader)
    {
        this.sseCHeader = sseCHeader;
    }
    
    public AccessControlList getAcl()
    {
        return acl;
    }
    
    public void setAcl(AccessControlList acl)
    {
        this.acl = acl;
    }
    
	public String getSuccessRedirectLocation() {
		return successRedirectLocation;
	}

	public void setSuccessRedirectLocation(String successRedirectLocation) {
		this.successRedirectLocation = successRedirectLocation;
	}
    
	public void grantExtensionPermission(String domainId, ExtensionObjectPermissionEnum extensionPermissionEnum) {
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
	
	public void withdrawExtensionPermission(String domainId, ExtensionObjectPermissionEnum extensionPermissionEnum) {
		if(extensionPermissionEnum == null || !ServiceUtils.isValid(domainId)) {
			return;
		}
		domainId = domainId.trim();
		Set<String> domainIds = getExtensionPermissionMap().get(extensionPermissionEnum);
		if(domainIds != null && domainIds.contains(domainId)) {
			domainIds.remove(domainId);
		}
	}
	
	public void withdrawExtensionPermissions(String domainId) {
		if(ServiceUtils.isValid(domainId)) {
			for(Map.Entry<ExtensionObjectPermissionEnum, Set<String>> entry : this.getExtensionPermissionMap().entrySet()) {
				if(entry.getValue().contains(domainId.trim())) {
					entry.getValue().remove(domainId);
				}
			}
		}
	}
	
	public Set<ExtensionObjectPermissionEnum> getAllGrantPermissions(){
		return this.getExtensionPermissionMap().keySet();
	}
	
	public Set<String> getDomainIdsByGrantPermission(ExtensionObjectPermissionEnum extensionPermissionEnum) {
		Set<String> domainIds = getExtensionPermissionMap().get(extensionPermissionEnum);
		if(domainIds == null) {
			domainIds = new HashSet<String>();
		}
		return domainIds;
	}
	
	public Set<ExtensionObjectPermissionEnum> getGrantPermissionsByDomainId(String domainId) {
		Set<ExtensionObjectPermissionEnum> grantPermissions = new HashSet<ExtensionObjectPermissionEnum>();
		if(ServiceUtils.isValid(domainId)) {
			domainId = domainId.trim();
			for(Map.Entry<ExtensionObjectPermissionEnum, Set<String>> entry : this.getExtensionPermissionMap().entrySet()) {
				if(entry.getValue().contains(domainId)) {
					grantPermissions.add(entry.getKey());
				}
			}
		}
		return grantPermissions;
	}
	
    public Map<ExtensionObjectPermissionEnum, Set<String>> getExtensionPermissionMap() {
		if(extensionPermissionMap == null) {
			extensionPermissionMap = new HashMap<ExtensionObjectPermissionEnum, Set<String>>();
		}
		return extensionPermissionMap;
	}

	public void setExtensionPermissionMap(Map<ExtensionObjectPermissionEnum, Set<String>> extensionPermissionMap) {
		if(extensionPermissionMap == null) {
			return;
		}
		this.extensionPermissionMap = extensionPermissionMap;
	}

	@Override
	public String toString() {
	        return "PutObjectsRequest [bucketName=" + bucketName + ", folderPath=" + folderPath + ", listFilePath=" + getFilePathsString() + "]";
	}
	
	private String getFilePathsString() {
		if(null == this.filePaths || this.filePaths.isEmpty()) {
			return "";
		}else {
			final String SEPARATOR = ", ";
			StringBuffer fileBuffer = new StringBuffer();
			for(String filePatg: filePaths) {
				fileBuffer.append(filePatg);
				fileBuffer.append(SEPARATOR);
			}
			return fileBuffer.substring(0,  fileBuffer.length() - SEPARATOR.length());
		}
	}
}

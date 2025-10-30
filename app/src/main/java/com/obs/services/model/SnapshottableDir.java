package com.obs.services.model;

import com.obs.services.internal.utils.ServiceUtils;

import java.util.Date;

public class SnapshottableDir {
    private Date modificationTime;

    private Owner owner;

    private String group;

    private String fileId;

    private String permission;

    private Integer snapshotQuota;

    private String parentFullPath;

    public void setModificationTime(Date modificationTime) { this.modificationTime = ServiceUtils.cloneDateIgnoreNull(modificationTime); }
    public Date getModificationTime() { return modificationTime; }

    public void setOwner(Owner owner) { this.owner = owner; }
    public Owner getOwner() { return owner; }

    public void setGroup(String group) { this.group = group; }
    public String getGroup() { return group; }

    public void setFileId(String fileId) { this.fileId = fileId; }
    public String getFileId() { return fileId; }

    public void setPermission(String permission) { this.permission = permission; }
    public String getPermission() { return permission; }

    public void setSnapshotQuota(Integer snapshotQuota) { this.snapshotQuota = snapshotQuota; }
    public Integer getSnapshotQuota() { return snapshotQuota; }

    public void setParentFullPath(String parentFullPath) { this.parentFullPath = parentFullPath; }
    public String getParentFullPath() { return parentFullPath; }

}

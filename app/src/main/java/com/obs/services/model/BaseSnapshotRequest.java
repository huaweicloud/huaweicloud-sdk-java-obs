package com.obs.services.model;

public abstract class BaseSnapshotRequest extends BaseObjectRequest {

    protected String snapshotName;

    protected BaseSnapshotRequest(String bucketName, String objectKey, String snapshotName) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.snapshotName = snapshotName;

    }

    public String getSnapshotName() {
        return snapshotName;
    }

    protected void validateSnapshotName() {
        if (snapshotName == null || snapshotName.trim().isEmpty()) {
            throw new IllegalArgumentException("Snapshot name cannot be null or empty");
        }
    }
}
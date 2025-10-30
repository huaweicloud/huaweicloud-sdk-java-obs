package com.obs.services.model;

public class DeleteSnapshotRequest extends BaseSnapshotRequest {

    {
        httpMethod = HttpMethodEnum.DELETE;
    }

    public DeleteSnapshotRequest(String bucketName, String objectKey, String snapshotName) {
        super(bucketName, objectKey, snapshotName);
        validateSnapshotName();
    }

}
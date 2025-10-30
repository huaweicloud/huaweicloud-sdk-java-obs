package com.obs.services.model;

public class CreateSnapshotRequest extends BaseSnapshotRequest {

    {
        httpMethod = HttpMethodEnum.POST;
    }

    public CreateSnapshotRequest(String bucketName, String objectKey, String snapshotName) {
        super(bucketName, objectKey, snapshotName);
        validateSnapshotName();
    }


    @Override
    public String toString() {
        return "CreateSnapshotRequest [bucketName=" + bucketName +
                ", objectKey=" + objectKey + ", snapshotName=" + snapshotName + "]";
    }
}
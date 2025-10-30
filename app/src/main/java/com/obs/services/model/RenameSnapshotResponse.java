package com.obs.services.model;

/**
 * Response for renaming snapshot
 * Returns Status, RequestId, and Err (only if there is a mistake)
 */
public class RenameSnapshotResponse extends HeaderResponse {

    public RenameSnapshotResponse() {
    }

    @Override
    public String toString() {
        return "RenameSnapshotResponse [statusCode=" + getStatusCode() + ", requestId=" + getRequestId() + "]";
    }
}

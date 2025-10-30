package com.obs.services.model;

/**
 * Response for creating snapshot
 * Since this only returns Status, Request ID, and Err, we extend HeaderResponse
 */
public class CreateSnapshotResponse extends HeaderResponse {

    public CreateSnapshotResponse() {
    }

    @Override
    public String toString() {
        return "CreateSnapshotResponse [statusCode=" + getStatusCode() + ", requestId=" + getRequestId() + "]";
    }
}
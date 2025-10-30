package com.obs.services.model;

/**
 * Request for renaming snapshot
 */
public class RenameSnapshotRequest extends BaseObjectRequest {

    {
        httpMethod = HttpMethodEnum.PUT;
    }

    private String oldSnapshotName;
    private String newSnapshotName;

    /**
     * Constructor
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object key
     * @param oldSnapshotName
     *            Old snapshot name
     * @param newSnapshotName
     *            New snapshot name
     */
    public RenameSnapshotRequest(String bucketName, String objectKey, String oldSnapshotName, String newSnapshotName) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.oldSnapshotName = oldSnapshotName;
        this.newSnapshotName = newSnapshotName;
    }

    /**
     * Obtain the object key.
     *
     * @return Object key
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * Set the object key.
     *
     * @param objectKey
     *            Object key
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    /**
     * Obtain the old snapshot name.
     *
     * @return Old snapshot name
     */
    public String getOldSnapshotName() {
        return oldSnapshotName;
    }

    /**
     * Set the old snapshot name.
     *
     * @param oldSnapshotName
     *            Old snapshot name
     */
    public void setOldSnapshotName(String oldSnapshotName) {
        this.oldSnapshotName = oldSnapshotName;
    }

    /**
     * Obtain the new snapshot name.
     *
     * @return New snapshot name
     */
    public String getNewSnapshotName() {
        return newSnapshotName;
    }

    /**
     * Set the new snapshot name.
     *
     * @param newSnapshotName
     *            New snapshot name
     */
    public void setNewSnapshotName(String newSnapshotName) {
        this.newSnapshotName = newSnapshotName;
    }

    @Override
    public String toString() {
        return "RenameSnapshotRequest [bucketName=" + bucketName + ", objectKey=" + objectKey
                + ", oldSnapshotName=" + oldSnapshotName + ", newSnapshotName=" + newSnapshotName + "]";
    }
}
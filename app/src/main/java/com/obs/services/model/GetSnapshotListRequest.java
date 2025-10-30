package com.obs.services.model;

import static com.obs.services.internal.ObsConstraint.SNAPSHOT_MAX_KEYS;

/**
 * Request for getting snapshot list
 */
public class GetSnapshotListRequest extends BaseObjectRequest {

    {
        httpMethod = HttpMethodEnum.GET;
    }

    private String marker;
    private int maxKeys;

    /**
     * Constructor
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object key
     */
    public GetSnapshotListRequest(String bucketName, String objectKey) {
        this(bucketName, objectKey, null, SNAPSHOT_MAX_KEYS);
    }

    /**
     * Constructor
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object key
     * @param marker
     *            Start position for listing snapshots
     */
    public GetSnapshotListRequest(String bucketName, String objectKey, String marker) {
        this(bucketName, objectKey, marker, SNAPSHOT_MAX_KEYS);
    }

    /**
     * Constructor
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object key
     * @param maxKeys
     *            Maximum number of snapshots to be returned
     */
    public GetSnapshotListRequest(String bucketName, String objectKey, int maxKeys) {
        this(bucketName, objectKey, null, maxKeys);
    }

    /**
     * Main constructor - all field assignments happen here only
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object key
     * @param marker
     *            Start position for listing snapshots
     * @param maxKeys
     *            Maximum number of snapshots to be returned
     */
    public GetSnapshotListRequest(String bucketName, String objectKey, String marker, int maxKeys) {
        // All field assignments consolidated in main constructor
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.marker = marker;
        this.maxKeys = maxKeys;
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
     * Obtain the start position for listing snapshots.
     *
     * @return Start position marker
     */
    public String getMarker() {
        return marker;
    }

    /**
     * Set the start position for listing snapshots.
     *
     * @param marker
     *            Start position marker
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * Obtain the maximum number of snapshots to be returned.
     *
     * @return Maximum number of snapshots
     */
    public int getMaxKeys() {
        return maxKeys;
    }

    /**
     * Set the maximum number of snapshots to be returned.
     *
     * @param maxKeys
     *            Maximum number of snapshots
     */
    public void setMaxKeys(int maxKeys) {
        this.maxKeys = maxKeys;
    }

    @Override
    public String toString() {
        return "GetSnapshotListRequest [bucketName=" + bucketName + ", objectKey=" + objectKey
                + ", marker=" + marker + ", maxKeys=" + maxKeys + "]";
    }
}
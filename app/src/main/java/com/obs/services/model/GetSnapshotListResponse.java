package com.obs.services.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Response for getting snapshot list
 */
public class GetSnapshotListResponse extends HeaderResponse {

    private String marker;
    private String nextMarker;
    private boolean truncated;
    private int maxKeys;
    private int snapshotCount;
    private List<Snapshot> snapshotList;

    public GetSnapshotListResponse(String marker, String nextMarker, boolean truncated, int maxKeys, int snapshotCount, List<Snapshot> snapshots) {
        this.marker = marker;
        this.nextMarker = nextMarker;
        this.truncated = truncated;
        this.maxKeys = maxKeys;
        this.snapshotCount = snapshotCount;
        this.snapshotList = snapshots != null ? new ArrayList<>(snapshots) : new ArrayList<>();

    }

    /**
     * Obtain the marker for the current listing position.
     *
     * @return Current marker
     */
    public String getMarker() {
        return marker;
    }

    /**
     * Set the marker for the current listing position.
     *
     * @param marker
     *            Current marker
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * Obtain the next marker for pagination.
     *
     * @return Next marker
     */
    public String getNextMarker() {
        return nextMarker;
    }

    /**
     * Set the next marker for pagination.
     *
     * @param nextMarker
     *            Next marker
     */
    public void setNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
    }

    /**
     * Check if the result is truncated.
     *
     * @return true if truncated, false otherwise
     */
    public boolean isTruncated() {
        return truncated;
    }

    /**
     * Set whether the result is truncated.
     *
     * @param truncated
     *            Truncation status
     */
    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    /**
     * Obtain the maximum number of snapshots requested.
     *
     * @return Maximum number of snapshots
     */
    public int getMaxKeys() {
        return maxKeys;
    }

    /**
     * Set the maximum number of snapshots requested.
     *
     * @param maxKeys
     *            Maximum number of snapshots
     */
    public void setMaxKeys(int maxKeys) {
        this.maxKeys = maxKeys;
    }

    /**
     * Obtain the number of snapshots returned.
     *
     * @return Snapshot count
     */
    public int getSnapshotCount() {
        return snapshotCount;
    }

    /**
     * Set the number of snapshots returned.
     *
     * @param snapshotCount
     *            Snapshot count
     */
    public void setSnapshotCount(int snapshotCount) {
        this.snapshotCount = snapshotCount;
    }

    /**
     * Obtain the list of snapshots.
     *
     * @return Snapshot list
     */
    public List<Snapshot> getSnapshotList() {
        return Collections.unmodifiableList(snapshotList);
    }

    /**
     * Set the list of snapshots.
     *
     * @param snapshotList
     *            Snapshot list
     */
    public void setSnapshotList(List<Snapshot> snapshotList) {
        this.snapshotList = snapshotList != null ? new ArrayList<>(snapshotList) : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "GetSnapshotListResponse [marker=" + marker + ", nextMarker=" + nextMarker
                + ", truncated=" + truncated + ", maxKeys=" + maxKeys + ", snapshotCount=" + snapshotCount
                + ", snapshotList=" + snapshotList + "]";
    }
}
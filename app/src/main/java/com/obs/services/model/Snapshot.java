package com.obs.services.model;

import java.util.Date;
import java.util.Objects;

/**
 * Represents a snapshot
 */
public class Snapshot {

    private String snapshotName;
    private Date modifyTime;
    private String snapshotID;

    public Snapshot() {
    }

    public Snapshot(String snapshotName, Date modifyTime, String snapshotID) {
        this.snapshotName = snapshotName;
        this.modifyTime = modifyTime;
        this.snapshotID = snapshotID;
    }

    /**
     * Obtain the snapshot name.
     *
     * @return Snapshot name
     */
    public String getSnapshotName() {
        return snapshotName;
    }

    /**
     * Set the snapshot name.
     *
     * @param snapshotName
     *            Snapshot name
     */
    public void setSnapshotName(String snapshotName) {
        this.snapshotName = snapshotName;
    }

    /**
     * Obtain the modify time.
     *
     * @return Modify time
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * Set the modify time.
     *
     * @param modifyTime
     *            Modify time
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * Obtain the snapshot ID.
     *
     * @return Snapshot ID
     */
    public String getSnapshotID() {
        return snapshotID;
    }

    /**
     * Set the snapshot ID.
     *
     * @param snapshotID
     *            Snapshot ID
     */
    public void setSnapshotID(String snapshotID) {
        this.snapshotID = snapshotID;
    }

    @Override
    public String toString() {
        return "Snapshot [snapshotName=" + snapshotName + ", modifyTime=" + modifyTime
                + ", snapshotID=" + snapshotID + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Snapshot snapshot = (Snapshot) obj;
        return Objects.equals(snapshotID, snapshot.snapshotID);
    }

    @Override
    public int hashCode() {
        int result = snapshotName != null ? snapshotName.hashCode() : 0;
        result = 31 * result + (modifyTime != null ? modifyTime.hashCode() : 0);
        result = 31 * result + (snapshotID != null ? snapshotID.hashCode() : 0);
        return result;
    }
}
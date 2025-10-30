package com.obs.services.model;

import java.util.List;

public class GetSnapshottableDirListResult extends HeaderResponse{
    private String marker;

    private String nextMarker;

    private boolean truncated;

    private int maxKeys;

    private int snapshottableDirCount;

    private List<SnapshottableDir> snapshottableDir;

    public GetSnapshottableDirListResult(String marker, String nextMarker, boolean truncated, int maxKeys,
                             int snapshottableDirCount, List<SnapshottableDir> snapshottableDir) {
        this.marker = marker;
        this.nextMarker = nextMarker;
        this.truncated = truncated;
        this.maxKeys = maxKeys;
        this.snapshottableDirCount = snapshottableDirCount;
        this.snapshottableDir = snapshottableDir;
    }

    public GetSnapshottableDirListResult(String marker, String nextMarker, boolean truncated, int maxKeys) {
        this.marker = marker;
        this.nextMarker = nextMarker;
        this.truncated = truncated;
        this.maxKeys = maxKeys;
        this.snapshottableDirCount = 0;
    }

    public String getMarker() {
        return marker;
    }

    public String getNextMarker() {
        return nextMarker;
    }

    public boolean isTruncated() { return truncated; }

    public int getMaxKeys() { return maxKeys; }

    public int getSnapshottableDirCount() { return snapshottableDirCount; }

    public List<SnapshottableDir> getSnapshottableDir() { return snapshottableDir; }

}

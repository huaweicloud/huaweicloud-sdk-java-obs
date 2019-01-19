package com.obs.services.internal.consensus;

import java.util.concurrent.locks.ReentrantLock;

public class SegmentLock {

	private static final int SEGMENT_NUM = 16;

	private ReentrantLock[] locks;

	public SegmentLock() {
		locks = new ReentrantLock[SEGMENT_NUM];
		for (int i = 0; i < SEGMENT_NUM; i++) {
			locks[i] = new ReentrantLock();
		}
	}

	public static SegmentLock getInstance() {
		return SegmentLockHolder.instance;
	}

	private static class SegmentLockHolder{
		private static SegmentLock instance = new SegmentLock();
	}

	public void lock(final String key) {
		locks[Math.abs(key.hashCode()) % SEGMENT_NUM].lock();
	}

	public void unlock(final String key) {
		locks[Math.abs(key.hashCode()) % SEGMENT_NUM].unlock();
	}

	public void clear() {
		locks = null;
	}
}

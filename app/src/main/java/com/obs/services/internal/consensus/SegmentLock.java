/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
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

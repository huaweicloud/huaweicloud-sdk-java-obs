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
package com.obs.services.internal;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.obs.services.model.ProgressListener;
import com.obs.services.model.ProgressStatus;

public class ConcurrentProgressManager extends ProgressManager {

	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private AtomicBoolean endFlag = new AtomicBoolean(false);
	protected AtomicLong transferredBytes;
	protected AtomicLong newlyTransferredBytes;

	public ConcurrentProgressManager(long totalBytes, long transferredBytes, ProgressListener progressListener,
			long intervalBytes) {
		super(totalBytes, progressListener, intervalBytes);
		this.transferredBytes = transferredBytes < 0 ? new AtomicLong(0) : new AtomicLong(transferredBytes);
		this.newlyTransferredBytes = new AtomicLong(0);
	}

	public void progressStart() {
		if(startFlag.compareAndSet(false, true)) {
			super.progressStart();
		}
	}

	public void progressEnd() {
		if(this.progressListener == null) {
    		return;
    	}
		synchronized (this) {
			Date now = new Date();
			ProgressStatus status = new DefaultProgressStatus(this.newlyTransferredBytes.get(), this.transferredBytes.get(), 
					this.totalBytes, now.getTime() - this.lastCheckpoint.getTime(), now.getTime() - this.startCheckpoint.getTime());
			this.progressListener.progressChanged(status);
		}
	}

	@Override
	protected void doProgressChanged(int bytes) {
		long _transferredBytes = this.transferredBytes.addAndGet(bytes);
		long _newlyTransferredBytes = this.newlyTransferredBytes.addAndGet(bytes);
		Date now = new Date();
		List<BytesUnit> currentInstantaneousBytes = this.createCurrentInstantaneousBytes(bytes, now);
		this.lastInstantaneousBytes = currentInstantaneousBytes;
		if(_newlyTransferredBytes >= this.intervalBytes && (_transferredBytes < this.totalBytes || this.totalBytes == -1)) {
			if(this.newlyTransferredBytes.compareAndSet(_newlyTransferredBytes, -_newlyTransferredBytes)) {
				DefaultProgressStatus status = new DefaultProgressStatus(_newlyTransferredBytes, _transferredBytes, 
						this.totalBytes, now.getTime() - this.lastCheckpoint.getTime(), now.getTime() - this.startCheckpoint.getTime());
				status.setInstantaneousBytes(currentInstantaneousBytes);
				this.progressListener.progressChanged(status);
				this.lastCheckpoint = now;
			}
		}
	}

}

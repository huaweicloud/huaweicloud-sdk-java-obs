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
		if(_newlyTransferredBytes >= this.intervalBytes && _transferredBytes < this.totalBytes) {
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

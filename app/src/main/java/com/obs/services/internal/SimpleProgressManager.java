package com.obs.services.internal;

import java.util.Date;
import java.util.List;

import com.obs.services.model.ProgressListener;
import com.obs.services.model.ProgressStatus;

public class SimpleProgressManager extends ProgressManager{
	
	protected long transferredBytes;
	protected long newlyTransferredBytes;
	
	public SimpleProgressManager(long totalBytes, long transferredBytes, 
			ProgressListener progressListener, long intervalBytes) {
		super(totalBytes, progressListener, intervalBytes);
		this.transferredBytes = transferredBytes < 0 ? 0 : transferredBytes;
	}

	@Override
	protected void doProgressChanged(int bytes) {
		this.transferredBytes += bytes;
		this.newlyTransferredBytes += bytes;
		Date now = new Date();
		List<BytesUnit> currentInstantaneousBytes = this.createCurrentInstantaneousBytes(bytes, now);
		this.lastInstantaneousBytes = currentInstantaneousBytes;
		if(this.newlyTransferredBytes >= this.intervalBytes && this.transferredBytes < this.totalBytes) {
			DefaultProgressStatus status = new DefaultProgressStatus(this.newlyTransferredBytes, this.transferredBytes, 
					this.totalBytes, now.getTime() - this.lastCheckpoint.getTime(), now.getTime() - this.startCheckpoint.getTime());
			status.setInstantaneousBytes(currentInstantaneousBytes);
			this.progressListener.progressChanged(status);
			this.newlyTransferredBytes = 0;
			this.lastCheckpoint = now;
		}
	}

	@Override
	public void progressEnd() {
		if(this.progressListener == null) {
    		return;
    	}
    	Date now = new Date();
    	ProgressStatus status = new DefaultProgressStatus(this.newlyTransferredBytes, this.transferredBytes, 
				this.totalBytes, now.getTime() - this.lastCheckpoint.getTime(), now.getTime() - this.startCheckpoint.getTime());
        this.progressListener.progressChanged(status);
	}

}

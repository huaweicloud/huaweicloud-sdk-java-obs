package com.obs.services.internal;

import java.util.List;

import com.obs.services.internal.ProgressManager.BytesUnit;
import com.obs.services.model.ProgressStatus;

public class DefaultProgressStatus implements ProgressStatus{

    private final long newlyTransferredBytes;
    private final long transferredBytes;
    private final long totalBytes;
    private final long intervalMilliseconds;
    private final long totalMilliseconds;
    private List<BytesUnit> instantaneousBytes;
    
    public DefaultProgressStatus(long newlyTransferredBytes, long transferredBytes, long totalBytes,
            long intervalMilliseconds, long totalMilliseconds) {
    	this.newlyTransferredBytes = newlyTransferredBytes;
    	this.transferredBytes = transferredBytes;
    	this.totalBytes = totalBytes;
    	this.intervalMilliseconds = intervalMilliseconds;
    	this.totalMilliseconds = totalMilliseconds;
    }
	
	@Override
	public double getInstantaneousSpeed() {
		if(this.instantaneousBytes != null) {
			long instantaneousSpeed = 0;
            for (BytesUnit item : this.instantaneousBytes)
            {
                instantaneousSpeed += item.bytes;
            }
            return instantaneousSpeed;
		}
		
		if(this.intervalMilliseconds <= 0) {
			return -1d;
		}
		return this.newlyTransferredBytes * 1000.0d / this.intervalMilliseconds;
	}

	@Override
	public double getAverageSpeed() {
		if(this.totalMilliseconds <= 0) {
			return -1d;
		}
		return this.transferredBytes * 1000.0d / this.totalMilliseconds;
	}

	@Override
	public int getTransferPercentage() {
		if(this.totalBytes < 0) {
			return -1;
		}else if(this.totalBytes == 0) {
			return 100;
		}
		return (int)(this.transferredBytes * 100 / this.totalBytes);
	}

	@Override
	public long getNewlyTransferredBytes() {
		return this.newlyTransferredBytes;
	}

	@Override
	public long getTransferredBytes() {
		return this.transferredBytes;
	}

	@Override
	public long getTotalBytes() {
		return this.totalBytes;
	}

	public void setInstantaneousBytes(List<BytesUnit> instantaneousBytes) {
		this.instantaneousBytes = instantaneousBytes;
	}

}

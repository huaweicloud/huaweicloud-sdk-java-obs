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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.obs.services.model.ProgressListener;

public abstract class ProgressManager {
	
	static class BytesUnit{
		Date dateTime;
		long bytes;
		BytesUnit(Date dateTime, long bytes){
			this.dateTime = dateTime;
			this.bytes = bytes;
		}
	}
	
	protected final long totalBytes;
	protected Date startCheckpoint;
	protected Date lastCheckpoint;
	protected final long intervalBytes;
	protected final ProgressListener progressListener;
	protected volatile List<BytesUnit> lastInstantaneousBytes;
	
    public ProgressManager(long totalBytes, ProgressListener progressListener,
    		long intervalBytes) {
		this.totalBytes = totalBytes;
		this.progressListener = progressListener;
		Date now = new Date();
		this.startCheckpoint = now;
		this.lastCheckpoint = now;
		this.intervalBytes = intervalBytes;
	}
    
    public void progressStart() {
    	Date now = new Date();
		this.startCheckpoint = now;
		this.lastCheckpoint = now;
    }
    
    
    public final void progressChanged(int bytes) {
    	if(this.progressListener == null || bytes <= 0) {
    		return;
    	}
    	this.doProgressChanged(bytes);
    }
    
    protected List<BytesUnit> createCurrentInstantaneousBytes(long bytes, Date now)
    {
        List<BytesUnit> currentInstantaneousBytes = new ArrayList<BytesUnit>();
        List<BytesUnit> _lastInstantaneousBytes = this.lastInstantaneousBytes;
        if (_lastInstantaneousBytes != null)
        {
            for(BytesUnit item : _lastInstantaneousBytes)
            {
                if ((now.getTime() - item.dateTime.getTime()) < 1000)
                {
                    currentInstantaneousBytes.add(item);
                }
            }
        }
        currentInstantaneousBytes.add(new BytesUnit(now, bytes));
        return currentInstantaneousBytes;
    }
    
    public abstract void progressEnd();
    
    protected abstract void doProgressChanged(int bytes);
}

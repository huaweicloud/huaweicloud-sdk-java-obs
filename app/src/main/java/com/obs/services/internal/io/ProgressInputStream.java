package com.obs.services.internal.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.obs.services.internal.ProgressManager;

public class ProgressInputStream extends FilterInputStream {
	
	private boolean readFlag;
	private ProgressManager progressManager;
	private boolean endFlag;
	
	public ProgressInputStream(InputStream in, ProgressManager progressManager) {
		this(in, progressManager, true);
	}
	
	public ProgressInputStream(InputStream in, ProgressManager progressManager, boolean endFlag) {
		super(in);
		this.progressManager = progressManager;
		this.endFlag = endFlag;
	}

	@Override
	public final boolean markSupported() {
		return false;
	}

	protected final void abortIfNeeded() {
		if (Thread.interrupted()) {
			throw new RuntimeException("Abort io due to thread interrupted");
		}
	}

	@Override
	public void mark(int a) {
		abortIfNeeded();
	}

	@Override
	public void reset() throws IOException {
		throw new UnrecoverableIOException("UnRepeatable");
	}

	@Override
	public long skip(long n) throws IOException {
		abortIfNeeded();
		return super.skip(n);
	}

	@Override
	public int available() throws IOException {
		abortIfNeeded();
		return super.available();
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
			abortIfNeeded();
		}finally {
			if(endFlag) {
				this.progressManager.progressEnd();
			}
		}
	}

	@Override
	public int read() throws IOException {
		abortIfNeeded();
		return super.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		abortIfNeeded();
		if (!this.readFlag)
        {
            this.readFlag = true;
            this.progressManager.progressStart();
        }
		int bytes = super.read(b, off, len);
		this.progressManager.progressChanged(bytes);
		return bytes;
	}

}

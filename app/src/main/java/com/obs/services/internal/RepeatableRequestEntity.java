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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.io.MayRepeatableInputStream;
import com.obs.services.internal.utils.Mimetypes;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class RepeatableRequestEntity extends RequestBody implements Closeable {
	private static final ILogger interfaceLog = LoggerBuilder.getLogger("com.obs.services.internal.RestStorageService");

	private String contentType;
	private long contentLength = -1;

	private volatile long bytesWritten = 0;
	private InputStream inputStream;

	private final int writeBufferSize = ObsConstraint.DEFAULT_CHUNK_SIZE;

	public RepeatableRequestEntity(InputStream is, String contentType, long contentLength,
			ObsProperties obsProperties) {
		if (is == null) {
			throw new IllegalArgumentException("InputStream cannot be null");
		}
		this.inputStream = is;
		this.contentLength = contentLength;
		this.contentType = contentType;

		if (!(this.inputStream instanceof MayRepeatableInputStream)) {
			this.inputStream = new MayRepeatableInputStream(is, obsProperties.getIntProperty(ObsConstraint.WRITE_BUFFER_SIZE,
					ObsConstraint.DEFAULT_WRITE_BUFFER_STREAM));
		} 
		this.inputStream.mark(0);
	}

	@Override
	public long contentLength() throws IOException {
		return this.contentLength;
	}

	public boolean isRepeatable() {
		return this.inputStream == null || this.inputStream.markSupported();
	}

	protected void writeToNIO(BufferedSink out) throws IOException {
		ReadableByteChannel in = Channels.newChannel(this.inputStream);
		WritableByteChannel ou = Channels.newChannel(out.outputStream());
		ByteBuffer buffer = ByteBuffer.allocate(this.writeBufferSize);

		int count = 0;
		if (this.contentLength < 0) {
			count = in.read(buffer);
			while (count > 0) {
				buffer.flip();
				while (buffer.hasRemaining()) {
					ou.write(buffer);
				}
				buffer.clear();
				this.bytesWritten += count;
				count = in.read(buffer);
			}
		} else {
			long remaining = this.contentLength;
			while (remaining > 0) {
				count = in.read(buffer);
				if (count <= 0) {
					break;
				}
				buffer.position((int) Math.min(this.writeBufferSize, remaining));
				buffer.flip();
				while (buffer.hasRemaining()) {
					ou.write(buffer);
				}
				buffer.clear();
				this.bytesWritten += count;
				remaining -= count;
			}
		}
	}

	protected void writeToBIO(BufferedSink out) throws IOException {
		byte[] tmp = new byte[this.writeBufferSize];
		int count = 0;
		if (this.contentLength < 0) {
			count = this.inputStream.read(tmp);
			while (count != -1) {
				bytesWritten += count;
				out.write(tmp, 0, count);
				count = this.inputStream.read(tmp);
			}
		} else {
			// consume no more than length
			long remaining = this.contentLength;
			while (remaining > 0) {
				count = inputStream.read(tmp, 0, (int) Math.min(this.writeBufferSize, remaining));
				if (count == -1) {
					break;
				}
				out.write(tmp, 0, count);
				bytesWritten += count;
				remaining -= count;
			}
		}
	}

	@Override
	public MediaType contentType() {
		return MediaType.parse(this.contentType == null ? Mimetypes.MIMETYPE_OCTET_STREAM : this.contentType);
	}

	@Override
	public void writeTo(BufferedSink sink) throws IOException {

		long start = System.currentTimeMillis();
		if (bytesWritten > 0) {
			inputStream.reset();
			bytesWritten = 0;
		}
//		this.writeToNIO(sink);
		this.writeToBIO(sink);
		if (interfaceLog.isInfoEnabled()) {
			interfaceLog.info("write data end, cost " + (System.currentTimeMillis() - start) + " ms");
		}
	}

	@Override
	public void close() throws IOException {
		if (this.inputStream != null) {
			this.inputStream.close();
		}
	}

}

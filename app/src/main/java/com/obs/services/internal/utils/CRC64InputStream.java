package com.obs.services.internal.utils;

import java.io.IOException;
import java.io.InputStream;

public class CRC64InputStream extends InputStream {
    private final InputStream inputStream;

    public CRC64 getCrc64() {
        return crc64;
    }

    private final CRC64 crc64;

    public CRC64InputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        this.crc64 = new CRC64();
    }

    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     *
     * <p> A subclass must provide an implementation of this method.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     * stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        int byteRead = this.inputStream.read();
        if (byteRead != -1) {
            crc64.update(byteRead);
        }
        return byteRead;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = inputStream.read(b, off, len);
        if (bytesRead != -1) {
            crc64.update(b, off, bytesRead);
        }
        return bytesRead;
    }
    @Override
    public long skip(long n) throws IOException {
        byte[] buffer = new byte[512];
        long totalSkippedBytes = 0L;
        long skippedBytes;
        long remainBytesToSkip;
        while (totalSkippedBytes < n) {
            remainBytesToSkip = n - totalSkippedBytes;
            skippedBytes =
                    read(buffer, 0, remainBytesToSkip < buffer.length ? (int) remainBytesToSkip : buffer.length);
            if (skippedBytes == -1) {
                return totalSkippedBytes;
            }
            totalSkippedBytes += skippedBytes;
        }
        return totalSkippedBytes;
    }
    @Override
    public int available() throws IOException {
        return inputStream.available();
    }
    @Override
    public synchronized void mark(int readlimit) {
        inputStream.mark(readlimit);
    }
    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    /**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        inputStream.close();
        super.close();
    }
}

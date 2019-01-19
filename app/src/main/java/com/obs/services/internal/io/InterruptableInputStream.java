package com.obs.services.internal.io;

import java.io.IOException;
import java.io.InputStream;

public class InterruptableInputStream extends InputStream implements InputStreamWrapper {
    private InputStream inputStream = null;

    private boolean interrupted = false;

    public InterruptableInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private void maybeInterruptInputStream() throws IOException {
        if (interrupted) {
            try {
                close();
            } catch (IOException ioe) {
            }
            // Throw an unrecoverable exception to indicate that this exception was deliberate, and
            // should not be recovered from.
            throw new UnrecoverableIOException("Reading from input stream deliberately interrupted");
        }
    }

    @Override
    public int read() throws IOException {
        maybeInterruptInputStream();
        return inputStream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        maybeInterruptInputStream();
        return inputStream.read(b, off, len);
    }

    @Override
    public int available() throws IOException {
        maybeInterruptInputStream();
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    public InputStream getWrappedInputStream() {
        return inputStream;
    }

    public void interrupt() {
        interrupted = true;
    }

}

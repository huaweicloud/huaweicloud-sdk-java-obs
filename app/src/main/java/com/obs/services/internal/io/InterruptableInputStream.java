/**
 * 
 * JetS3t : Java S3 Toolkit
 * Project hosted at http://bitbucket.org/jmurty/jets3t/
 *
 * Copyright 2006-2010 James Murty
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

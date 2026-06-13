/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.obs.services.crypto;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A FileInputStream that deletes the underlying temporary file when closed.
 * <p>
 * Extends FileInputStream (not FilterInputStream) so that
 * {@link com.obs.services.internal.io.MayRepeatableInputStream} can detect
 * {@code instanceof FileInputStream} and use the FileChannel for mark/reset
 * support, which is required for upload retry.
 * <p>
 * Close is idempotent: calling close() multiple times is safe and will only
 * attempt file deletion once.
 */
public class TempFileCleanupInputStream extends FileInputStream {

    private static final ILogger log = LoggerBuilder.getLogger(TempFileCleanupInputStream.class);

    private final File tempFile;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * Constructs a new TempFileCleanupInputStream for the given temporary file.
     *
     * @param tempFile the temporary file to read from; will be deleted on close
     * @throws FileNotFoundException if the file does not exist
     */
    public TempFileCleanupInputStream(File tempFile) throws FileNotFoundException {
        super(tempFile);
        this.tempFile = tempFile;
    }

    @Override
    public void close() throws IOException {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        try {
            super.close();
        } finally {
            if (!tempFile.delete() && log.isWarnEnabled()) {
                log.warn("Failed to delete temporary file: " + tempFile.getAbsolutePath());
            }
        }
    }
}

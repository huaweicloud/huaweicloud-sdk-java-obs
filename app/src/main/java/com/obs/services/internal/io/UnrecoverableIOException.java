package com.obs.services.internal.io;

import java.io.IOException;

public class UnrecoverableIOException extends IOException {
    private static final long serialVersionUID = 1423979730178522822L;

    public UnrecoverableIOException(String message) {
        super(message);
    }

}

package com.io.index.file.reader;

import java.io.File;

public class FileIndexingException
extends RuntimeException {
    private static final long serialVersionUID = -6457380789213673476L;

    public FileIndexingException(File file) {
        super("An exception occurred while indexing " + file.getName());
    }

    public FileIndexingException(File file, Throwable cause) {
        super("An exception occurred while indexing " + file.getName(), cause);
    }
}


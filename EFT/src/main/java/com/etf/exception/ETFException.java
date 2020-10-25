package com.etf.exception;

public class ETFException
extends Exception {
    private static final long serialVersionUID = 1;

    public ETFException() {
    }

    public ETFException(String message) {
        super(message);
    }

    public ETFException(Throwable throwable) {
        super(throwable);
    }

    public ETFException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ETFException(String message, Throwable throwable, boolean a2, boolean b2) {
        super(throwable);
    }
}


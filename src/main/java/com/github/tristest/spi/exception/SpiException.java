package com.github.tristest.spi.exception;

public final class SpiException extends RuntimeException {
    public SpiException(String message) {
        super(message);
    }

    public SpiException(Exception e) {
        super(e);
    }
}


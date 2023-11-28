package com.holland.infrastructure.kit.exception;

public class NotImplementedException extends RuntimeException {
    public NotImplementedException(String functionName) {
        super("Function [" + functionName + "] not implemented yet!");
    }
}

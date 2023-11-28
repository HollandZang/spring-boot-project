package com.holland.infrastructure.kit.exception;

import com.holland.infrastructure.kit.kit.StrKit;

/**
 * 尽量清晰的描述问题出现的原因
 */
public class SimpleException extends RuntimeException {
    public SimpleException() {
    }

    public SimpleException(String message) {
        super(message);
    }

    public SimpleException(String message, Object... args) {
        super(StrKit.builderMsg(message, args));
    }
}

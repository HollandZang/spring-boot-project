package com.holland.infrastructure.kit.exception;

import com.holland.infrastructure.kit.kit.StrKit;

/**
 * 打印异常站信息的业务异常类
 */
public class BizException extends RuntimeException {
    public BizException() {
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Object... args) {
        super(StrKit.builderMsg(message, args));
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

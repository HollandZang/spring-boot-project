package com.holland.infrastructure.kit.web;

import com.holland.infrastructure.kit.kit.StrKit;
import lombok.Getter;

import java.io.Serializable;

/**
 * 响应信息主体
 */
@Getter
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 200;
    public static final int FAIL = 500;

    private final int code;
    private final String msg;
    private final T data;

    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> R<T> ok() {
        return new R<>(SUCCESS, "操作成功", null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(SUCCESS, "操作成功", data);
    }

    public static <T> R<T> ok(T data, String msg, Object... args) {
        return new R<>(SUCCESS, StrKit.builderMsg(msg, args), data);
    }

    public static <T> R<T> fail() {
        return new R<>(FAIL, "操作失败", null);
    }

    public static <T> R<T> fail(String msg, Object... args) {
        return new R<>(FAIL, StrKit.builderMsg(msg, args), null);
    }

    public static <T> R<T> fail(int code, String msg, Object... args) {
        return new R<>(code, StrKit.builderMsg(msg, args), null);
    }
}

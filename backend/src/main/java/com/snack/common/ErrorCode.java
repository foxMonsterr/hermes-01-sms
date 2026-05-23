package com.snack.common;

/**
 * 错误码常量
 */
public final class ErrorCode {

    private ErrorCode() {}

    public static final int SUCCESS       = 200;
    public static final int BAD_REQUEST   = 400;
    public static final int UNAUTHORIZED  = 401;
    public static final int NOT_FOUND     = 404;
    public static final int CONFLICT      = 409;
    public static final int SERVER_ERROR  = 500;
}

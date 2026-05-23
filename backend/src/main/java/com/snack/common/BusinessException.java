package com.snack.common;

/**
 * 业务异常，包含 code + msg，由 GlobalExceptionHandler 统一处理
 */
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public Integer getCode() { return code; }

    // ---------- 静态工厂 ----------

    public static BusinessException badRequest(String msg) {
        return new BusinessException(ErrorCode.BAD_REQUEST, msg);
    }

    public static BusinessException unauthorized(String msg) {
        return new BusinessException(ErrorCode.UNAUTHORIZED, msg);
    }

    public static BusinessException notFound(String msg) {
        return new BusinessException(ErrorCode.NOT_FOUND, msg);
    }

    public static BusinessException conflict(String msg) {
        return new BusinessException(ErrorCode.CONFLICT, msg);
    }
}

package com.snack.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常 → 对应 HTTP 状态码 + Result JSON
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusiness(BusinessException e) {
        HttpStatus status = switch (e.getCode()) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 404 -> HttpStatus.NOT_FOUND;
            case 409 -> HttpStatus.CONFLICT;
            default  -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity
                .status(status)
                .body(Result.error(e.getCode(), e.getMessage()));
    }

    /**
     * 参数校验失败 → 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(ErrorCode.BAD_REQUEST, msg);
    }

    /**
     * 未预期的异常 → 500
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception e) {
        return Result.error(ErrorCode.SERVER_ERROR, "服务器内部错误");
    }
}

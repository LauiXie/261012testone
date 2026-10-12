package com.example.idcard.common;

/**
 * 业务异常，由 {@link GlobalExceptionHandler} 统一转换为标准返回体。
 */
public class BusinessException extends RuntimeException {

    private final int status;
    private final int code;

    public BusinessException(int status, int code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(400, ErrorCode.BAD_REQUEST, message);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(404, ErrorCode.NOT_FOUND, message);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(409, ErrorCode.CONFLICT, message);
    }

    public int getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }
}

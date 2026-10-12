package com.example.idcard.common;

/**
 * 业务错误码约定（同时决定 HTTP 状态码）。
 */
public final class ErrorCode {

    /** 成功 */
    public static final int OK = 0;
    /** 参数校验失败 / 格式错误 -> 400 */
    public static final int BAD_REQUEST = 40000;
    /** 记录不存在 -> 404 */
    public static final int NOT_FOUND = 40400;
    /** 资源冲突（身份证号重复等） -> 409 */
    public static final int CONFLICT = 40900;
    /** 服务器内部错误 -> 500 */
    public static final int INTERNAL_ERROR = 50000;

    private ErrorCode() {
    }
}

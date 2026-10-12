package com.example.idcard.common;

/**
 * 统一返回结构：{@code { code, message, data }}。
 *
 * @param code    0 表示成功，非 0 表示业务错误码
 * @param message 提示信息
 * @param data    业务数据
 */
public record ApiResponse<T>(int code, String message, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(ErrorCode.OK, "success", data);
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(ErrorCode.OK, "success", null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}

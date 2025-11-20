package com.beishuo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> Result<T> success(T data) {
        return new Result<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                data,
                System.currentTimeMillis()
        );
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(
                ResultCode.SUCCESS.getCode(),
                message,
                data,
                System.currentTimeMillis()
        );
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(
                resultCode.getCode(),
                resultCode.getMessage(),
                null,
                System.currentTimeMillis()
        );
    }

    public static <T> Result<T> error(ResultCode resultCode, String message) {
        return new Result<>(
                resultCode.getCode(),
                message,
                null,
                System.currentTimeMillis()
        );
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(
                code,
                message,
                null,
                System.currentTimeMillis()
        );
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(
                ResultCode.INTERNAL_SERVER_ERROR.getCode(),
                message,
                null,
                System.currentTimeMillis()
        );
    }
}


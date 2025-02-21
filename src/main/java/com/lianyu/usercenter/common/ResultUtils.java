package com.lianyu.usercenter.common;

/**
 * 返回工具类
 *
 * @author liany
 */
public class ResultUtils {
    public static <T> BaseResponse<T> success(T data) {

        return new BaseResponse<>(0, data, "ok", "");
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode) {

        return new BaseResponse<>(errorCode.getCode(), null,errorCode.getMessage(), errorCode.getDescription());
    }
}

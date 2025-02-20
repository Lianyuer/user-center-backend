package com.lianyu.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回对象
 *
 * @param <T>
 * @author liany
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -9194771138949734673L;

    private String code;

    private T data;

    private String message;

    public BaseResponse(String code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(String code, T data) {
        this(code, data, "");
    }
}

package com.lianyu.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author liany
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 6771011001536045175L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}

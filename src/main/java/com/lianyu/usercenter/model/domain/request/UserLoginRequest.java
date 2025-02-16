package com.lianyu.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author liany
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -7231824410957765442L;

    private String userAccount;

    private String userPassword;
}

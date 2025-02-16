package com.lianyu.usercenter.controller;

import com.lianyu.usercenter.constant.UserConstant;
import com.lianyu.usercenter.model.domain.User;
import com.lianyu.usercenter.model.domain.request.UserLoginRequest;
import com.lianyu.usercenter.model.domain.request.UserRegisterRequest;
import com.lianyu.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.lianyu.usercenter.constant.UserConstant.ADMIN_ROLE;

/**
 * 用户接口
 *
 * @author lianyu
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册接口
     *
     * @param userRegisterRequest 用户登录请求体，包含用户账号、密码和校验密码
     * @return 注册成功返回用户 id
     * @author lianyu
     */
    @PostMapping("/register")
    public Long register(@RequestBody UserRegisterRequest userRegisterRequest) {
        log.info("用户注册请求体,{}", userRegisterRequest);
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    /**
     * 用户登录接口
     *
     * @param userLoginRequest 用户登录请求体，包含用户账号和密码
     * @param request          HttpServletRequest 对象，用于获取客户端请求信息
     * @return 返回登录成功的用户对象，如果登录失败则返回 null
     * @author lianyu
     */
    @PostMapping("/login")
    public User login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        log.info("用户登录请求体,{}", userLoginRequest);
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        return userService.userLogin(userAccount, userPassword, request);
    }

    /**
     * 查询用户接口
     *
     * @return 用户列表
     * @author lianyu
     */
    @GetMapping("/search")
    public List<User> getUsers(String nickName, HttpServletRequest request) {
        log.info("查询用户nickName参数:{}", nickName);
        // 仅管理员才能进行查询
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }
        return userService.getUserList(nickName);
    }

    /**
     * 删除用户接口
     *
     * @param id 用户 id
     * @return 返回是布尔类型，表示是否删除成功
     * @author lianyu
     */
    @PostMapping("/delete")
    public Boolean deleteUser(@RequestBody long id, HttpServletRequest request) {
        log.info("删除用户id参数:{}", id);
        // 仅管理员才能进行删除
        if (!isAdmin(request)) {
            return false;
        }
        if (id < 0) {
            return null;
        }
        return userService.deleteUser(id);
    }

    /**
     * 判断是否是管理员
     *
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 仅管理员才能进行查询和删除
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        int userRole = user.getUserRole();
        return user != null && userRole == ADMIN_ROLE;
    }
}

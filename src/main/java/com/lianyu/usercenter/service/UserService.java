package com.lianyu.usercenter.service;

import com.lianyu.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author liany
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2025-02-14 22:13:45
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser 原用户对象
     * @return 返回脱敏后的用户对象
     */
    User getSafeUser(User originUser);

    /**
     * 查询用户列表
     *
     * @return 返回用户列表
     */
    List<User> getUserList(String nickName);

    /**
     * 删除用户
     *
     * @return 返回是布尔类型，表示是否删除成功
     * @author lianyu
     */
    Boolean deleteUser(long id);

    /**
     * 获取当前登录用户信息
     *
     * @param id 用户id
     * @return 返回用户对象
     * @author lianyu
     */
    User getCurrentUser(Long id);
}

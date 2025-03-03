package com.lianyu.usercenter;

import com.lianyu.usercenter.mapper.UserMapper;
import com.lianyu.usercenter.model.domain.User;
import com.lianyu.usercenter.model.domain.request.UserRegisterRequest;
import com.lianyu.usercenter.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserCenterApplicationTests {

    @Resource
    private UserMapper userMapper;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Test
    void testAddUser() {
        System.out.println("----- test add user ----");
        User user = new User();
        user.setNickName("我的名字");
        user.setAvatarUrl("https://www.baidu.com");
        user.setGender(0);
        user.setUserAccount("admin");
        user.setUserPassword("123456");
        user.setPhoneNumber("679080");
        user.setEmail("123@163.com");
        int effectedRowCount = userMapper.insert(user);
        Assertions.assertEquals(1, effectedRowCount);
    }

    @Test
    void testRegister() {
        System.out.println("----- test register ----");
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        // 非空
        String userAccount = "admin";
        String userPassword = "";
        String checkPassword = "12345678";
        String planetCode = "1";
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        userRegisterRequest.setCheckPassword(checkPassword);
        userRegisterRequest.setPlanetCode(planetCode);
        System.out.println("1非空" + userServiceImpl.userRegister(userRegisterRequest));
        // 账户不小于4位
        userAccount = "adm";
        userPassword = "12345678";
        checkPassword = "12345678";
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        userRegisterRequest.setCheckPassword(checkPassword);
        userRegisterRequest.setPlanetCode(planetCode);
        System.out.println("2账户长度" + userServiceImpl.userRegister(userRegisterRequest));
        // 密码长度不小于8位
        userAccount = "admin";
        userPassword = "123456";
        checkPassword = "123456";
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        userRegisterRequest.setCheckPassword(checkPassword);
        userRegisterRequest.setPlanetCode(planetCode);
        System.out.println("3密码长度" + userServiceImpl.userRegister(userRegisterRequest));
        // 星球编号长度不大于5位
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "123456";
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        userRegisterRequest.setCheckPassword(checkPassword);
        userRegisterRequest.setPlanetCode(planetCode);
        System.out.println("8星球编号长度" + userServiceImpl.userRegister(userRegisterRequest));
        // 账户不能重复
        userAccount = "myname";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "12345";
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        userRegisterRequest.setCheckPassword(checkPassword);
        userRegisterRequest.setPlanetCode(planetCode);
        System.out.println("6账户重复" + userServiceImpl.userRegister(userRegisterRequest));
        // 星球编号不能重复
        userAccount = "xxxxyyy";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "123";
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        userRegisterRequest.setCheckPassword(checkPassword);
        userRegisterRequest.setPlanetCode(planetCode);
        System.out.println("7星球编号重复" + userServiceImpl.userRegister(userRegisterRequest));
        // 账户不能包含特殊字符
        userAccount = "adm【】";
        userPassword = "12345678";
        checkPassword = "12345678";
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        userRegisterRequest.setCheckPassword(checkPassword);
        userRegisterRequest.setPlanetCode(planetCode);
        System.out.println("4账户特殊字符" + userServiceImpl.userRegister(userRegisterRequest));
        // 密码和校验密码相同
        userAccount = "admin";
        userPassword = "12345678";
        checkPassword = "123458";
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        userRegisterRequest.setCheckPassword(checkPassword);
        userRegisterRequest.setPlanetCode(planetCode);
        System.out.println("5密码校验" + userServiceImpl.userRegister(userRegisterRequest));

        // 插入成功
        userAccount = "admin22";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "123";
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        userRegisterRequest.setCheckPassword(checkPassword);
        userRegisterRequest.setPlanetCode(planetCode);
        long num = userServiceImpl.userRegister(userRegisterRequest);
        if (num > 0) {
            System.out.println("注册成功，id：" + num);
        } else {
            System.out.println("注册失败，错误码：" + num);
        }
    }

}

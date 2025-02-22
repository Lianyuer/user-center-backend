package com.lianyu.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianyu.usercenter.common.ErrorCode;
import com.lianyu.usercenter.exception.BusinessException;
import com.lianyu.usercenter.model.domain.User;
import com.lianyu.usercenter.model.domain.request.UserRegisterRequest;
import com.lianyu.usercenter.service.UserService;
import com.lianyu.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.lianyu.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author liany
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-02-14 22:13:45
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，密码混淆
     */
    private static final String SALT = "lianyu";

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求体
     * @return 注册成功返回用户 id
     * @author lianyu
     */
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 非空校验
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            // return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 账户长度不小于4
        if (userAccount.length() < 4) {
            // return -2;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        // 密码长度不小于8
        if (userPassword.length() < 8) {
            // return -3;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        // 星球编号长度不大于5
        if (planetCode.length() > 5) {
            // return -8;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号长度过长");
        }
        // 账户不包含特殊字符
        String validPattern = "^[a-zA-Z0-9_]{4,}$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            // return -4;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号格式错误");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            // return -5;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码不一致");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        Long userCount = userMapper.selectCount(queryWrapper);
        if (userCount > 0) {
            // return -6;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }
        // 星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planet_code", planetCode);
        userCount = userMapper.selectCount(queryWrapper);
        if (userCount > 0) {
            // return -7;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号已存在");
        }
        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 向数据库插入用户数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        int effectedRowCount = userMapper.insert(user);

        /**
         *  插入失败就没有 id => user.getId() 返回就会是 null，
         *  所以这里判断受影响的行数，如果小于 1，就说明插入失败，
         *  直接返回错误
         */
        if (effectedRowCount < 1) {
            // return -1;
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return user.getId(); // 如果返回 null，由于 service 中返回类型为 long，会导致拆箱错误，所以在上面特殊处理下
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      HttpServletRequest 对象，用于获取客户端请求信息
     * @return 返回登录成功的用户对象，如果登录失败则返回 null
     * @author lisnyu
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 非空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            // return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        // 账号长度和密码长度校验
        if (userAccount.length() < 4) {
            // return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        if (userPassword.length() < 8) {
            // return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        // 账户不包含特殊字符
        String validPattern = "^[a-zA-Z0-9_]{4,}$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            // return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能出现特殊字符");
        }
        // 密码加密，和数据库中密文密码进行对比
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            // return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        // 用户数据脱敏
        User safetyUser = getSafeUser(user);

        // 记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    /**
     * 数据脱敏
     *
     * @param originUser 数据库查询到的数据
     * @return 返回脱敏后的数据对象
     */
    @Override
    public User getSafeUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setNickName(originUser.getNickName());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setPhoneNumber(originUser.getPhoneNumber());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 查询用户
     *
     * @return 返回用户列表
     * @author lianyu
     */
    @Override
    public List<User> getUserList(String nickName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(nickName)) {
            queryWrapper.like("nick_name", nickName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafeUser).collect(Collectors.toList());
    }

    /**
     * 删除用户
     *
     * @return 返回是布尔类型，表示是否删除成功
     * @author lianyu
     */
    @Override
    public Boolean deleteUser(long id) {
        if (id < 0) {
            return false;
        }
        int effectedRowCount = userMapper.deleteById(id);
        return effectedRowCount >= 1;
    }

    /**
     * 获取当前登录用户信息
     *
     * @param id 用户id
     * @return 返回用户对象
     * @author lianyu
     */
    @Override
    public User getCurrentUser(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @Override
    public Integer logout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

}





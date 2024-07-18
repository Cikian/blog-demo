package com.blogdemo.service.impl;

import com.blogdemo.mapper.UserMapper;
import com.blogdemo.pojo.Result;
import com.blogdemo.pojo.User;
import com.blogdemo.service.UserService;
import com.blogdemo.utils.ResultCode;
import com.blogdemo.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Title: UserServiceImpl
 * @Author Cikian
 * @Package com.blogdemo.service.impl
 * @Date 2024/7/18 上午4:02
 * @description: blog-demo:
 */

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserMapper userMapper;

    @Value("${blog.security.token-timeout:3}")
    int tokenTimeout;

    @Override
    public Result addUser(User user) {
        User userByUserName = userMapper.getUserByUserName(user.getUsername());
        if (userByUserName != null) {
            return new Result(ResultCode.FAIL.getCode(), "用户名重复");
        }
        String password = user.getPassword();
        // 加盐
        password = password + "cikian";
        // md5加密
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        user.setPassword(md5Password);

        user.setCreated(LocalDateTime.now());
        user.setLastModified(LocalDateTime.now());
        int i = userMapper.insert(user);
        if (i > 0) {
            return new Result(ResultCode.SUCCESS.getCode(), "注册成功");
        }
        return new Result(ResultCode.FAIL.getCode(), "注册失败，请稍后再试");
    }

    @Override
    public Result login(String username, String password) {
        User userByUserName = userMapper.getUserByUserName(username);
        if (userByUserName == null){
            return new Result(ResultCode.FAIL.getCode(), "用户名不存在");
        }
        // 加盐
        password = password + "cikian";
        // md5加密
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if (!md5Password.equals(userByUserName.getPassword())){
            return new Result(ResultCode.FAIL.getCode(), "密码错误");
        }

        String token = TokenUtils.generateToken(userByUserName, tokenTimeout);
        return new Result(ResultCode.SUCCESS.getCode(), "登录成功", token);
    }

    @Override
    public Map<String, Object> getUserInfo(String userId) {
        return userMapper.getUserById(userId);
    }

    @Override
    public Map<String, Object> getUserInfo() {
        String token = request.getHeader("token");

        String userId = TokenUtils.parseToken(token);
        return userMapper.getUserById(userId);
    }

    @Override
    public String getCurrentUserId() {
        return request.getHeader("userId");
    }
}

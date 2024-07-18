package com.blogdemo.controller;

import com.blogdemo.annotation.NeedLogin;
import com.blogdemo.annotation.NoAuth;
import com.blogdemo.pojo.Result;
import com.blogdemo.pojo.User;
import com.blogdemo.service.UserService;
import com.blogdemo.utils.ResultCode;
import com.blogdemo.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Title: UserController
 * @Author Cikian
 * @Package com.blogdemo.controller
 * @Date 2024/7/18 下午11:57
 * @description: blog-demo:
 */

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    UserService userService;

    @NoAuth
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        return userService.login(user.getUsername(), user.getPassword());
    }

    @NeedLogin
    @GetMapping("/me")
    public Result getUserInfoByUserId() {
        Map<String, Object> userInfo = userService.getUserInfo();
        String msg = userInfo != null ? "获取成功" : "获取失败";
        Integer code = userInfo != null ? ResultCode.SUCCESS.getCode() : ResultCode.FAIL.getCode();
        return new Result(code, msg, userInfo);
    }
}

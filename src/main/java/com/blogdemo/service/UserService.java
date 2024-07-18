package com.blogdemo.service;

import com.blogdemo.pojo.Result;
import com.blogdemo.pojo.User;

import java.util.Map;

/**
 * @Title: UserService
 * @Author Cikian
 * @Package com.blogdemo.service
 * @Date 2024/7/18 上午4:00
 * @description: blog-demo:
 */
public interface UserService {
    Result addUser(User user);
    Result login(String username, String password);
    Map<String, Object> getUserInfo(String userId);
    Map<String, Object> getUserInfo();
    String getCurrentUserId();
}

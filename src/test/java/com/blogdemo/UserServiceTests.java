package com.blogdemo;

import com.blogdemo.pojo.User;
import com.blogdemo.service.UserService;
import com.blogdemo.utils.TokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class UserServiceTests {
    @Autowired
    private UserService userService;

    @Test
    void getUserById() {
        Map<String, Object> userInfo = userService.getUserInfo("1");
    }

    @Test
    void getToken(){
        User user = new User();
        user.setUserId("1");
        String s = TokenUtils.generateToken(user, 3);
    }

    @Test
    void parseToken(){
        String token = "MTUyNjU0NDU=.eyJzdHIiOiIxNTI2NTQ0NXd3dy5jaWtpYW4uY24iLCJ0aW1lc3RhbXAiOiIxNzIxMzE2MTM3NDAxI0=";
        String s = TokenUtils.parseToken(token);
    }

}

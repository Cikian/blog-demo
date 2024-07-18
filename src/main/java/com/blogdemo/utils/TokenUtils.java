package com.blogdemo.utils;

import com.blogdemo.exception.TokenException;
import com.blogdemo.pojo.TokenAuth;
import com.blogdemo.pojo.User;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;

/**
 * @Title: TokenUtils
 * @Author Cikian
 * @Package com.blogdemo.utils
 * @Date 2024/7/18 下午10:28
 * @description: blog-demo: token工具类
 */


public class TokenUtils {
    public static final String TOKEN_AUTH = "www.cikian.cn";

    public static final Long BASE_TIME = (long) (1000 * 60 * 60); // 1小时

    public static String generateToken(User user, int refreshTime) {

        String userId = user.getUserId();
        Long expireTime = System.currentTimeMillis() + (BASE_TIME * refreshTime);
        TokenAuth tokenAuth = new TokenAuth(userId + TOKEN_AUTH, expireTime);
        String authStr = tokenAuth.toString();
        // base64加密
        String tokenHeaderBase64 = Base64.getEncoder().encodeToString(userId.getBytes());
        String tokenAuthBase64 = Base64.getEncoder().encodeToString(authStr.getBytes());
        return tokenHeaderBase64 + "." + tokenAuthBase64;
    }

    public static String parseToken(String token) throws TokenException {
        TokenAuth tokenAuthObj;
        String tokenHeader;
        try {
            String[] tokenArr = token.split("\\.");
            tokenHeader = new String(Base64.getDecoder().decode(tokenArr[0]));
            String tokenAuth = new String(Base64.getDecoder().decode(tokenArr[1]));
            tokenAuthObj = TokenAuth.parse(tokenAuth);
        } catch (Exception e) {
            throw new TokenException("token无效，请重新登录");
        }
        if (!tokenAuthObj.getStr().equals(tokenHeader + TOKEN_AUTH)) {
            throw new TokenException("违法认证，请重新登录");
        }
        if (tokenAuthObj.getTimestamp() < System.currentTimeMillis()) {
            throw new TokenException("token过期，请重新登录");
        }

        return tokenHeader;
    }

    // 获取token剩余时间
    public static Long getRemainingTime(String token) {
        TokenAuth tokenAuthObj;
        String tokenHeader;
        try {
            String[] tokenArr = token.split("\\.");
            tokenHeader = new String(Base64.getDecoder().decode(tokenArr[0]));
            String tokenAuth = new String(Base64.getDecoder().decode(tokenArr[1]));
            tokenAuthObj = TokenAuth.parse(tokenAuth);
        } catch (Exception e) {
            throw new TokenException("token无效，请重新登录");
        }
        if (!tokenAuthObj.getStr().equals(tokenHeader + TOKEN_AUTH)) {
            throw new TokenException("违法认证，请重新登录");
        }
        if (tokenAuthObj.getTimestamp() < System.currentTimeMillis()) {
            throw new TokenException("token过期，请重新登录");
        }

        return tokenAuthObj.getTimestamp() - System.currentTimeMillis();
    }
}

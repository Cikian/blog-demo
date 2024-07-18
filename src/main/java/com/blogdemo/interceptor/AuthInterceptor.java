package com.blogdemo.interceptor;

import com.blogdemo.annotation.NeedAuth;
import com.blogdemo.annotation.NeedLogin;
import com.blogdemo.annotation.NoAuth;
import com.blogdemo.exception.NoLoginException;
import com.blogdemo.pojo.User;
import com.blogdemo.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.MimeHeaders;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Field;

/**
 * @Title: AuthInterceptor
 * @Author Cikian
 * @Package com.blogdemo.interceptor
 * @Date 2024/7/18 下午11:35
 * @description: blog-demo: 身份验证拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final Environment environment;

    public AuthInterceptor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean securityEnabled = environment.getProperty("blog.security.enabled", Boolean.class, true);
        Integer FLUSH_TIME = environment.getProperty("blog.security.token-refresh-per", Integer.class, 20);
        Integer TOKEN_TIMEOUT = environment.getProperty("blog.security.token-timeout", Integer.class, 3);

        if (!securityEnabled) {
            return true;
        }

        if (handler instanceof HandlerMethod handlerMethod) {
            NoAuth noAuth = handlerMethod.getMethodAnnotation(NoAuth.class);
            if (noAuth != null) {
                return true;
            }

            NeedLogin needLogin = handlerMethod.getMethodAnnotation(NeedLogin.class);
            if (needLogin != null) {
                String msg = isLogin(request);
                if (msg.equals("请先登录") || msg.equals("登录已过期，请重新登录")) {
                    throw new NoLoginException(msg);
                } else {
                    setHeader(request, "userId", msg);
                }
            }
        }

        String token = request.getHeader("token");
        if (token != null) {
            Long remainingTime = TokenUtils.getRemainingTime(token);
            // 如果
            if (remainingTime < 1000L * 60 * FLUSH_TIME) {
                String uid = TokenUtils.parseToken(token);
                // 刷新 token
                String newToken = TokenUtils.generateToken(new User(uid), TOKEN_TIMEOUT);
                response.setHeader("new-token", newToken);
            }
        }
        response.setHeader("Access-Control-Allow-Origin", "new-token");


        return true;
    }

    private String isLogin(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null) {
            return "请先登录";
        }
        String userId = TokenUtils.parseToken(token);
        if (userId == null) {
            return "登录已过期，请重新登录";
        }
        return userId;
    }

    private void setHeader(HttpServletRequest request, String key, String value){
        Class<? extends HttpServletRequest> requestClass = request.getClass();
        try {
            Field request1 = requestClass.getDeclaredField("request");
            request1.setAccessible(true);
            Object o = request1.get(request);
            Field coyoteRequest = o.getClass().getDeclaredField("coyoteRequest");
            coyoteRequest.setAccessible(true);
            Object o1 = coyoteRequest.get(o);
            Field headers = o1.getClass().getDeclaredField("headers");
            headers.setAccessible(true);
            MimeHeaders o2 = (MimeHeaders)headers.get(o1);
            o2.removeHeader(key);
            o2.addValue(key).setString(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

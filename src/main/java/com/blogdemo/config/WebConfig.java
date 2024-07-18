package com.blogdemo.config;

import com.blogdemo.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Title: WebConfig
 * @Author Cikian
 * @Package com.blogdemo.config
 * @Date 2024/7/18 下午11:43
 * @description: blog-demo:
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final Environment environment;

    @Autowired
    public WebConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(environment))
                .addPathPatterns("/**");
    }
}

package com.blogdemo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Title: NeedAuth
 * @Author Cikian
 * @Package com.blogdemo.annotation
 * @Date 2024/7/18 上午3:32
 * @description: blog-demo: 需鉴权
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedAuth {
}

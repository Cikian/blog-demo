package com.blogdemo.exception;

/**
 * @Title: NoLoginException
 * @Author Cikian
 * @Package com.blogdemo.exception
 * @Date 2024/7/19 上午1:07
 * @description: blog-demo: 自定义异常：未登录
 */

public class NoLoginException extends  RuntimeException{
    public NoLoginException() {
        super();
    }

    public NoLoginException(String message) {
        super(message);
    }

    public NoLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoLoginException(Throwable cause) {
        super(cause);
    }
}

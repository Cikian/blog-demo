package com.blogdemo.exception;

/**
 * @Title: NoLoginException
 * @Author Cikian
 * @Package com.blogdemo.exception
 * @Date 2024/7/19 上午1:05
 * @description: blog-demo: 自定义异常：token异常
 */

public class TokenException extends  RuntimeException{
    public TokenException() {
        super();
    }

    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenException(Throwable cause) {
        super(cause);
    }
}

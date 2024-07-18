package com.blogdemo.exception;

import com.blogdemo.pojo.Result;
import com.blogdemo.utils.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Title: ExceptionHandler
 * @Author Cikian
 * @Package com.blogdemo.config
 * @Date 2024/7/19 上午1:08
 * @description: blog-demo:
 */

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoLoginException.class)
    public Result handleNoLoginException(NoLoginException e) {
        return new Result(ResultCode.NO_LOGIN.getCode(), e.getMessage());
    }

    @ExceptionHandler(TokenException.class)
    public Result handleTokenException(TokenException e) {
        return new Result(ResultCode.LOGIN_EXPIRED.getCode(), e.getMessage());
    }
}

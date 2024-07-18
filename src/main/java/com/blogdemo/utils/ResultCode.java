package com.blogdemo.utils;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(2000), // 2000: 成功
    FAIL(2001), // 2001: 失败

    NO_LOGIN(4001), // 4001: 未登录
    LOGIN_EXPIRED(4002), // 4002: 登录失效，请重新登录
    NO_AUTH(4003), // 4003: 无权限

    INTERNAL_SERVER_ERROR(5000); // 5000: 服务器内部错误

    private Integer code;

    ResultCode(Integer code) {
        this.code = code;
    }

}

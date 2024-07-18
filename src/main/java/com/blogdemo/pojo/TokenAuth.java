package com.blogdemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

/**
 * @Title: TokenAuth
 * @Author Cikian
 * @Package com.blogdemo.pojo
 * @Date 2024/7/18 下午10:39
 * @description: blog-demo:
 */

@Data
@AllArgsConstructor
public class TokenAuth {
    private String str;
    private Long timestamp;

    public static TokenAuth parse(String tokenAuth) {
        tokenAuth = tokenAuth.substring(1, tokenAuth.length() - 1);
        String[] arr = tokenAuth.split(",");
        String str = arr[0].split(":")[1].replace("\"", "");
        String timestamp = arr[1].split(":")[1].replace("\"", "");
        return new TokenAuth(str, Long.parseLong(timestamp));
    }

    @Override
    public String toString() {
        return "{" +
                "\"str\":\"" + str + "\"," +
                "\"timestamp\":\"" + timestamp + "\"" +
                '}';
    }
}

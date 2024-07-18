package com.blogdemo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @Title: User
 * @Author Cikian
 * @Package com.blogdemo.pojo
 * @Date 2024/7/18 上午3:39
 * @description: blog-demo: 用户类
 */

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    private String userId;
    private String username;
    private String password;
    private String email;
    private LocalDateTime created;
    private LocalDateTime lastModified;

    public User(String userId) {
        this.userId = userId;
    }
}

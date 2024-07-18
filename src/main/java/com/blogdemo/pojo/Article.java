package com.blogdemo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: Article
 * @Author Cikian
 * @Package com.blogdemo.pojo
 * @Date 2024/7/18 上午3:41
 * @description: blog-demo: 文章
 */

@Data
public class Article {
    @TableId(type = IdType.ASSIGN_ID)
    private String postId;
    private String title;
    private String content;
    private String userId;
    private LocalDateTime created;
    private LocalDateTime lastModified;
}

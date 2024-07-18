package com.blogdemo.service;

import com.blogdemo.pojo.Article;
import com.blogdemo.pojo.Result;

/**
 * @Title: ArticleService
 * @Author Cikian
 * @Package com.blogdemo.service
 * @Date 2024/7/19 上午2:53
 * @description: blog-demo:
 */
public interface ArticleService {
    boolean postArticle(Article article);
    Result getArticlesByUserId(String userId, Integer page, Integer size, boolean desc);
    Article getArticleById(String pid);
    Result deleteArticleById(String pid);
    Result updateArticle(Article article);
}

package com.blogdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blogdemo.pojo.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Title: ArticleMapper
 * @Author Cikian
 * @Package com.blogdemo.mapper
 * @Date 2024/7/18 上午3:49
 * @description: blog-demo:
 */

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    Article getArticleById(String postId);
    List<Article> getAllArticlesByUserId(String userId);
    int updateArticle(Article article);
    int deleteArticle(String postId);
}

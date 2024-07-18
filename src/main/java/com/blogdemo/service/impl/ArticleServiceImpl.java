package com.blogdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blogdemo.mapper.ArticleMapper;
import com.blogdemo.mapper.UserMapper;
import com.blogdemo.pojo.Article;
import com.blogdemo.pojo.Result;
import com.blogdemo.pojo.User;
import com.blogdemo.service.ArticleService;
import com.blogdemo.service.UserService;
import com.blogdemo.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Title: ArticleServiceImpl
 * @Author Cikian
 * @Package com.blogdemo.service.impl
 * @Date 2024/7/19 上午2:56
 * @description: blog-demo:
 */

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @Override
    public boolean postArticle(Article article) {
        // 此处可添加一些验证操作，比如文章标题不能为空等，省略

        article.setCreated(LocalDateTime.now());
        article.setLastModified(LocalDateTime.now());

        int i = articleMapper.insert(article);
        return i > 0;
    }

    @Override
    public Result getArticlesByUserId(String userId, Integer page, Integer size, boolean isDesc) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return new Result(ResultCode.FAIL.getCode(), "用户不存在", null);
        }

        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<>();
        qw.eq(Article::getUserId, userId);
        if (isDesc) {
            qw.orderByDesc(Article::getCreated);
        }
        Page<Article> articlePage = articleMapper.selectPage(pageParam, qw);
        return new Result(ResultCode.SUCCESS.getCode(), "获取成功", articlePage);
    }

    @Override
    public Article getArticleById(String pid) {
        return articleMapper.getArticleById(pid);
    }

    @Override
    public Result deleteArticleById(String pid) {
        Article articleById = articleMapper.getArticleById(pid);
        boolean b = userAuth(articleById.getUserId());
        if (!b) {
            return new Result(ResultCode.FAIL.getCode(), "无权限删除");
        }
        int d = articleMapper.deleteArticle(pid);

        String msg = d > 0 ? "删除成功" : "删除失败";
        Integer code = d > 0 ? ResultCode.SUCCESS.getCode() : ResultCode.FAIL.getCode();
        return new Result(code, msg);
    }


    @Override
    public Result updateArticle(Article article) {
        Article articleById = articleMapper.getArticleById(article.getPostId());
        if (articleById == null) {
            return new Result(ResultCode.FAIL.getCode(), "文章不存在");
        }

        boolean b = userAuth(articleById.getUserId());
        if (!b) {
            return new Result(ResultCode.FAIL.getCode(), "无权限修改");
        }

        articleById.setTitle(article.getTitle());
        articleById.setContent(article.getContent());
        articleById.setLastModified(LocalDateTime.now());

        article.setLastModified(LocalDateTime.now());
        int i = articleMapper.updateArticle(articleById);
        String msg = i > 0 ? "修改成功" : "修改失败";
        Integer code = i > 0 ? ResultCode.SUCCESS.getCode() : ResultCode.FAIL.getCode();
        return new Result(code, msg);
    }

    public boolean userAuth(String articleUserId) {
        String uid = userService.getCurrentUserId();
        return uid.equals(articleUserId);
    }
}

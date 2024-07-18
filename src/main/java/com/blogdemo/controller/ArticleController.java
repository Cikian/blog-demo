package com.blogdemo.controller;

import com.blogdemo.annotation.NeedLogin;
import com.blogdemo.pojo.Article;
import com.blogdemo.pojo.Result;
import com.blogdemo.service.ArticleService;
import com.blogdemo.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Title: ArticleController
 * @Author Cikian
 * @Package com.blogdemo.controller
 * @Date 2024/7/19 上午3:35
 * @description: blog-demo:
 */

@RestController
@RequestMapping("/posts")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @NeedLogin
    @PostMapping
    public Result postArticle(@RequestBody Article article) {
        boolean b = articleService.postArticle(article);
        String msg = b ? "发布成功" : "发布失败";
        Integer code = b ? ResultCode.SUCCESS.getCode() : ResultCode.FAIL.getCode();
        return new Result(code, msg);
    }

    @GetMapping
    public Result getArticleByUserId(@RequestParam("uid") String userId,
                                     @RequestParam(value = "page", defaultValue = "1") Integer page,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size,
                                     @RequestParam(value = "isDesc", defaultValue = "true") boolean isDesc) {
        return articleService.getArticlesByUserId(userId, page, size, isDesc);
    }

    @GetMapping("/{id}")
    public Result getArticleById(@PathVariable("id") String pid) {
        Article articleById = articleService.getArticleById(pid);
        String msg = articleById == null ? "文章不存在" : "获取成功";
        Integer code = articleById == null ? ResultCode.FAIL.getCode() : ResultCode.SUCCESS.getCode();
        return new Result(code, msg, articleById);
    }

    @NeedLogin
    @PutMapping("/{id}")
    public Result updateArticle(@PathVariable("id") String pid, @RequestBody Article article) {
        article.setPostId(pid);
        return articleService.updateArticle(article);
    }

    @NeedLogin
    @DeleteMapping("/{id}")
    public Result deleteArticle(@PathVariable("id") String pid) {
        return articleService.deleteArticleById(pid);
    }

}

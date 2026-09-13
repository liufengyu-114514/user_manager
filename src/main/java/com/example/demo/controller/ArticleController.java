package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.Result;
import com.example.demo.entity.Article;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    // 1. 查询所有文章
    @GetMapping
    public Result<List<Article>> listArticles() {
        return Result.success(articleService.list());
    }

    // 2. 查询单个文章
    @GetMapping("/{id}")
    public Result<Article> getArticleById(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null) {
            throw new BusinessException("文章不存在，ID: " + id);
        }
        return Result.success(article);
    }

    // 3. 新增文章
    @PostMapping
    public Result<Article> createArticle(@Valid @RequestBody Article article) {
        articleService.save(article);
        return Result.success("创建成功", article);
    }

    // 4. 更新文章
    @PutMapping("/{id}")
    public Result<Article> updateArticle(@PathVariable Long id, @Valid @RequestBody Article article) {
        if (articleService.getById(id) == null) {
            throw new BusinessException("文章不存在，无法更新");
        }
        article.setId(id);
        articleService.updateById(article);
        return Result.success("更新成功", article);
    }

    // 5. 删除文章
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        if (!articleService.removeById(id)) {
            throw new BusinessException("文章不存在，无法删除");
        }
        return Result.success();
    }

    // 6. 按作者查询
    @GetMapping("/author/{authorId}")
    public Result<List<Article>> listByAuthor(@PathVariable Long authorId) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("author_id", authorId);
        return Result.success(articleService.list(wrapper));
    }

    // 7. 按状态查询
    @GetMapping("/status/{status}")
    public Result<List<Article>> listByStatus(@PathVariable Integer status) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status);
        return Result.success(articleService.list(wrapper));
    }
}

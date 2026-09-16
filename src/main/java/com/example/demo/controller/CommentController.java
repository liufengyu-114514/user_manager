package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Comment;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 1. 发表评论
    @PostMapping
    public Result<Comment> createComment(@Valid @RequestBody Comment comment) {
        commentService.save(comment);
        return Result.success("评论成功", comment);
    }

    // 2. 查询某文章的所有评论
    @GetMapping("/article/{articleId}")
    public Result<List<Comment>> listByArticle(@PathVariable Long articleId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("article_id", articleId).orderByDesc("create_time");
        return Result.success(commentService.list(wrapper));
    }

    // 3. 查询某用户的所有评论
    @GetMapping("/user/{userId}")
    public Result<List<Comment>> listByUser(@PathVariable Long userId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        return Result.success(commentService.list(wrapper));
    }

    // 4. 删除评论
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        if (!commentService.removeById(id)) {
            throw new BusinessException("评论不存在，无法删除");
        }
        return Result.success();
    }

    // 5. 分页查询
    @GetMapping("/page")
    public Result<Page<Comment>> pageComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Comment> pageParam = new Page<>(page, size);
        Page<Comment> result = commentService.page(pageParam);
        return Result.success(result);
    }

    // 6. 统计某文章的评论数
    @GetMapping("/count/{articleId}")
    public Result<Long> countByArticle(@PathVariable Long articleId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("article_id", articleId);
        long count = commentService.count(wrapper);
        return Result.success(count);
    }
}

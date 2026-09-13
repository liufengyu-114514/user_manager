package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.Result;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. 查询所有用户
    @GetMapping
    public Result<List<User>> listUsers() {
        List<User> users = userService.list();
        return Result.success(users);
    }

    // 2. 查询单个用户
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在，ID: " + id);
        }
        return Result.success(user);
    }

    // 3. 新增用户
    @PostMapping
    public Result<User> createUser(@Valid @RequestBody User user) {
        userService.save(user);
        return Result.success("创建成功", user);
    }

    // 4. 更新用户
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        if (userService.getById(id) == null) {
            throw new BusinessException("用户不存在，无法更新");
        }
        user.setId(id);
        userService.updateById(user);
        return Result.success("更新成功", user);
    }

    // 5. 删除用户
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        if (!userService.removeById(id)) {
            throw new BusinessException("用户不存在，无法删除");
        }
        return Result.success();
    }

    // 6. 模糊搜索（用 QueryWrapper）
    @GetMapping("/search")
    public Result<List<User>> searchUsers(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return Result.success(userService.list());
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("username", keyword);
        return Result.success(userService.list(wrapper));
    }
}

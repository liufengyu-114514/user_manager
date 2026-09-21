package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.Result;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.UserCreateDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import com.example.demo.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    // 1. 查询所有用户
    @GetMapping
    public Result<List<UserVO>> listUsers() {
        List<User> users = userService.list();
        List<UserVO> voList = users.stream()
                .map(this::convertToVO)
                .toList();
        return Result.success(voList);
    }

    // 2. 查询单个用户
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在，ID: " + id);
        }
        return Result.success(convertToVO(user));
    }

    // 3. 新增用户
    @PostMapping
    public Result<UserVO> createUser(@Valid @RequestBody UserCreateDTO dto) {
        // 1. 检查用户名是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", dto.getUsername());
        if (userService.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 2. DTO → Entity
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));  // 加密
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());

        // 3. 保存
        userService.save(user);

        // 4. Entity → VO
        UserVO vo = convertToVO(user);
        return Result.success("创建成功", vo);
    }

    /*
     Entity → VO 转换
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(maskEmail(user.getEmail()));  // 脱敏
        vo.setAge(user.getAge());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    /*
     邮箱脱敏：zhangsan@example.com → z*******n@example.com
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);
        if (prefix.length() <= 2) {
            return "*".repeat(prefix.length()) + suffix;
        }
        return prefix.charAt(0) + "*".repeat(prefix.length() - 2) + prefix.charAt(prefix.length() - 1) + suffix;
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


     //7. 登录接口（JWT Token）
     @PostMapping("/login")
     public Result<String> login(@Valid @RequestBody LoginDTO dto) {
         // 1. 查用户
         QueryWrapper<User> wrapper = new QueryWrapper<>();
         wrapper.eq("username", dto.getUsername());
         User user = userService.getOne(wrapper);

         // 2. 校验
         if (user == null) {
             throw new BusinessException("用户名或密码错误");
         }
         if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
             throw new BusinessException("用户名或密码错误");
         }

         // 3. 生成 Token
         String token = jwtUtil.generateToken(user.getId(), user.getUsername());
         return Result.success("登录成功", token);
     }
}

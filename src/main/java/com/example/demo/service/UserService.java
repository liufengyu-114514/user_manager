package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.User;

public interface UserService extends IService<User> {
    // 继承 IService 后，自动拥有常用业务方法
    // 也可以自定义方法
}

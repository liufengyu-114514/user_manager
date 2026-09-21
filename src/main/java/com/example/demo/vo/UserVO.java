package com.example.demo.vo;

import java.time.LocalDateTime;

public class UserVO {
    private Long id;
    private String username;
    private String email;      // 可以脱敏
    private Integer age;
    private LocalDateTime createTime;

    // 注意：没有 password 字段

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
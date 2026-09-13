package com.example.demo.common;

public class Result<T> {

    private Integer code;      // 状态码：200成功，其他失败
    private String message;    // 提示信息
    private T data;            // 返回数据（泛型，可以是任何类型）

    // 无参构造
    public Result() {}

    // 全参构造
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ========== 静态工厂方法（推荐使用） ==========

    // 成功（带数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "成功", data);
    }

    // 成功（不带数据）
    public static <T> Result<T> success() {
        return new Result<>(200, "成功", null);
    }

    // 成功（自定义消息）
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // 失败（默认400）
    public static <T> Result<T> error(String message) {
        return new Result<>(400, message, null);
    }

    // 失败（自定义code）
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // ========== Getter / Setter ==========
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
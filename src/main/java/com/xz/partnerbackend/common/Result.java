package com.xz.partnerbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 阿庆
 * @Date: 2024/3/7 16:35
 * 后端统一返回结果
 */
@Data
public class Result<T> implements Serializable {
    private Integer code; //编码：200成功，枚举其他失败数字为失败
    private String msg; //错误信息
    private T data; //数据

    public Result() {
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 200;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 200;
        return result;
    }

    public static <T> Result<T> success(T object,String msg) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 200;
        result.msg = msg;
        return result;
    }


    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<T>();
        result.code = 200;
        result.msg = msg;
        return result;
    }


    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }

    public static <T> Result<T> error(ErrorCode errorCode,String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = errorCode.getCode();
        return result;
    }
}

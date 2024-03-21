package com.xz.partnerbackend.constant;

/**
 * @Author: 阿庆
 * @Date: 2024/3/20 16:35
 * redis 过期，前缀常量
 */

public class RedisConstant {

    public static final String LOGIN = "user:token:";

    public static final Long LOGIN_EXPIRE = 30L;
}

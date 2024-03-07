package com.xz.partnerbackend.context;

/**
 * @Author: 阿庆
 * @Date: 2024/3/7 17:33
 * 记录登录用户id
 */

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
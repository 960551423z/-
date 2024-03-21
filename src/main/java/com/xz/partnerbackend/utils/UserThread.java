package com.xz.partnerbackend.utils;

import com.xz.partnerbackend.model.vo.UserLoginVO;

/**
 * @Author: 阿庆
 * @Date: 2024/3/20 15:24
 * 将线程保存到 ThreadLocal中
 */

public class UserThread {
    private static final ThreadLocal<UserLoginVO> user = new ThreadLocal<>();

    public static void saveUser(UserLoginVO userLoginVO) {
        user.set(userLoginVO);
    }

    public static UserLoginVO getUser() {
        return user.get();
    }

    public static void removeUser() {
        user.remove();
    }

}

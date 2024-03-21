package com.xz.partnerbackend.utils;

import com.xz.partnerbackend.constant.SessionConstant;
import com.xz.partnerbackend.constant.UserMsgFailedConstant;
import com.xz.partnerbackend.exception.BusinessException;
import com.xz.partnerbackend.model.vo.UserLoginVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Author: 阿庆
 * @Date: 2024/3/15 10:43
 * Session 相关工具类
 */

public class SessionUtils {

    /**
     * 判断是否是管理员
     * @param request
     * @return
     */
    public static boolean isAdmin(HttpServletRequest request) {
        Object userVoObj = request.getSession().getAttribute(SessionConstant.USER_LOGIN_STATE);
        UserLoginVO userLoginVO = (UserLoginVO) userVoObj;
        return userLoginVO != null && Objects.equals(userLoginVO.getUserRose(), SessionConstant.ADMIN);
    }


    /**
     * 返回当前登录用户信息
     * @param request
     * @return
     */
    public static UserLoginVO AdminAndInfo(HttpServletRequest request) {
        Object userVoObj = request.getSession().getAttribute(SessionConstant.USER_LOGIN_STATE);
        UserLoginVO userLoginVO = (UserLoginVO) userVoObj;
        if (userVoObj == null) {
            throw new BusinessException(UserMsgFailedConstant.NO_LOGIN);
        }

        return userLoginVO;
    }

    /**
     * 获取当前用户登录信息
     * @param request
     * @return
     */
    public static UserLoginVO getLogin(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        // 鉴权
        Object userVoObj = request.getSession().getAttribute(SessionConstant.USER_LOGIN_STATE);
        if (userVoObj == null) {
            throw new BusinessException(UserMsgFailedConstant.NO_LOGIN);
        }

        return (UserLoginVO) userVoObj;
    }

}

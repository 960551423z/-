package com.xz.partnerbackend.interceptor;

import com.xz.partnerbackend.constant.SessionConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: 阿庆
 * @Date: 2024/3/14 10:54
 * Session 拦截器
 */

@Component
@Slf4j
public class SessionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        // 从响应中 获取session
        Object session = request.getSession().getAttribute(SessionConstant.USER_LOGIN_STATE);

        if (session != null) {
            return true;
        } else  {
            response.setStatus(401);
            return false;
        }
    }

}

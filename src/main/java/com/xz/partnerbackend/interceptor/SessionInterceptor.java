package com.xz.partnerbackend.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.xz.partnerbackend.constant.SessionConstant;
import com.xz.partnerbackend.model.vo.UserLoginVO;
import com.xz.partnerbackend.utils.UserThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.xz.partnerbackend.constant.RedisConstant.LOGIN;
import static com.xz.partnerbackend.constant.RedisConstant.LOGIN_EXPIRE;

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

        // 1. 判断是否需要拦截
        if(UserThread.getUser() == null) {
            response.setStatus(401);
            // 拦截
            return false;
        }

        // 2. 有用户放行
        return true;
    }


}

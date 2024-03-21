package com.xz.partnerbackend.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
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
 * 刷新 token 的拦截请求
 */

@Component
@Slf4j
public class RefreshInterceptor implements HandlerInterceptor {


    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }


        // 1.从请求头中获取token
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }
        // 2. 基于 token 从 redis 中获取用户
        Map<Object, Object> map = redisTemplate.opsForHash().entries(LOGIN + token);

        // 2.1 进行判读，有可能这个登录信息过期了，所以查询出来的map为空
        if (map.isEmpty()) {
            return true;
        }

        // 3. 查询到的 用户时 hash 对象，转成 UserLoginVO 对象
        UserLoginVO userLoginVO = BeanUtil.fillBeanWithMap(map, UserLoginVO.builder().build(), false);

        // 4. 存储到 ThreadLocal 中
        UserThread.saveUser(userLoginVO);

        // 5. 刷新 token 有效期
        redisTemplate.expire(LOGIN + token,LOGIN_EXPIRE, TimeUnit.MINUTES);

        // 6. 放行
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 避免资源泄露
        UserThread.removeUser();
    }
}

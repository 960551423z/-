package com.xz.partnerbackend.config;


import com.xz.partnerbackend.interceptor.RefreshInterceptor;
import com.xz.partnerbackend.interceptor.SessionInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {


    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Autowired
    private RefreshInterceptor refreshInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        // 登录 拦截器
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/login").order(1);
//                .excludePathPatterns("/user/current");

        // token 刷新拦截器
        registry.addInterceptor(refreshInterceptor).addPathPatterns("/**").order(0);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowCredentials(true);
    }
}
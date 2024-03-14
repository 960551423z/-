package com.xz.partnerbackend.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * Swagger 配置
 */
@Configuration
@EnableKnife4j
@EnableSwagger2WebMvc
@Slf4j
@Profile({"dev", "test"})
public class SwaggerConfig implements WebMvcConfigurer , CommandLineRunner {

    @Value("${server.port}")
    private Integer port;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(ApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xz.partnerbackend.controller"))
                .paths(PathSelectors.any())
                .build();
    }


    private ApiInfo ApiInfo() {
        return new ApiInfoBuilder()
                .title("API 接口文档")
                .version("1.0")
                .build();
    }

    /**
     * 拦截器过滤
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("設置静态资源管理映射");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 项目启动打印 swagger 路径
     */
    @Override
    public void run(String... args) throws Exception {
        final InetAddress localHost = Inet4Address.getLocalHost();
        String path = "http://" + localHost.getHostAddress() + ":" + port + "/doc.html";
        log.info(path);
    }
}
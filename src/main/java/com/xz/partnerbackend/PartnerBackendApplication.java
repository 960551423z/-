package com.xz.partnerbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.xz.partnerbackend.mapper"})
public class PartnerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartnerBackendApplication.class, args);
    }

}

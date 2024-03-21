package com.hamll.search;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.hmall.item.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.hmall.common.client")
public class SearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }
}
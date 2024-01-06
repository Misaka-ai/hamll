package com.hmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 搜索应用程序
 *
 * @author liudo
 * @date 2023/08/26
 */
@EnableFeignClients(basePackages = "com.hmall.client")
@SpringBootApplication
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }
}
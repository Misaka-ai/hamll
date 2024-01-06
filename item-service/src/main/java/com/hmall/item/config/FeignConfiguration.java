package com.hmall.item.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/27 20:32:52
 */
@Configuration
public class FeignConfiguration {

    @Bean
    public Logger.Level loggerLevel() {
        return Logger.Level.FULL;
    }
}

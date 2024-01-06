package com.hmall.item.config;

import com.hmall.item.interceptor.AuthorizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author liudo
 * @version 1.0
 * @project hmall
 * @description MVC配置类
 * @date 2023/8/25 19:08:21
 */
@Configuration
@RequiredArgsConstructor
public class MVCConfiguration extends WebMvcConfigurationSupport {

    private final AuthorizationInterceptor authorizationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/**");
    }
}

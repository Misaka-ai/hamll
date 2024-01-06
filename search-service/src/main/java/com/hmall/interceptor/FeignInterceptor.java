package com.hmall.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * Feign拦截器
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/25 19:13:53
 */
@Component
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("authorization", "2");
    }
}

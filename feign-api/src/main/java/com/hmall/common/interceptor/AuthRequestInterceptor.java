package com.hmall.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class AuthRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 添加请求头authorization=2
        template.header("authorization", "2");
    }
}

package com.hmall.order.config;

import com.hmall.order.interceptor.AuthorizationInterceptor;
import com.hmall.order.json.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * MVC配置类
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/26 22:47:00
 */
@Configuration
public class MvcConfiguration extends WebMvcConfigurationSupport {

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizationInterceptor())
                .addPathPatterns("/**");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建一个消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 需要为消息转换器设置一个对象转换器，对象转换器可以将Java对象序列化为json数据
        converter.setObjectMapper(new JacksonObjectMapper());
        // 将自己的消息转化器加入容器中
        converters.add(0, converter);
        super.extendMessageConverters(converters);
    }
}

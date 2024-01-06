package com.hmall.item.config;

import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/26 21:55:37
 */
@Configuration
public class RibbonConfiguration {

    @Bean
    public IRule iRule() {
        return new NacosRule();
    }
}

package com.hmall.config.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class CommonConfig {
    @Bean
    public DirectExchange delayDirectExchange() {
        return ExchangeBuilder
                .directExchange("delay.direct")
                .delayed()
                .build();
    }

    @Bean
    public Queue delayQueue() {
        return QueueBuilder
                .durable("delay.queue")
                .build();
    }

    @Bean
    public Binding delayDirectExchangeBindingDelayQueue() {
        return BindingBuilder.bind(delayQueue()).to(delayDirectExchange()).with("delay");
    }
}

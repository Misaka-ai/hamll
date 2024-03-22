package com.hmall.item.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ItemQueue {

    @Bean
    public DirectExchange updateEsItemExchange() {
        return ExchangeBuilder
                .directExchange("updateEsItem")
                .build();
    }

    @Bean
    public Queue updateEsItemQueue() {
        return QueueBuilder.durable("updateEsItemQueue").build();
    }
    @Bean
    public Queue insertEsItemQueue() {
        return QueueBuilder.durable("insertEsItemQueue").build();
    }
    @Bean
    public Queue deleteEsItemQueue() {
        return QueueBuilder.durable("deleteEsItemQueue").build();
    }


    @Bean
    public Binding updateEsItemBinding() {
        return BindingBuilder.bind(updateEsItemQueue()).to(updateEsItemExchange()).with("update");
    }

    @Bean
    public Binding insertEsItemBinding() {
        return BindingBuilder.bind(updateEsItemQueue()).to(updateEsItemExchange()).with("insert");
    }
    @Bean
    public Binding deleteEsItemBinding() {
        return BindingBuilder.bind(updateEsItemQueue()).to(updateEsItemExchange()).with("delete");
    }

}

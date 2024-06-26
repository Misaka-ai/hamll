package com.hamll.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {



    @Bean
    public RestHighLevelClient highLevelRestClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("192.168.17.130", 9200, "http"))
        );
    }
}

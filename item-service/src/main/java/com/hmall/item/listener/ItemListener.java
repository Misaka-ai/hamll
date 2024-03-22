package com.hmall.item.listener;

import com.alibaba.fastjson.JSON;
import com.hmall.common.pojo.Item;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class ItemListener {

    public static final String HAMLL = "hamll";
    private RestHighLevelClient restHighLevelClient;

    public ItemListener(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }


    @RabbitListener(queues = "updateEsItemQueue")
    public void setItem(Item item) {
        try {
            if (Objects.equals(item.getStatus(), 1)) {
                //将商品添加到ES中
                IndexRequest indexRequest = new IndexRequest(HAMLL);
                indexRequest.id(String.valueOf(item.getId()));
                indexRequest.source(JSON.toJSONString(item), XContentType.JSON);
                restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            } else {
                //将商品从ES中删除
                DeleteRequest deleteRequest = new DeleteRequest(HAMLL, String.valueOf(item.getId()));
                restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(ItemListener.class);
            logger.error("Error processing message", e);
        }
    }

    @RabbitListener(queues = "insertEsItemQueue")
    public void insertItemToES(Item item) {
        try {
            IndexRequest indexRequest = new IndexRequest(HAMLL);
            indexRequest.id(String.valueOf(item.getId()));
            indexRequest.source(JSON.toJSONString(item), XContentType.JSON);
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "deleteEsItemQueue")
    private void insertItemFromES(Long id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(HAMLL, String.valueOf(id));
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
    }

}

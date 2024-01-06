package com.hmall.listener;

import com.alibaba.fastjson.JSON;
import com.hmall.client.ItemClient;
import com.hmall.constant.MqConst;
import com.hmall.index.ItemIndex;
import com.hmall.pojo.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * 商品监听
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/26 19:04:54
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ItemListener {

    private final ItemClient itemClient;

    private final RestHighLevelClient restHighLevelClient;

    /**
     * 商品插入或更新侦听器
     *
     * @param itemId 商品id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConst.ITEM_INSERT_UPDATE_QUEUE),
            exchange = @Exchange(name = MqConst.ITEM_DIRECT),
            key = MqConst.ITEM_INSERT_UPDATE
    ))
    public void itemInsertOrUpdateListener(Long itemId) {
        Item item = itemClient.itemGetById(itemId);
        if (Objects.isNull(item)) {
            return;
        }
        try {
            IndexRequest indexRequest = new IndexRequest(ItemIndex.INDEX_NAME).id(String.valueOf(itemId));
            indexRequest.source(JSON.toJSONString(new ItemIndex(item)), XContentType.JSON);
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("-------ES新增和更新商品信息同步失败-------", e);
        }
    }

    /**
     * 商品删除监听器
     *
     * @param itemId 商品id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConst.ITEM_DELETE_QUEUE),
            exchange = @Exchange(name = MqConst.ITEM_DIRECT),
            key = MqConst.ITEM_DELETE
    ))
    public void itemDeleteListener(Long itemId) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(ItemIndex.INDEX_NAME, String.valueOf(itemId));
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("-------ES删除商品信息同步失败-------", e);
        }
    }
}

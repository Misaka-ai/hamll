package com.hamll.search.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.common.client.ItemClient;
import com.hmall.item.mapper.ItemMapper;
import com.hmall.item.pojo.Item;
import com.hmall.item.pojo.ItemDoc;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@SpringBootTest
class SearchServiceImplTest {
    @Autowired
    private ItemClient itemClient;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Autowired
    private Executor executor;

    @Autowired
    private ItemMapper itemMapper;

    @Test
    void insertData() {


        int pageNum = 1;  // Initialize pageNum

        while (true) {
            // Create Page object
            Page<Item> pageItem = new Page<>(pageNum, 100);
            itemMapper.selectPage(pageItem, null);

            // Process the hotels
            int bulkSize = 100; // Adjust this value as needed
            BulkRequest request = new BulkRequest();
            for (ItemDoc itemDoc : pageItem.getRecords().stream().map(ItemDoc::new).collect(Collectors.toList())) {
                IndexRequest indexRequest = new IndexRequest("hmall").id(itemDoc.getId().toString());
                indexRequest.source(JSON.toJSONString(itemDoc), XContentType.JSON);
                request.add(indexRequest);

                // Execute the bulk request when the number of documents reaches the threshold
                if (request.numberOfActions() >= bulkSize) {
                    try {
                        restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    request = new BulkRequest(); // Create a new BulkRequest for the next batch of documents
                }
            }

// Execute the remaining documents if any
            if (request.numberOfActions() > 0) {
                try {
                    restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // If there are no more pages, break the loop
            if (!((Page<Item>) pageItem).hasNext()) {
                break;
            }

            // Otherwise, move to the next page
            pageNum++;
        }
    }

    @Test
    void insertData11() {

        Integer count = itemMapper.selectCount(null);
        Integer page = count / 1000;
        if (count % page > 0) {
            page++;
        }
        CountDownLatch countDownLatch = new CountDownLatch(page.intValue());

        ThreadPoolExecutor pool = new ThreadPoolExecutor(24, 60, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));

        for (int i = 0; i < page; i++) {
            int finalI = i;
            pool.submit(() -> {
                countDownLatch.countDown();
                Page<Item> itemPage = new Page<>(finalI, 1000);

                Page<Item> selectedPage = itemMapper.selectPage(itemPage, null);
                BulkRequest bulkRequest = new BulkRequest();
                List<Item> records = selectedPage.getRecords();
                records.stream().map(ItemDoc::new).forEach(itemDoc -> {
                    IndexRequest item = new IndexRequest("hmall").id(itemDoc.getId().toString());
                    item.source(JSONObject.toJSONString(itemDoc), XContentType.JSON);
                    bulkRequest.add(item);
                });
                try {
                    restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
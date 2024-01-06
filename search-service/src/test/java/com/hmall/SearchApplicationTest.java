package com.hmall;

import com.alibaba.fastjson2.JSON;
import com.hmall.client.ItemClient;
import com.hmall.dto.PageDTO;
import com.hmall.index.ItemIndex;
import com.hmall.pojo.Item;
import lombok.extern.slf4j.Slf4j;
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


/**
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/26 18:07:15
 */
@SpringBootTest
@Slf4j
public class SearchApplicationTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ItemClient itemClient;

    /**
     * 存量数据转换
     *
     * @throws IOException ioexception
     */
    @Test
    void dataTranslate() throws IOException {
        long page = 1L;
        while (true) {
            PageDTO<Item> itemPageDTO = itemClient.itemList(page, 1000L);
            List<Item> list = itemPageDTO.getList();
            if (list.size() == 0) {
                break;
            }
            BulkRequest request = new BulkRequest();
            list.stream()
                    .map(ItemIndex::new)
                    .forEach(itemIndex -> {
                        IndexRequest indexRequest = new IndexRequest(ItemIndex.INDEX_NAME)
                                .id(String.valueOf(itemIndex.getId()));

                        indexRequest.source(JSON.toJSONString(itemIndex), XContentType.JSON);
                        request.add(indexRequest);
                    });
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            log.info("--------------完成第{}页--------------", page);
            page++;
        }
    }
}

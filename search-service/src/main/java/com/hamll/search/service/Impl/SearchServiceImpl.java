package com.hamll.search.service.Impl;


import com.alibaba.fastjson.JSON;
import com.hamll.search.dto.SearchDTO;
import com.hamll.search.service.SearchService;
import com.hamll.search.vo.ItemIndexVO;
import com.hmall.common.client.ItemClient;
import com.hmall.common.dto.PageDTO;
import com.hmall.item.pojo.ItemDoc;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    public static final String HMALL = "hmall";
    @Autowired
    private ItemClient itemClient;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Autowired
    private Executor executor;


    @Override
    public PageDTO<ItemIndexVO> searchList(SearchDTO searchDTO) {
        PageDTO<ItemIndexVO> pageDTO;
        SearchRequest searchRequest = new SearchRequest(HMALL);
        int from = (searchDTO.getPage() - 1) * searchDTO.getSize();
        try {
            if (StringUtils.hasText(searchDTO.getKey())) {
                searchRequest.source()
                        .size(searchDTO.getSize())
                        .from(from)
                        .query(
                                QueryBuilders.matchQuery("all", searchDTO.getKey())
                        )
                        .highlighter(new HighlightBuilder()
                                .field("all")
                                .preTags("<span style='color: red;'>")
                                .postTags("</span>")
                        );

            } else {
                searchRequest.source()
                        .size(searchDTO.getSize())
                        .from(from)
                        .query(
                                QueryBuilders.matchAllQuery()
                        );
            }
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            TotalHits totalHits = hits.getTotalHits();
            long value = totalHits.value;
            SearchHit[] hitsHits = hits.getHits();

            pageDTO = new PageDTO<>();
            List<ItemIndexVO> itemIndexVOS = Arrays.stream(hitsHits).map(hit -> {
                String sourceAsString = hit.getSourceAsString();
                return JSON.parseObject(sourceAsString, ItemIndexVO.class);
            }).collect(Collectors.toList());

            pageDTO.setList(itemIndexVOS);
            pageDTO.setTotal(value);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pageDTO;
    }
}
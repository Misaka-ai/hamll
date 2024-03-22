package com.hamll.search.service.Impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.StringUtils;
import com.hamll.search.dto.SearchDTO;
import com.hamll.search.service.SearchService;
import com.hamll.search.vo.ItemIndexVO;
import com.hmall.common.dto.PageDTO;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    public static final String HMALL = "hmall";

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Override
    public PageDTO<ItemIndexVO> searchList(SearchDTO searchDTO) {
        SearchRequest searchRequest = new SearchRequest(HMALL);
        int from = (searchDTO.getPage() - 1) * searchDTO.getSize();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (StringUtils.hasText(searchDTO.getCategory())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("category", searchDTO.getCategory()));
        }
        if (StringUtils.hasText(searchDTO.getBrand())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("brand", searchDTO.getBrand()));
        }
        if (Objects.equals(searchDTO.getSortBy(), "sold")) {
            searchRequest.source().sort(SortBuilders.fieldSort("sold").order(SortOrder.DESC));
        }
        if (Objects.nonNull(searchDTO.getMinPrice()) && Objects.nonNull(searchDTO.getMaxPrice())) {
            boolQueryBuilder.filter(
                    QueryBuilders.rangeQuery("price")
                            .gte(searchDTO.getMinPrice())
                            .lte(searchDTO.getMaxPrice())
            );
        }
        if (Objects.equals(searchDTO.getSortBy(), "price")) {
            searchRequest.source().sort(SortBuilders.fieldSort("price").order(SortOrder.ASC));
        }
        if (StringUtils.hasText(searchDTO.getKey())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("all", searchDTO.getKey()));
            searchRequest.source().highlighter(new HighlightBuilder()
                    .field("all")
                    .preTags("<span style='color: red;'>")
                    .postTags("</span>")
            );
        } else {
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        }

        searchRequest.source()
                .query(boolQueryBuilder)
                .from(from)
                .size(searchDTO.getSize());

        return executeSearch(searchRequest);
    }

    private PageDTO<ItemIndexVO> executeSearch(SearchRequest searchRequest) {
        PageDTO<ItemIndexVO> pageDTO = new PageDTO<>();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits().value;
            List<ItemIndexVO> itemIndexVOS = Arrays.stream(hits.getHits())
                    .map(hit -> JSON.parseObject(hit.getSourceAsString(), ItemIndexVO.class))
                    .collect(Collectors.toList());

            pageDTO.setList(itemIndexVOS);
            pageDTO.setTotal(totalHits);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pageDTO;
    }

    @Override
    public Map<String, List<String>> filters(SearchDTO searchDTO) {
        SearchRequest searchRequest = new SearchRequest(HMALL);
        String category = searchDTO.getCategory();
        String brand = searchDTO.getBrand();
        Integer minPrice = searchDTO.getMinPrice();
        Integer maxPrice = searchDTO.getMaxPrice();

        String categoryAgg = "categoryAgg";
        String brandAgg = "brandAgg";

        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(category, brand, minPrice, maxPrice);

        try {
            searchRequest.source()
                    .query(boolQueryBuilder)
                    .aggregation(AggregationBuilders.terms(categoryAgg)
                            .field("category.keyword"))
                    .aggregation(AggregationBuilders.terms(brandAgg)
                            .field("brand.keyword"))
            ;

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            Aggregations aggregations = searchResponse.getAggregations();
            ParsedStringTerms cateA = aggregations.get(categoryAgg);
            ParsedStringTerms brandA = aggregations.get(brandAgg);
            List<String> categoryties = cateA.getBuckets().stream().map(
                    MultiBucketsAggregation.Bucket::getKeyAsString
            ).collect(Collectors.toList());
            List<String> brands = brandA.getBuckets().stream().map(
                    MultiBucketsAggregation.Bucket::getKeyAsString
            ).collect(Collectors.toList());

            HashMap<String, List<String>> stringListHashMap = new HashMap<>();
            stringListHashMap.put("category", categoryties);
            stringListHashMap.put("brand", brands);

            return stringListHashMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<String> suggestion(String key) {
        SearchRequest searchRequest = new SearchRequest(HMALL);
        if (StringUtils.isEmpty(key)) {
            return new ArrayList<>();
        }

        List<String> suggestionList;
        try {
            searchRequest.source()
                    .suggest(
                            new SuggestBuilder()
                                    .addSuggestion("suggestionSugg"
                                            , SuggestBuilders.completionSuggestion("suggestion")
                                                    .prefix(key)
                                                    .skipDuplicates(true)
                                                    .size(10)
                                    )
                    );
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            Suggest responseSuggest = searchResponse.getSuggest();
            CompletionSuggestion suggestionSugg = responseSuggest.getSuggestion("suggestionSugg");
            suggestionList = suggestionSugg.getOptions().stream()
                    .map(option -> option.getText().string()).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return suggestionList;
    }

    private BoolQueryBuilder getBoolQueryBuilder(String category, String brand, Integer minPrice, Integer maxPrice) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();


        if (StringUtils.hasText(category)) {
            boolQueryBuilder.must(
                    QueryBuilders.matchQuery("category", category)
            );
        }

        if (StringUtils.hasText(brand)) {
            boolQueryBuilder.must(
                    QueryBuilders.matchQuery("brand", brand)
            );
        }
        if (Objects.nonNull(minPrice) && Objects.nonNull(maxPrice)) {
            boolQueryBuilder.filter(
                    QueryBuilders.rangeQuery("price")
                            .gte(minPrice)
                            .lte(maxPrice)
            );
        }

        return boolQueryBuilder;
    }
}
package com.hmall.service.impl;

import com.alibaba.fastjson.JSON;
import com.hmall.dto.PageDTO;
import com.hmall.dto.SearchDTO;
import com.hmall.index.ItemIndex;
import com.hmall.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SearchService
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/26 21:30:24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final RestHighLevelClient restHighLevelClient;

    @Override
    public List<String> suggestion(String key) {
        try {
            SearchRequest searchRequest = new SearchRequest(ItemIndex.INDEX_NAME);

            searchRequest.source()
                    .size(0)
                    .suggest(
                            new SuggestBuilder()
                                    .addSuggestion(ItemIndex.SUGGESTION_NAME,
                                            SuggestBuilders.completionSuggestion("suggestion")
                                                    .prefix(key)
                                                    .skipDuplicates(true)
                                                    .size(10)

                                    )
                    );

            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            CompletionSuggestion suggestion = response.getSuggest().getSuggestion(ItemIndex.SUGGESTION_NAME);
            List<CompletionSuggestion.Entry.Option> options = suggestion.getOptions();
            return options.stream()
                    .map(item -> item.getText().string())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("ES补全检索失败", e);
        }
        return Collections.emptyList();
    }

    @Override
    public PageDTO<ItemIndex> list(SearchDTO searchDTO) {
        try {
            SearchRequest searchRequest = new SearchRequest(ItemIndex.INDEX_NAME);
            // bool查询条件构建
            BoolQueryBuilder boolQuery = getBoolQuery(searchDTO);
            // 算分查询
            searchRequest.source()
                    .query(
                            QueryBuilders.functionScoreQuery(
                                            boolQuery,
                                            new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                                                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                                            QueryBuilders.termQuery("isAD", true),
                                                            ScoreFunctionBuilders.weightFactorFunction(10F)
                                                    )
                                            }

                                    )
                                    .boostMode(CombineFunction.MULTIPLY)
                    );
            // 排序
            if (StringUtils.isNotBlank(searchDTO.getSortBy()) &&
                    !Objects.equals(searchDTO.getSortBy(), "default")) {
                searchRequest.source()
                        .sort(SortBuilders.fieldSort(searchDTO.getSortBy()));
            }
            // 分页查询
            int page = Objects.isNull(searchDTO.getPage()) ? 1 : searchDTO.getPage();
            int size = Objects.isNull(searchDTO.getSize()) ? 10 : searchDTO.getSize();
            searchRequest.source()
                    .from(page)
                    .size(size);
            // 高亮显示
            if (StringUtils.isNotBlank(searchDTO.getKey())) {
                searchRequest.source()
                        .highlighter(
                                SearchSourceBuilder.highlight()
                                        .field("name")
                                        .requireFieldMatch(false)
                                        .preTags("<em>")
                                        .postTags("</em>")

                        );
            }
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            SearchHit[] hitsHits = hits.getHits();
            List<ItemIndex> itemIndexList = Arrays.stream(hitsHits)
                    .map(item -> {
                        String sourceAsString = item.getSourceAsString();
                        Map<String, HighlightField> highlightFields = item.getHighlightFields();
                        HighlightField highlightField = highlightFields.get("name");
                        ItemIndex itemIndex = JSON.parseObject(sourceAsString, ItemIndex.class);
                        if (Objects.nonNull(highlightField)) {
                            Text[] fragments = highlightField.getFragments();
                            if (fragments.length > 0) {
                                Text fragment = fragments[0];
                                itemIndex.setName(fragment.string());
                            }

                        }
                        return itemIndex;
                    })
                    .collect(Collectors.toList());
            long total = hits.getTotalHits().value;
            return new PageDTO<>(total, itemIndexList);
        } catch (IOException e) {
            log.error("ES搜搜失败", e);
        }
        return new PageDTO<>();
    }

    @Override
    public Map<String, List<String>> filters(SearchDTO searchDTO) {
        try {
            SearchRequest searchRequest = new SearchRequest(ItemIndex.INDEX_NAME);
            // bool查询条件构建
            BoolQueryBuilder boolQuery = getBoolQuery(searchDTO);
            // bool查询
            searchRequest.source().query(boolQuery);
            // 聚合搜索
            searchRequest.source()
                    .aggregation(
                            AggregationBuilders.terms(ItemIndex.AGGREGATION_CATEGORY)
                                    .field("category")
                                    .size(20)
                    );
            searchRequest.source()
                    .aggregation(
                            AggregationBuilders.terms(ItemIndex.AGGREGATION_BRAND)
                                    .field("brand")
                                    .size(20)
                    );
            // 设置分页数据返回为0
            searchRequest.source().size(0);
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            if (Objects.isNull(aggregations)) {
                return new HashMap<>();
            }
            ParsedStringTerms aggregationCategory = aggregations.get(ItemIndex.AGGREGATION_CATEGORY);
            ParsedStringTerms aggregationBrand = aggregations.get(ItemIndex.AGGREGATION_BRAND);
            List<String> categoryList = aggregationCategory.getBuckets()
                    .stream().map(MultiBucketsAggregation.Bucket::getKeyAsString)
                    .collect(Collectors.toList());
            List<String> brandList = aggregationBrand.getBuckets()
                    .stream().map(MultiBucketsAggregation.Bucket::getKeyAsString)
                    .collect(Collectors.toList());
            // 封装结果
            HashMap<String, List<String>> map = new HashMap<>();
            map.put("category", categoryList);
            map.put("brand", brandList);
            return map;
        } catch (IOException e) {
            log.error("ES搜搜失败", e);
        }
        return new HashMap<>();
    }

    private static BoolQueryBuilder getBoolQuery(SearchDTO searchDTO) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(searchDTO.getKey())) {
            boolQuery.must(QueryBuilders.matchQuery("all", searchDTO.getKey()));
        }
        if (StringUtils.isNotBlank(searchDTO.getCategory())) {
            boolQuery.filter(QueryBuilders.termQuery("category", searchDTO.getCategory()));
        }
        if (StringUtils.isNotBlank(searchDTO.getBrand())) {
            boolQuery.filter(QueryBuilders.termQuery("brand", searchDTO.getBrand()));
        }
        if (Objects.nonNull(searchDTO.getMinPrice()) && Objects.nonNull(searchDTO.getMaxPrice())) {
            boolQuery.filter(QueryBuilders.rangeQuery("price")
                    .gte(searchDTO.getMinPrice() * 100)
                    .lte(searchDTO.getMaxPrice() * 100)
            );
        }
        return boolQuery;
    }
}

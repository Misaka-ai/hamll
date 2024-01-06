package com.hmall.service;

import com.hmall.dto.PageDTO;
import com.hmall.dto.SearchDTO;
import com.hmall.index.ItemIndex;

import java.util.List;
import java.util.Map;

/**
 * SearchService
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/26 21:30:11
 */
public interface SearchService {
    /**
     * 建议
     *
     * @param key 钥匙
     * @return {@link List}<{@link String}>
     */
    List<String> suggestion(String key);

    /**
     * 列表
     *
     * @param searchDTO 搜索d到
     * @return {@link PageDTO}<{@link ItemIndex}>
     */
    PageDTO<ItemIndex> list(SearchDTO searchDTO);

    /**
     * 过滤器
     *
     * @param searchDTO 搜索d到
     * @return {@link Map}<{@link String}, {@link List}<{@link String}>>
     */
    Map<String, List<String>> filters(SearchDTO searchDTO);
}

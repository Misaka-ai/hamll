package com.hmall.web;

import com.hmall.dto.PageDTO;
import com.hmall.dto.SearchDTO;
import com.hmall.index.ItemIndex;
import com.hmall.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/26 19:22:45
 */
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 建议
     *
     * @param key 钥匙
     * @return {@link List}<{@link String}>
     */
    @GetMapping("/suggestion")
    public List<String> suggestion(String key) {
        return searchService.suggestion(key);
    }

    /**
     * 列表
     *
     * @param searchDTO 搜索d到
     * @return {@link PageDTO}<{@link ItemIndex}>
     */
    @PostMapping("/list")
    public PageDTO<ItemIndex> list(@RequestBody SearchDTO searchDTO) {
        return searchService.list(searchDTO);
    }

    /**
     * 过滤器
     *
     * @param searchDTO 搜索d到
     * @return {@link Map}<{@link String}, {@link List}<{@link String}>>
     */
    @PostMapping("/filters")
    public Map<String, List<String>> filters(@RequestBody SearchDTO searchDTO) {
        return searchService.filters(searchDTO);
    }
}

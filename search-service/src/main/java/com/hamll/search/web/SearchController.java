package com.hamll.search.web;

import com.hamll.search.dto.SearchDTO;
import com.hamll.search.service.SearchService;
import com.hamll.search.vo.ItemIndexVO;
import com.hmall.common.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /*
     * 基本搜索功能
     * */
    @PostMapping("/list")
    public PageDTO<ItemIndexVO> searchList(@RequestBody SearchDTO searchDTO) {
        return searchService.searchList(searchDTO);
    }

    /*
     * 条件过滤
     * */
    @PostMapping("/filters")
    public Map<String, List<String>> filters(@RequestBody SearchDTO searchDTO) {
        return searchService.filters(searchDTO);
    }
/*
* 自动补全
* */
    @GetMapping("/suggestion")
    public List<String> suggestion(String key){
        return searchService.suggestion(key);
    }

}

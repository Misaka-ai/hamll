package com.hamll.search.web;

import com.hamll.search.dto.SearchDTO;
import com.hamll.search.service.SearchService;
import com.hamll.search.vo.ItemIndexVO;
import com.hmall.common.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

private final SearchService searchService;

    @PostMapping("/list")
    public PageDTO<ItemIndexVO> searchList(@RequestBody SearchDTO searchDTO){
        return searchService.searchList(searchDTO);
    }








}

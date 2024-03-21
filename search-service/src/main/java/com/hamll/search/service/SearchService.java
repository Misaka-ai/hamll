package com.hamll.search.service;

import com.hamll.search.dto.SearchDTO;
import com.hamll.search.vo.ItemIndexVO;
import com.hmall.common.dto.PageDTO;

import java.util.List;
import java.util.Map;

public interface SearchService {

    /*
     * 基本搜索
     * */
    PageDTO<ItemIndexVO> searchList(SearchDTO searchDTO);

    /*
     * 条件过滤
     * */
    Map<String, List<String>> filters(SearchDTO searchDTO);

    /*
     * 自动补全
     * */
    List<String> suggestion(String key);
}

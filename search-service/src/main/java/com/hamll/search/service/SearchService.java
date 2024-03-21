package com.hamll.search.service;

import com.hamll.search.dto.SearchDTO;
import com.hamll.search.vo.ItemIndexVO;
import com.hmall.common.dto.PageDTO;

public interface SearchService {
    PageDTO<ItemIndexVO> searchList(SearchDTO searchDTO);
}

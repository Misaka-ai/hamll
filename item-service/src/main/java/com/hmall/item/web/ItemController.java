package com.hmall.item.web;

import com.hmall.common.dto.PageDTO;
import com.hmall.item.pojo.Item;
import com.hmall.item.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private IItemService itemService;

    /*
     * 分页查询
     * */
    @GetMapping("list")
    PageDTO<Item> list(Integer page, Integer size) {
        return itemService.pageQuery(page,size);
    }
}

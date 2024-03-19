package com.hmall.item.web;

import com.hmall.common.dto.PageDTO;
import com.hmall.item.pojo.Item;
import com.hmall.item.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private IItemService itemService;

    /*
     * 分页查询
     * */
    @GetMapping("list")
    public PageDTO<Item> list(Integer page, Integer size) {
        return itemService.pageQuery(page, size);
    }

    /*
     * 根据id查询详情
     *
     * */
    @GetMapping("{id}")
    public Item getByid(@PathVariable Long id) {
        return itemService.getByOneId(id);
    }
    /*
     * 上下架商品
     * */
    @PutMapping("status/{id}/{status}")
    public void updateStatus(@PathVariable Long id,@PathVariable Integer status){
        itemService.updateStatus(id,status);
    }
}

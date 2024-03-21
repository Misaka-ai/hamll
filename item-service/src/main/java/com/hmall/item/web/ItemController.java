package com.hmall.item.web;

import com.hmall.common.dto.PageDTO;
import com.hmall.item.pojo.Item;
import com.hmall.item.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private IItemService itemService;

    /*
     * 分页查询
     * */
    @GetMapping("/list")
    public PageDTO<?> list(Integer page, Integer size) {
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
    public void updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        itemService.updateStatus(id, status);
    }

    /*
     * 根据id删除商品
     * */
    @DeleteMapping("{id}")
    public void deleteItenById(@PathVariable Long id) {
        itemService.deleteItenById(id);
    }

    /*
     * 修改商品
     * */
    @PutMapping()
    public void updateItem(@RequestBody Item item) {
        itemService.updateItem(item);
    }

    /*
     * 新增商品
     **/
    @PostMapping()
    public void insertItem(@RequestBody Item item) {
        itemService.insertItem(item);
    }


}

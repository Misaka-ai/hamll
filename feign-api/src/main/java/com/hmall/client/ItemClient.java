package com.hmall.client;

import com.hmall.dto.PageDTO;
import com.hmall.pojo.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品客户端
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/25 19:49:45
 */
@FeignClient("item-service")
public interface ItemClient {

    /**
     * 按id获取
     *
     * @param id id
     * @return {@link Item}
     */
    @GetMapping("/item/{id}")
    Item itemGetById(@PathVariable Long id);

    /**
     * 商品的分页查询
     *
     * @param page 页面
     * @param size 尺寸
     * @return {@link PageDTO}<{@link Item}>
     */
    @GetMapping("/item/list")
    PageDTO<Item> itemList(@RequestParam(defaultValue = "1") Long page,
                           @RequestParam(defaultValue = "10") Long size);

    /**
     * 按id更新
     *
     * @param item 项目
     */
    @PutMapping("/item")
    void itemUpdateById(@RequestBody Item item);

    /**
     * 项目更新库存批次
     *
     * @param itemList 项目清单
     */
    @PutMapping("/item/stockBatch")
    void itemUpdateStockBatch(@RequestBody List<Item> itemList);
}

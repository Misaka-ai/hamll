package com.hmall.common.client;


import com.hmall.common.dto.PageDTO;
import com.hmall.common.interceptor.MyFeignInterceptor;
import com.hmall.common.pojo.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "itemservice", configuration = MyFeignInterceptor.class)
public interface ItemClient {

    @GetMapping("/item/list")
    PageDTO<Item> list(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping("/item/{id}")
    Item getByid(@PathVariable Long id);

    @PutMapping("/item")
    void updateItem(@RequestBody Item item);
}

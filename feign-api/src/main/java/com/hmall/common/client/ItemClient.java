package com.hmall.common.client;


import com.hmall.common.dto.PageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "itemservice", configuration = FeignClient.class)
public interface ItemClient {

    @GetMapping("/item/list")
    PageDTO<?> list(@RequestParam("page") Integer page, @RequestParam("size") Integer size);


}

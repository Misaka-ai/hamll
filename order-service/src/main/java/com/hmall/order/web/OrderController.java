package com.hmall.order.web;

import com.hmall.order.dto.OrderDTO;
import com.hmall.order.pojo.Order;
import com.hmall.order.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @GetMapping("{id}")
    public Order queryOrderById(@PathVariable("id") Long orderId) {
        return orderService.getOrderById(orderId);
    }

    /*
     * 下单并返回订单ID
     * */
    @PostMapping()
    public Long getOrederId(@RequestBody OrderDTO orderDTO) {

        Long orederId = orderService.getOrederId(orderDTO);
        orederId = orederId / 1000;
        return orederId;
    }
}

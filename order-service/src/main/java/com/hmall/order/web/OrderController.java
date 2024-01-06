package com.hmall.order.web;

import com.hmall.dto.CreateOrderDTO;
import com.hmall.pojo.Order;
import com.hmall.order.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @GetMapping("/{id}")
    public Order queryOrderById(@PathVariable("id") Long orderId) {
        return orderService.getById(orderId);
    }

    /**
     * 创建订单
     *
     * @param createOrderDTO 创建订单DTO
     * @return {@link Long}
     */
    @PostMapping
    public Long createOrder(@RequestBody CreateOrderDTO createOrderDTO) {
        return orderService.createOrder(createOrderDTO);
    }
}

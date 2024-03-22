package com.hmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.order.dto.OrderDTO;
import com.hmall.order.pojo.Order;

public interface IOrderService extends IService<Order> {
    /*
     * 下单并返回订单ID
     * */
    Long getOrederId(OrderDTO orderDTO);


    Order getOrderById(Long orderId);
}

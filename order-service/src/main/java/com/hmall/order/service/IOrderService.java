package com.hmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.dto.CreateOrderDTO;
import com.hmall.pojo.Order;

public interface IOrderService extends IService<Order> {
    /**
     * 创建订单
     *
     * @param createOrderDTO 创建订单d去
     * @return {@link Long}
     */
    Long createOrder(CreateOrderDTO createOrderDTO);

    /**
     * 取消订单
     *
     * @param orderId 订单id
     */
    void cancelOrder(Long orderId);
}

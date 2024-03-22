package com.hmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.client.ItemClient;
import com.hmall.common.client.UserClient;
import com.hmall.common.pojo.Address;
import com.hmall.common.pojo.Item;

import com.hmall.myThread.MyThread;
import com.hmall.order.dto.OrderDTO;
import com.hmall.order.mapper.OrderDetailMapper;
import com.hmall.order.mapper.OrderLogisticsMapper;
import com.hmall.order.mapper.OrderMapper;
import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.OrderDetail;
import com.hmall.order.pojo.OrderLogistics;
import com.hmall.order.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class OrderService extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    public static final int STATUS = 1;
    private final ItemClient itemClient;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final UserClient userClient;
    private final OrderLogisticsMapper orderLogisticsMapper;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(ItemClient itemClient, OrderMapper orderMapper, OrderDetailMapper orderDetailMapper, UserClient userClient, OrderLogisticsMapper orderLogisticsMapper, RabbitTemplate rabbitTemplate) {
        this.itemClient = itemClient;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.userClient = userClient;
        this.orderLogisticsMapper = orderLogisticsMapper;
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    @Transactional
    public Long getOrederId(OrderDTO orderDTO) {
        log.info("开始生成订单");
        Item item = itemClient.getByid(orderDTO.getItemId());//根据itemid获取商品信息
        Long totalFee = item.getPrice() * orderDTO.getNum();

        //生成订单
        Order order = new Order();
        order.setTotalFee(totalFee);
        order.setPaymentType(orderDTO.getPaymentType());
        order.setUserId(2L);
        order.setStatus(STATUS);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        orderMapper.insert(order);

        //生成订单详情
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(order.getId());
        orderDetail.setItemId(item.getId());
        orderDetail.setNum(orderDTO.getNum());
        orderDetail.setPrice(item.getPrice());
        orderDetail.setSpec(item.getSpec());
        orderDetail.setImage(item.getImage());
        orderDetail.setCreateTime(new Date());
        orderDetail.setUpdateTime(new Date());
        orderDetail.setName(item.getName());
        orderDetailMapper.insert(orderDetail);

        Address address = userClient.findAddressById(orderDTO.getAddressId());
        OrderLogistics orderLogistics = getOrderLogistics(order, address);
        orderLogisticsMapper.insert(orderLogistics);
        item.setStock(item.getStock() - orderDTO.getNum());

        itemClient.updateItem(item);

        String id = order.getId().toString();
        Message message = MessageBuilder.withBody(id.getBytes())
                .setHeader("x-delay", 1000 * 60 * 30)
                .build();
        rabbitTemplate.convertAndSend("delay.direct", "delay", message);

        return order.getId();
    }

    @Override
    public Order getOrderById(Long orderId) {


        return orderMapper.getOrderById(orderId);
    }

    private static OrderLogistics getOrderLogistics(Order order, Address address) {
        OrderLogistics orderLogistics = new OrderLogistics();

        orderLogistics.setOrderId(order.getId());
        orderLogistics.setLogisticsNumber(null);
        orderLogistics.setLogisticsCompany(null);
        orderLogistics.setContact(address.getContact());
        orderLogistics.setMobile(address.getMobile());
        orderLogistics.setProvince(address.getProvince());
        orderLogistics.setCity(address.getCity());
        orderLogistics.setTown(address.getTown());
        orderLogistics.setStreet(address.getStreet());
        orderLogistics.setCreateTime(new Date());
        orderLogistics.setUpdateTime(new Date());
        return orderLogistics;
    }
}

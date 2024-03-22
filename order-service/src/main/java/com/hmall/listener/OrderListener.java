package com.hmall.listener;

import com.hmall.common.client.ItemClient;
import com.hmall.common.message.MessageConstant;
import com.hmall.common.pojo.Item;
import com.hmall.order.mapper.OrderDetailMapper;
import com.hmall.order.mapper.OrderMapper;
import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.OrderDetail;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OrderListener {


    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final ItemClient itemClient;

    public OrderListener(OrderMapper orderMapper, OrderDetailMapper orderDetailMapper, ItemClient itemClient) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.itemClient = itemClient;
    }


    @RabbitListener(queues = "delay.queue")
    public void watchDelayQueue(String order) {
        //根据订单id查询订单
        Order order1 = orderMapper.selectById(order);
        //判断支付状态
        if (order1.getStatus().equals(MessageConstant.NON_PAYMENT)) {
            order1.setStatus(MessageConstant.CANCELED_AND_CLOSED);
            order1.setUpdateTime(new Date());
            order1.setCloseTime(new Date());
            //恢复扣减库存根据订单id查询订单详情
            OrderDetail orderDetail = orderDetailMapper.selectByOrderId(order);
            Integer num = orderDetail.getNum();
            Long itemId = orderDetail.getItemId();
            orderDetail.setUpdateTime(new Date());
            Item item = itemClient.getByid(itemId);
            item.setStock(item.getStock() + num);
            itemClient.updateItem(item);

        }

    }
}

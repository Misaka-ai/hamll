package com.hmall.order.listener;

import com.hmall.constant.MqConst;
import com.hmall.order.service.impl.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 订单消息监听
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/27 19:48:24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderListener {

    private final OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConst.ORDER_TTL_QUEUE),
            exchange = @Exchange(name = MqConst.ORDER_TTL_DIRECT, delayed = "true"),
            key = MqConst.ORDER_TTL
    ))
    public void orderExpireListen(String orderId) {
        log.info("------------订单超时取消:{}----------", orderId);
        orderService.cancelOrder(Long.parseLong(orderId));
    }
}

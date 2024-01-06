package com.hmall.order.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.client.ItemClient;
import com.hmall.client.UserClient;
import com.hmall.constant.MqConst;
import com.hmall.context.BaseContext;
import com.hmall.dto.CreateOrderDTO;
import com.hmall.order.mapper.OrderDetailMapper;
import com.hmall.order.mapper.OrderLogisticsMapper;
import com.hmall.order.mapper.OrderMapper;
import com.hmall.order.service.IOrderService;
import com.hmall.pojo.*;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    private final ItemClient itemClient;

    private final UserClient userClient;

    private final OrderDetailMapper orderDetailMapper;

    private final OrderLogisticsMapper orderLogisticsMapper;

    private final RabbitTemplate rabbitTemplate;

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public Long createOrder(CreateOrderDTO createOrderDTO) {
        // 查询商品
        Item item = itemClient.itemGetById(createOrderDTO.getItemId());
        if (Objects.isNull(item)) {
            throw new RuntimeException("商品不存在");
        }
        if (item.getStock() < createOrderDTO.getNum()) {
            throw new RuntimeException("商品库存不足");
        }
        // 获取地址信息
        Address address = userClient.findAddressById(createOrderDTO.getAddressId());
        if (Objects.isNull(address)) {
            throw new RuntimeException("地址不存在");
        }
        // 创建订单
        Order order = createOrder(createOrderDTO, item);
        // 创建订单详情
        createOrderDetail(createOrderDTO, item, order);
        // 物流表
        createOrderLogistics(address, order);
        // 库存处理
        item.setStock(item.getStock() - createOrderDTO.getNum());
        itemClient.itemUpdateById(item);
        // 向MQ推送有效期数据
        sendTTLMessage(order.getId());
        return order.getId();
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        // 订单取消
        Order order = baseMapper.selectOne(
                Wrappers.<Order>lambdaQuery()
                        .eq(Order::getId, orderId)
                        .eq(Order::getStatus, 1)
        );
        // 幂等处理，如果状态已经不为1，则无需取消订单
        if (Objects.isNull(order)) {
            return;
        }
        // 状态改成取消订单
        order.setStatus(5);
        order.setCloseTime(new Date());
        order.setUpdateTime(new Date());
        baseMapper.updateById(order);
        // 库存恢复
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(
                Wrappers.<OrderDetail>lambdaQuery()
                        .eq(OrderDetail::getOrderId, orderId)
        );
        List<Item> itemList = orderDetails.stream()
                .map(orderDetail -> {
                    Item item = new Item();
                    item.setId(orderDetail.getItemId());
                    item.setStock(orderDetail.getNum());
                    return item;
                })
                .collect(Collectors.toList());
        itemClient.itemUpdateStockBatch(itemList);
    }

    /**
     * 创建ttlmessage
     *
     * @param orderId 订单id
     */
    private void sendTTLMessage(Long orderId) {
        Message message = MessageBuilder.withBody(String.valueOf(orderId).getBytes())
                // 测试数据，有效期 10s
                .setHeader("x-delay", 5000)
                .build();
        rabbitTemplate.convertAndSend(MqConst.ORDER_TTL_DIRECT, MqConst.ORDER_TTL, message);
    }

    /**
     * 创建订单物流
     *
     * @param address 地址
     * @param order   订单
     */
    private void createOrderLogistics(Address address, Order order) {
        OrderLogistics orderLogistics = new OrderLogistics();
        orderLogistics.setOrderId(order.getId());
        orderLogistics.setContact(address.getContact());
        orderLogistics.setMobile(address.getMobile());
        orderLogistics.setProvince(address.getProvince());
        orderLogistics.setCity(address.getCity());
        orderLogistics.setTown(address.getTown());
        orderLogistics.setStreet(address.getStreet());
        orderLogistics.setCreateTime(new Date());
        orderLogistics.setUpdateTime(new Date());
        orderLogisticsMapper.insert(orderLogistics);
    }

    /**
     * 创建订单详细信息
     *
     * @param createOrderDTO 创建订单d去
     * @param item           项目
     * @param order          订单
     */
    private void createOrderDetail(CreateOrderDTO createOrderDTO, Item item, Order order) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(order.getId());
        orderDetail.setItemId(createOrderDTO.getItemId());
        orderDetail.setNum(createOrderDTO.getNum());
        orderDetail.setName(item.getName());
        orderDetail.setPrice(item.getPrice());
        orderDetail.setSpec(item.getSpec());
        orderDetail.setImage(item.getImage());
        orderDetail.setCreateTime(new Date());
        orderDetail.setUpdateTime(new Date());
        orderDetailMapper.insert(orderDetail);
    }

    /**
     * 创建订单
     *
     * @param createOrderDTO 创建订单d去
     * @param item           项目
     * @return {@link Order}
     */
    private Order createOrder(CreateOrderDTO createOrderDTO, Item item) {
        Order order = new Order();
        Long price = item.getPrice();
        order.setTotalFee(createOrderDTO.getNum() * price);
        order.setPaymentType(createOrderDTO.getPaymentType());
        order.setUserId(BaseContext.getCurrentUserId());
        order.setStatus(1);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        baseMapper.insert(order);
        return order;
    }
}

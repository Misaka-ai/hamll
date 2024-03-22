package com.hmall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmall.order.pojo.Order;
import org.apache.ibatis.annotations.Select;

public interface OrderMapper extends BaseMapper<Order> {
    @Select("SELECT * FROM tb_order WHERE id LIKE CONCAT('%',#{orderId},'%')")
    Order getOrderById(Long orderId);
}

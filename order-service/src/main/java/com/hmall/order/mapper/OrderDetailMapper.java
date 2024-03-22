package com.hmall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmall.order.pojo.OrderDetail;
import org.apache.ibatis.annotations.Select;

public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

    @Select("select * from tb_order_detail where order_id=#{oreder};")
    OrderDetail selectByOrderId(String order);
}

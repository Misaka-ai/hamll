package com.hmall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmall.pojo.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * OrderDetailMapper
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/25 19:59:27
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}

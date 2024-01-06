package com.hmall.dto;

import lombok.Data;

/**
 * 下单DTO
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/25 19:30:22
 */
@Data
public class CreateOrderDTO {

    /**
     * 数量
     */
    private Integer num;
    /**
     * 付款类型
     */
    private Integer paymentType;
    /**
     * 地址id
     */
    private Long addressId;
    /**
     * 商品id
     */
    private Long itemId;
}

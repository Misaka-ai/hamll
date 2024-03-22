
package com.hmall.order.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private Integer num;//数量

    private Integer paymentType;//付款方式

    private Long addressId;//收货人地址ID

    private Long itemId;//商品ID
}

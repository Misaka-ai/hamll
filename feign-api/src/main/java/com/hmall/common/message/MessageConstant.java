package com.hmall.common.message;



public class MessageConstant {


    /**
     * 订单状态,1、未付款 2、已付款,未发货 3、已发货,未确认 4、确认收货，交易成功 5、交易取消，订单关闭 6、交易结束
     */
    public static final Integer NON_PAYMENT = 1;
    public static final Integer PAID_NOT_SHIPPED = 2;
    public static final Integer SHIPPED_NOT_CONFIRMED = 3;
    public static final Integer CONFIRMED_AND_SUCCEED = 4;
    public static final Integer CANCELED_AND_CLOSED = 5;
    public static final Integer SHOPPING_IS_FINISHED = 6;
}

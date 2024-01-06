package com.hmall.constant;

/**
 * MQ常量
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/26 19:00:12
 */
public interface MqConst {

    /**
     * 商品交换机名称
     */
    String ITEM_DIRECT = "item.direct";
    /**
     * 商品插入更新队列
     */
    String ITEM_INSERT_UPDATE_QUEUE = "item.insert.queue";

    /**
     * 商品删除队列
     */
    String ITEM_DELETE_QUEUE = "item.delete.queue";

    /**
     * 商品插入更新
     */
    String ITEM_INSERT_UPDATE = "item.insert";

    /**
     * 商品删除
     */
    String ITEM_DELETE = "item.delete";

    /**
     * 直接订购ttl
     */
    String ORDER_TTL_DIRECT = "order.ttl.direct";
    /**
     * 订单ttl队列
     */
    String ORDER_TTL_QUEUE = "order.ttl.queue";
    /**
     * 订单ttl
     */
    String ORDER_TTL = "order.ttl";

}

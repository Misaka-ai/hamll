package com.hamll.search.vo;

import lombok.Data;

@Data
public class ItemIndexVO {
    private Long id;//商品id
    private String name;//商品名称
    private Long price;//价格（分）
    private Integer stock;//库存数量
    private String image;//商品图片
    private String category;//分类名称
    private String brand;//品牌名称
    private Integer sold;//销量

}

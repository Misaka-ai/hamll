package com.hmall.pojo;


import lombok.Data;


import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDoc {

    private String name;//商品名称
    private String brand;//品牌

    private String category;//分类

    private Long price;//价格

    private Integer sold;//销售数量

    private Long id;//商品ID

    private String image;//图片

    private Integer commentCount;//评价数

    private List<String> suggestion;

    private Boolean isAD;//是否为广告

    public ItemDoc(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.price = item.getPrice();
        this.image = item.getImage();
        this.category = item.getCategory();
        this.brand = item.getBrand();
        this.sold = item.getSold();
        this.commentCount = item.getCommentCount();
        this.suggestion = new ArrayList<>();
        suggestion.add(this.name);
        suggestion.add(this.brand);
    }
}

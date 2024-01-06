package com.hmall.index;

import com.hmall.pojo.Item;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 商品索引库实体
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/26 17:56:08
 */
@Data
@NoArgsConstructor
public class ItemIndex {
    /**
     * id
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 价格
     */
    private Long price;
    /**
     * 库存
     */
    private Integer stock;
    /**
     *
     */
    private String image;
    /**
     * 类别
     */
    private String category;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 已售
     */
    private Integer sold;
    /**
     * 评论计数
     */
    private Integer commentCount;
    /**
     * 是广告
     */
    private Boolean isAD;
    /**
     * 搜索补全 品牌和分类
     */
    private List<String> suggestion;

    public ItemIndex(Item item) {
        this.setId(item.getId());
        this.setName(item.getName());
        this.setPrice(item.getPrice());
        this.setStock(item.getStock());
        this.setImage(item.getImage());
        this.setCategory(item.getCategory());
        this.setBrand(item.getBrand());
        this.setSold(item.getSold());
        this.setCommentCount(item.getCommentCount());
        this.setIsAD(item.getIsAD());
        this.setSuggestion(Arrays.asList(this.brand, this.category));
    }

    public final static String INDEX_NAME = "item";
    public final static String SUGGESTION_NAME = "suggestion";
    public final static String AGGREGATION_CATEGORY = "aggregationCategory";
    public final static String AGGREGATION_BRAND = "aggregationBrand";
}

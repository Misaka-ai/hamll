package com.hmall.dto;

import lombok.Data;

/**
 * 搜索DTO
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/26 21:59:45
 */
@Data
public class SearchDTO {

    /**
     * 钥匙
     */
    private String key;
    /**
     * 类别
     */
    private String category;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 最低价格
     */
    private Integer minPrice;
    /**
     * 最高价格
     */
    private Integer maxPrice;
    /**
     * 页面
     */
    private Integer page;
    /**
     * 尺寸
     */
    private Integer size;
    /**
     * 排序依据
     */
    private String sortBy;
}

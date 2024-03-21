package com.hamll.search.dto;


import lombok.Data;

@Data
public class SearchDTO {
    private String key;//关键字
    private String category;//分类
    private String brand;//品牌
    private Integer minPrice;//最低价格
    private Integer maxPrice;//最高价格
    private Integer page;
    private Integer size;
    private String sortBy;//排序依据


}

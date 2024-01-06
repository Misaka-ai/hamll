package com.hmall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 分页结果对象
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO<T> {
    /**
     * 总条数
     */
    private Long total = 10L;
    /**
     * 当前页数据
     */
    private List<T> list = Collections.emptyList();
}

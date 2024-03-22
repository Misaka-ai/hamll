package com.hmall.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.common.dto.PageDTO;
import com.hmall.pojo.Item;

public interface IItemService extends IService<Item> {
    /*
     * 分页查询
     * */
    PageDTO<Item> pageQuery(Integer page, Integer size);

    /*
     * 更具id查询商品详情
     * */
    Item getByOneId(Long id);

    /*
     *根据id上盖商品状态
     * */
    void updateStatus(Long id, Integer status);

    /*
     *根据id删除商品
     * */
    void deleteItenById(Long id);

    /*
     * 修改商品
     * */
    void updateItem(Item item);

    /*
     * 新增商品
     * */
    void insertItem(Item item);
}

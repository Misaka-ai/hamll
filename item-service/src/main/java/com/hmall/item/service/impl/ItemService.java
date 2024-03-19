package com.hmall.item.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.dto.PageDTO;
import com.hmall.item.mapper.ItemMapper;
import com.hmall.item.pojo.Item;
import com.hmall.item.service.IItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ItemService extends ServiceImpl<ItemMapper, Item> implements IItemService {
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public PageDTO<Item> pageQuery(Integer page, Integer size) {
        Page<Item> itemPage = new Page<>(page, size);
        itemMapper.selectPage(itemPage, null);
        PageDTO<Item> itemPageDTO = new PageDTO<>();
        itemPageDTO.setTotal(itemPage.getTotal());
        itemPageDTO.setList(itemPage.getRecords());
        return itemPageDTO;
    }

    @Override
    public Item getByOneId(Long id) {
        return itemMapper.selectById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Item item = new Item();
        item.setId(id);
        item.setStatus(status);
        itemMapper.updateById(item);
    }

    @Override
    public void deleteItenById(Long id) {
        itemMapper.deleteById(id);
    }
}

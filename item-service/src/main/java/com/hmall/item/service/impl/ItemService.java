package com.hmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.dto.PageDTO;
import com.hmall.item.mapper.ItemMapper;
import com.hmall.item.pojo.Item;
import com.hmall.item.pojo.ItemDoc;
import com.hmall.item.service.IItemService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class ItemService extends ServiceImpl<ItemMapper, Item> implements IItemService {
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public PageDTO<?> pageQuery(Integer page, Integer size) {
        Page<Item> itemPage = new Page<>(page, size);
        itemMapper.selectPage(itemPage, null);
        PageDTO<Item> itemPageDTO = new PageDTO<>();
        //将分页查到数据放到elsticsearch中
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

    @Override
    public void updateItem(Item item) {
        item.setUpdateTime(new Date());
        itemMapper.updateById(item);
    }

    @Override
    public void insertItem(Item item) {
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        itemMapper.insert(item);
    }
}

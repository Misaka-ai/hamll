package com.hmall.item.web;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.constant.MqConst;
import com.hmall.dto.PageDTO;
import com.hmall.item.service.IItemService;
import com.hmall.pojo.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * 商品控制器
 *
 * @author liudo
 * @date 2023/08/25
 */
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final IItemService itemService;

    private final RabbitTemplate rabbitTemplate;

    /**
     * 分页查询商品列表
     *
     * @param page 页面
     * @param size 尺寸
     * @return {@link PageDTO}<{@link Item}>
     */
    @GetMapping("/list")
    public PageDTO<Item> list(@RequestParam(defaultValue = "1") Long page,
                              @RequestParam(defaultValue = "10") Long size) {
        Page<Item> itemPage = new Page<>(page, size);
        itemPage.addOrder(OrderItem.desc("update_time"));
        itemService.page(itemPage);
        return new PageDTO<>(itemPage.getTotal(), itemPage.getRecords());

    }

    /**
     * 根据ID查询详情
     *
     * @param id id
     * @return {@link Item}
     */
    @GetMapping("/{id}")
    public Item getById(@PathVariable Long id) {
        return itemService.getById(id);
    }

    /**
     * 新增商品
     *
     * @param item 项目
     */
    @PostMapping
    public void create(@RequestBody Item item) {
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        itemService.save(item);
        // 向ES同步
        rabbitTemplate.convertAndSend(MqConst.ITEM_DIRECT, MqConst.ITEM_INSERT_UPDATE, item.getId());
    }

    /**
     * 按id更新状态
     *
     * @param id     id
     * @param status 状态
     */
    @PutMapping("/status/{id}/{status}")
    public void updateStatusById(@PathVariable Long id,
                                 @PathVariable Integer status) {
        Item item = new Item();
        item.setStatus(status);
        item.setId(id);
        item.setUpdateTime(new Date());
        itemService.updateById(item);
        // 上架和下架对于ES是新增和删除
        if (Objects.equals(Item.Status.NORMAL.status, status)) {
            rabbitTemplate.convertAndSend(MqConst.ITEM_DIRECT, MqConst.ITEM_INSERT_UPDATE, item.getId());
        }
        if (Objects.equals(Item.Status.OFF_SHELF.status, status)) {
            rabbitTemplate.convertAndSend(MqConst.ITEM_DIRECT, MqConst.ITEM_DELETE, item.getId());
        }
    }

    /**
     * 按id更新
     *
     * @param item 项目
     */
    @PutMapping
    public void updateById(@RequestBody Item item) {
        item.setUpdateTime(new Date());
        itemService.updateById(item);
        rabbitTemplate.convertAndSend(MqConst.ITEM_DIRECT, MqConst.ITEM_INSERT_UPDATE, item.getId());
    }

    /**
     * 按id删除
     *
     * @param id id
     */
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        itemService.removeById(id);
        rabbitTemplate.convertAndSend(MqConst.ITEM_DIRECT, MqConst.ITEM_DELETE, id);
    }

    /**
     * 更新库存批次
     *
     * @param updateItemList 项目清单
     */
    @PutMapping("/stockBatch")
    public void updateStockBatch(@RequestBody List<Item> updateItemList) {
        List<Long> itemIds = updateItemList.stream().map(Item::getId)
                .collect(Collectors.toList());
        List<Item> itemExistList = itemService.listByIds(itemIds);
        itemExistList.forEach(itemExist -> {
            Optional<Item> optionalItem = updateItemList.stream()
                    .filter(updateItem -> Objects.equals(updateItem.getId(), itemExist.getId()))
                    .findAny();
            // 库存进行还原
            optionalItem.ifPresent(item -> {
                itemExist.setStock(itemExist.getStock() + item.getStock());
                itemExist.setUpdateTime(new Date());
            });
        });
        itemService.updateBatchById(itemExistList);
    }
}

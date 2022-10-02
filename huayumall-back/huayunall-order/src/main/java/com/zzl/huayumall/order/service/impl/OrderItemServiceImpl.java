package com.zzl.huayumall.order.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.order.dao.OrderItemDao;
import com.zzl.huayumall.order.entity.OrderItemEntity;
import com.zzl.huayumall.order.service.OrderItemService;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据订单号批量查询
     * @param orderSnList
     * @return
     */
    @Override
    public List<OrderItemEntity> queryOrderItemByOrderSn(List<String> orderSnList) {
        return baseMapper.queryOrderItemByOrderSn(orderSnList);
    }

}

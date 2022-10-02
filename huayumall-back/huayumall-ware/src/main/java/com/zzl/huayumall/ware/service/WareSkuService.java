package com.zzl.huayumall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.to.mq.OrderTo;
import com.zzl.common.to.mq.StockLockTo;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.ware.entity.WareSkuEntity;
import com.zzl.huayumall.ware.vo.WareLockResultVo;
import com.zzl.huayumall.ware.vo.WareSkuLockVo;
import com.zzl.huayumall.ware.vo.hasStockSkuVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:57:18
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Integer skuNum, Long wareId);

    List<hasStockSkuVo> selectWareHasSkuStock(List<Long> skuId);

    Boolean orderWareLock(WareSkuLockVo vo);

    void unlockStock(StockLockTo stockLockTo);

    void unlockLockStock(Long skuId, Long wareId, int num, Long orderDetailId);

    void unlockStock(OrderTo orderTo);
}


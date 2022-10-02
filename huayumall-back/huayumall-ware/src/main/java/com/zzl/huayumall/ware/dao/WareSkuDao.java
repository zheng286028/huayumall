package com.zzl.huayumall.ware.dao;

import com.zzl.huayumall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:57:18
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void updateWareSkustock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum, @Param("wareId") Long wareId);

    Long selectWareHasSkuStock(Long skuId);

    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId,@Param("count") Integer count);

    Long skuWareLock(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("num") Integer num);

    int liftLockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") int num);
}

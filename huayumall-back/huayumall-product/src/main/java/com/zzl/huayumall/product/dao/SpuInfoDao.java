package com.zzl.huayumall.product.dao;

import com.zzl.huayumall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzl.huayumall.product.vo.Attr;
import com.zzl.huayumall.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    List<SpuInfoEntity> selectSpuInfoByDynamic(Map<String, Object> params);

    void updateStatusById(@Param("spuId") Long spuId, @Param("upStatus") int upStatus);

    List<SkuItemSaleAttrVo> selectSkuBySpuId(Long spuId);
}

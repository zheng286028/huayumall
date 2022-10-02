package com.zzl.huayumall.ware.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.ware.dao.WareOrderTaskDetailDao;
import com.zzl.huayumall.ware.entity.WareOrderTaskDetailEntity;
import com.zzl.huayumall.ware.service.WareOrderTaskDetailService;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskDetailEntity> page = this.page(
                new Query<WareOrderTaskDetailEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskDetailEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据库存工作单id查询为解锁的库存工作单详情
     * @param id
     * @return
     */
    @Override
    public List<WareOrderTaskDetailEntity> queryNoUnlockStockByTaskId(Long id) {
        return baseMapper.queryNoUnlockStockByTaskId(id);
    }


}

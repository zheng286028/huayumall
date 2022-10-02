package com.zzl.huayumall.ware.service.impl;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.ware.dao.PurchaseDetailDao;
import com.zzl.huayumall.ware.entity.PurchaseDetailEntity;
import com.zzl.huayumall.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    /**
     *  key: '华为',//检索关键字
     *    status: 0,//状态
     *    wareId: 1,//仓库id
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(item->{
               wrapper.eq("purchase_id",key).or().like("sku_id",key);
            });
        }
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
        }
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId) && new BigDecimal(wareId).compareTo(BigDecimal.ZERO)>0){
            wrapper.eq("ware_id",wareId);
        }
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),wrapper

        );

        return new PageUtils(page);
    }

    /**
     * 根据采购单id查询采购需求
     * @param purchaseIds
     * @return
     */
    @Override
    public List<PurchaseDetailEntity> updateStatusByPurchaseId(Long purchaseIds) {
        List<PurchaseDetailEntity> entityList = this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", purchaseIds));
        return entityList;
    }
}

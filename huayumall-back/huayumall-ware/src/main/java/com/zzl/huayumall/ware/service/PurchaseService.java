package com.zzl.huayumall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.ware.entity.PurchaseEntity;
import com.zzl.huayumall.ware.vo.mergeVo;
import com.zzl.huayumall.ware.vo.purchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:57:18
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageAndunReceive(Map<String, Object> params);

    void MergePurchaseArrivePurchaseOrder(mergeVo vo);

    void receivedPurchaseByPurchaseIds(List<Long> purchaseIds);

    void purchaseDone(purchaseDoneVo vo);
}


package com.zzl.huayumall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zzl.huayumall.ware.vo.mergeVo;
import com.zzl.huayumall.ware.vo.purchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zzl.huayumall.ware.entity.PurchaseEntity;
import com.zzl.huayumall.ware.service.PurchaseService;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.R;

import javax.validation.Valid;


/**
 * 采购信息
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:57:18
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;


    @PostMapping("/done")
    public R PurchaseDone(@Valid @RequestBody purchaseDoneVo vo){
        purchaseService.purchaseDone(vo);
        return R.ok();
    }

    @PostMapping("/received")
    public R receivedPurchase(@RequestBody List<Long> purchaseIds){
        if(!purchaseIds.isEmpty() && purchaseIds.size()>0){
            purchaseService.receivedPurchaseByPurchaseIds(purchaseIds);
            return R.ok();
        }
        return R.error();
    }


    /**
     * 合并采购需求到采购单
     * @param vo
     * @return
     */
    @PostMapping("/merge")
    public R purchaseMerge(@Valid @RequestBody mergeVo vo){
        purchaseService.MergePurchaseArrivePurchaseOrder(vo);
        return R.ok();
    }
    /**
     * 查询采购单状态为新建/已领取的
     * @param params
     * @return
     */
    @RequestMapping("/unreceive/list")
    //@RequiresPermissions("ware:purchase:list")
    public R unReceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageAndunReceive(params);

        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

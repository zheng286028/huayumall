package com.zzl.huayumall.ware.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.zzl.common.exception.BizCodeEnum;
import com.zzl.common.to.mq.StockDetailTo;
import com.zzl.common.to.mq.StockLockTo;
import com.zzl.huayumall.ware.entity.WareOrderTaskDetailEntity;
import com.zzl.huayumall.ware.entity.WareOrderTaskEntity;
import com.zzl.huayumall.ware.exception.NoStockException;
import com.zzl.huayumall.ware.feign.OrderFeignService;
import com.zzl.huayumall.ware.service.WareOrderTaskDetailService;
import com.zzl.huayumall.ware.service.WareOrderTaskService;
import com.zzl.huayumall.ware.vo.*;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zzl.huayumall.ware.entity.WareSkuEntity;
import com.zzl.huayumall.ware.service.WareSkuService;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.R;


/**
 * 商品库存
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:57:18
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private OrderFeignService orderFeignService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 根据skuId查询是否还有库存
     *
     * @param skuId
     * @return
     */
    @PostMapping("/hasSkuStock")
    public List<hasStockSkuVo> selectWareHasSkuStock(@RequestBody List<Long> skuId) {
        List<hasStockSkuVo> vos = wareSkuService.selectWareHasSkuStock(skuId);
        return vos;
    }

    /**
     * 库存锁定
     *
     * @return
     */
    @PostMapping("/ware/lock")
    public R orderWareLock(@RequestBody WareSkuLockVo vo) {
        try {
            wareSkuService.orderWareLock(vo);
            return R.ok();
        } catch (NoStockException e) {
            return R.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(), BizCodeEnum.NO_STOCK_EXCEPTION.getMsg());
        }
    }
}

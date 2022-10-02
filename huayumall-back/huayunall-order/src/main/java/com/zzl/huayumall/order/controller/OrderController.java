package com.zzl.huayumall.order.controller;

import java.util.Arrays;
import java.util.Map;

import com.zzl.huayumall.order.vo.SubmitOrderResponseVo;
import com.zzl.huayumall.order.vo.SubmitOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zzl.huayumall.order.entity.OrderEntity;
import com.zzl.huayumall.order.service.OrderService;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.R;



/**
 * 订单
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:48:59
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 根据订单号查询订单状态
     * @param orderSn
     * @return
     */
    @GetMapping("/status/{orderSn}")
    public R queryOrderStatusByOrderSh(@PathVariable("orderSn")String orderSn){
        OrderEntity entity = orderService.queryOrderStatusByOrderSh(orderSn);
        return R.ok().setData(entity);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("order:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("order:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 用户订单页面
     */
    @PostMapping("/listWithItem")
    public R queryOrderWithOrderItem(@RequestBody Map<String, Object> params){
        PageUtils page = orderService.queryOrderWithOrderItem(params);
        return R.ok().put("page", page);
    }

}

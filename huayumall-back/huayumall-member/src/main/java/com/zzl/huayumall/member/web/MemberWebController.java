package com.zzl.huayumall.member.web;

import com.zzl.common.utils.R;
import com.zzl.huayumall.member.feign.OrderFeignService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/20  17:30
 */
@Controller
public class MemberWebController {

    @Resource
    private OrderFeignService orderFeignService;

    /**
     * 用户订单页面
     * @return
     */
    @GetMapping("/OrderPage")
    public String memberOrderPage(@RequestParam(value = "pageNum",defaultValue = "1")int pageNum, Model model){
        //1、查询用户订单和订单详情
        Map<String,Object> params = new HashMap<>(5);
        params.put("page",pageNum+"");
        R r = orderFeignService.queryOrderWithOrderItem(params);
        model.addAttribute("orderData",r);
        System.out.println("查询到的数据为："+r);
        return "orderList";
    }
}

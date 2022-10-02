package com.zzl.huayumall.order.web;

import com.zzl.huayumall.order.service.OrderService;
import com.zzl.huayumall.order.vo.OrderConfirmVo;
import com.zzl.huayumall.order.vo.SubmitOrderResponseVo;
import com.zzl.huayumall.order.vo.SubmitOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/08  18:50
 */
@Controller
public class OrderWebController {
    @Autowired
    private OrderService orderService;

    /**
     * 结算页
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) {
        OrderConfirmVo orderItemVo = null;
        try {
            orderItemVo = orderService.confirmOrder();
            //1、是否选中购物项
            if (orderItemVo.getItems() != null && orderItemVo.getItems().size() > 0) {
                model.addAttribute("confirmOrderData", orderItemVo);
                return "confirm";
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //2、未选中购物项，不能结算
        return "redirect:http://cart.huayumall.com/cart.html";
    }

    /**
     * 提交订单
     */
    @PostMapping("/submitOrder")
    public String submitOrder(SubmitOrderVo vo, Model model, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        SubmitOrderResponseVo submitOrder = orderService.submitOrder(vo);
        if (submitOrder.getCode() != 0) {
            String msg = "";
            //失败,回到结算页
            switch (submitOrder.getCode()) {
                case 1:
                    msg = "订单信息过期，请重新下单";
                    break;
                case 2:
                    msg = "商品价格发生变化，请确认后再次购物";
                    break;
                case 3:
                    msg = "商品库存不足";
                    break;
            }
            return "redirect:http://order.huayumall.com/toTrade";
        }
        model.addAttribute("orderData", submitOrder);
        return "pay";
    }
}

package com.zzl.cart.controller;

import com.zzl.cart.interceptor.CartInterceptor;
import com.zzl.cart.service.CartService;
import com.zzl.cart.to.UserInfo;
import com.zzl.cart.vo.CartItemVo;
import com.zzl.cart.vo.CartVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.jws.WebParam;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 功能描述
 * 购物车功能的Controller
 *
 * @author 郑子浪
 * @date 2022/09/03  20:14
 */
@Controller
@Slf4j
public class CartController {
    @Resource
    private CartService cartService;

    /**
     * 获取当前用户的购物车中的购物项
     * @return
     */
    @GetMapping("/getUserCartItem")
    @ResponseBody
    public List<CartItemVo> getUserCartItem(){
        return cartService.getUserCartItem();
    }

    /**
     * 购物车页面
     *
     * @return
     */
    @RequestMapping("/cart.html")
    public String cartListPage(Model model) {
        CartVo cartVo = null;
        try {
            cartVo = cartService.queryCartData();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        model.addAttribute("cartData", cartVo);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     *
     * @return
     */
    @RequestMapping("/addToCart")
    public String addToCart(@RequestParam("num") int num, @RequestParam("skuId") Long skuId, RedirectAttributes attributes) {
        //1、调用service完成添加
        try {
            cartService.addProductToCart(num, skuId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //2、重定向到购物车页面,addAttribute会将存储的数据拼接到路径上
        attributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.huayumall.com/success.html";
    }

    /**
     * 查询添加的购物项
     *
     * @return
     */
    @RequestMapping("/success.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId, Model model) {
        CartItemVo vo = cartService.selectCartDataBySkuId(skuId);
        model.addAttribute("productItem", vo);
        return "success";
    }

    /**
     * 修改购物车的购物项checked
     *
     * @param skuId
     * @param check
     * @return
     */
    @GetMapping("/cartChecked")
    public String updateCartItemCheckedBySkuId(@RequestParam("skuId")Long skuId, @RequestParam("check")Boolean check) {
        cartService.updateCartItemCheckedBySkuId(skuId, check);
        return "redirect:http://cart.huayumall.com/cart.html";
    }

    /**
     * 修改购物车中的购物项数量
     *
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/cartItemNum")
    public String updateCartItemNumBySkuId(@RequestParam("skuId")Long skuId, @RequestParam("num")int num) {
        cartService.updateCartItemNumBySkuId(skuId,num);
        return "redirect:http://cart.huayumall.com/cart.html";
    }

    /**
     * 删除购物车中的购物项
     * @param skuId
     * @return
     */
    @GetMapping("/deleteCartItem")
    public String deleteCartItemBySkuId(@RequestParam("skuId") Long skuId) {
        cartService.deleteCartItemBySkuId(skuId);
        return "redirect:http://cart.huayumall.com/cart.html";
    }

    /**
     * 根据搜索内容查询购物项
     * @param searchContent
     * @return
     */
    @GetMapping("/searchValue")
    public String queryCartItemByValue(@RequestParam("searchContent")String searchContent,Model model){
        CartVo cartVo = null;
        try {
            cartVo = cartService.queryCartItemByValue(searchContent);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        model.addAttribute("cartData", cartVo);
        return "cartList";
    }
}

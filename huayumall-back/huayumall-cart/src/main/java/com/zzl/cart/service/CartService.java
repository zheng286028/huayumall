package com.zzl.cart.service;

import com.zzl.cart.vo.CartItemVo;
import com.zzl.cart.vo.CartVo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/04  15:33
 */
public interface CartService {

    CartItemVo addProductToCart(int num, Long skuId) throws ExecutionException, InterruptedException;

    CartItemVo selectCartDataBySkuId(Long skuId);

    CartVo queryCartData() throws ExecutionException, InterruptedException;

    void deleteCartByKey(String cartKey);

    void updateCartItemCheckedBySkuId(Long skuId, Boolean check);

    void updateCartItemNumBySkuId(Long skuId, int num);

    void deleteCartItemBySkuId(Long skuId);

    CartVo queryCartItemByValue(String searchContent) throws ExecutionException, InterruptedException;

    List<CartItemVo> getUserCartItem();
}

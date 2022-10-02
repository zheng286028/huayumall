package com.zzl.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zzl.cart.feign.ProductService;
import com.zzl.cart.interceptor.CartInterceptor;
import com.zzl.cart.service.CartService;
import com.zzl.cart.to.UserInfo;
import com.zzl.cart.vo.CartItemVo;
import com.zzl.cart.vo.CartVo;
import com.zzl.cart.vo.SkuInfo;
import com.zzl.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/04  15:33
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProductService productService;
    @Autowired
    private ThreadPoolExecutor executor;
    //redisKey的前缀
    public static final String CART_PREFIX = "huayumall:catr:";

    /**
     * 添加购物项
     *
     * @param num
     * @param skuId
     * @return
     */
    public CartItemVo addProductToCart(int num, Long skuId) throws ExecutionException, InterruptedException {
        //1、判断是否登录和连接redis
        BoundHashOperations<String, Object, Object> ops = getCart();
        //1.1、根据商品id查询是否存在
        String resp = (String) ops.get(skuId.toString());
        if (StringUtils.isEmpty(resp)) {
            //1.2、该商品没添加过
            //2、根据skuId远程查询skuInfo
            CartItemVo vo = new CartItemVo();
            CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
                R r = productService.getSkuInfo(skuId);
                SkuInfo skuInfo = (SkuInfo) r.getData("skuInfo", new TypeReference<SkuInfo>() {
                });
                //3、封装信息
                vo.setCount(num);
                vo.setImage(skuInfo.getSkuDefaultImg());
                vo.setPrice(skuInfo.getPrice());
                vo.setTitle(skuInfo.getSkuTitle());
                vo.setSkuId(skuInfo.getSkuId());
            }, executor);
            //4、远程查询对应的销售属性
            CompletableFuture<Void> SkuAttrInfo = CompletableFuture.runAsync(() -> {
                List<String> skuAttrBySkuId = productService.getSkuAttrBySkuId(skuId);
                vo.setSkuAttr(skuAttrBySkuId);
            }, executor);
            //5、等待全部任务完成
            CompletableFuture.allOf(getSkuInfo, SkuAttrInfo).get();
            //6、存储到redis
            String s = JSON.toJSONString(vo);
            ops.put(skuId.toString(), s);
            return vo;
        }
        //1.3、商品已经存在，修改数量即可
        CartItemVo itemVo = JSON.parseObject(resp, CartItemVo.class);
        itemVo.setCount(itemVo.getCount() + num);
        //1.4、修改redis数据
        ops.put(skuId.toString(), JSON.toJSONString(itemVo));
        return itemVo;
    }

    /**
     * 查询添加的购物项
     *
     * @param skuId
     * @return
     */
    @Override
    public CartItemVo selectCartDataBySkuId(Long skuId) {
        //根据key查询购物项
        BoundHashOperations<String, Object, Object> cart = getCart();
        String ops = (String) cart.get(skuId.toString());
        CartItemVo itemVo = null;
        if (ops != null) {
            //反序列化
            itemVo = JSON.parseObject(ops, CartItemVo.class);
            return itemVo;
        }
        return itemVo;
    }

    /**
     * 查询购物车数据
     *
     * @return
     */
    @Override
    public CartVo queryCartData() throws ExecutionException, InterruptedException {
        //1、是否登录
        UserInfo user = CartInterceptor.threadLocal.get();
        CartVo cartVo = new CartVo();
        if (user.getUserId() != null) {
            //3、登录
            String cartKey = CART_PREFIX + user.getUserKey();
            // 3.1、查询是否还有临时购物项
            List<CartItemVo> cartItemVos = selectCartByCartKey(cartKey);
            if (cartItemVos != null && cartItemVos.size() > 0) {
                //3.1、遍历，合并到登录购物项
                for (CartItemVo itemVo : cartItemVos) {
                    addProductToCart(itemVo.getCount(), itemVo.getSkuId());
                }
                //3.1、清空临时购物项
                deleteCartByKey(cartKey);
            }
            //3.2、查询登录的购物项
            List<CartItemVo> userCart = selectCartByCartKey(CART_PREFIX + user.getUserId());
            if (userCart != null && userCart.size() > 0) {
                //3.3、收集
                cartVo.setCartItemVos(userCart);
            }
        } else {
            //2、未登录，查询对应数据
            List<CartItemVo> cartItemVos = selectCartByCartKey(CART_PREFIX + user.getUserKey());
            if (cartItemVos != null && cartItemVos.size() > 0) {
                //封装
                cartVo.setCartItemVos(cartItemVos);
            }
        }
        //4、返回
        return cartVo;
    }

    /**
     * 根据key动态查询购物车
     */
    private List<CartItemVo> selectCartByCartKey(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> cartItem = hashOps.values();
        //是否有购物项
        if (cartItem != null && cartItem.size() > 0) {
            //遍历购物项
            List<CartItemVo> collect = cartItem.stream().map((obj) -> {
                //转成Str
                String cartStr = (String) obj;
                //反序列化
                return JSON.parseObject(cartStr, CartItemVo.class);
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 根据登录状态动态连接hash
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCart() {
        //1、是否已经登录
        UserInfo userInfo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfo.getUserId() != null) {
            //登录
            cartKey = CART_PREFIX + userInfo.getUserId();
        } else {
            //未登录
            cartKey = CART_PREFIX + userInfo.getUserKey();
        }
        //2、连接reids指定的key的hash
        BoundHashOperations<String, Object, Object> redis = stringRedisTemplate.boundHashOps(cartKey);
        return redis;
    }

    /**
     * 根据key清空购物车
     *
     * @param cartKey
     */
    @Override
    public void deleteCartByKey(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    /**
     * 修改购物车中的购物项checked
     *
     * @param skuId
     * @param check
     */
    @Override
    public void updateCartItemCheckedBySkuId(Long skuId, Boolean check) {
        //1、查询购物项
        CartItemVo cartItemVo = selectCartDataBySkuId(skuId);
        //2、修改checked
        cartItemVo.setCheck(check);
        //3、修改redis
        BoundHashOperations<String, Object, Object> cart = getCart();
        //4、序列化
        String s = JSON.toJSONString(cartItemVo);
        cart.put(skuId.toString(), s);
    }

    /**
     * 修改购物车中的购物项数量
     *
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public void updateCartItemNumBySkuId(Long skuId, int num) {
        //1、查询购物项
        CartItemVo cartItemVo = selectCartDataBySkuId(skuId);
        //2、修改checked
        cartItemVo.setCount(num);
        //3、修改redis
        BoundHashOperations<String, Object, Object> cart = getCart();
        //4、序列化
        String s = JSON.toJSONString(cartItemVo);
        cart.put(skuId.toString(), s);
    }

    /**
     * 删除购物车中的购物项
     *
     * @param skuId
     */
    @Override
    public void deleteCartItemBySkuId(Long skuId) {
        BoundHashOperations<String, Object, Object> cart = getCart();
        //1、先查询该购物项是否存在
        CartItemVo cartItemVo = selectCartDataBySkuId(skuId);
        if (cartItemVo != null) {
            cart.delete(skuId.toString());
        }
    }

    /**
     * 根据搜索内容查询购物项
     *
     * @param searchContent
     * @return
     */
    @Override
    public CartVo queryCartItemByValue(String searchContent) throws ExecutionException, InterruptedException {
        //1、内容是否为空
        if (searchContent == null || searchContent == "") {
            //1.1、查询全部
            CartVo cartVo = queryCartData();
            return cartVo;
        }
        //2、根据内容查询
        CartVo cart = new CartVo();
        CartVo cartVo = queryCartData();
        List<CartItemVo> cartItem = new ArrayList<>();
        for (CartItemVo cartItemVo : cartVo.getCartItemVos()) {
            //2.1、是否包含
            if (cartItemVo.getTitle().contains(searchContent)) {
                //2.2、包含，收集
                cartItem.add(cartItemVo);
            }
        }
        cart.setCartItemVos(cartItem);
        return cart;
    }

    /**
     * 获取当前用户的购物车中的购物项
     *
     * @return
     */
    @Override
    public List<CartItemVo> getUserCartItem() {
        //1、拼接查询的key
        UserInfo userInfo = CartInterceptor.threadLocal.get();
        //1.1、登录，根据key查询购物项
        /**
         * 明明已经登录到了这里却没有登录，数据丢失了？
         * 原因就是user-key是存储在浏览器的，只有浏览器访问时才会带上，而这个方法
         *      是通过feign远程调用的，所以没有user-key
         * 解决办法，要feign远程调用时，携带上cookie信息
         */
        List<CartItemVo> cartItems = selectCartByCartKey(CART_PREFIX + userInfo.getUserId());
        //1.2、空判断
        if (cartItems != null && cartItems.size() > 0) {
            //1.3、只有被选中的才进行return
            List<CartItemVo> CartItem = cartItems.stream().filter(item -> {
                return item.getCheck();
            }).map(item -> {
                //1.4、远程查询product获取该sku最新的价格
                /**
                 * todo 循环里远程查询价格，效率低下
                 * 可以先获取被选中的skuId，在批量查询对应的skuInfo
                 * 这里在获取带被选中的skuInfo信息，在存储到CartItemVo里，但这样需要遍历大量集合
                 */
                BigDecimal newPrice = productService.getProductPriceBySkuId(item.getSkuId());
                item.setPrice(newPrice);
                return item;
            }).collect(Collectors.toList());
            return CartItem;
        }
        return null;
    }
}

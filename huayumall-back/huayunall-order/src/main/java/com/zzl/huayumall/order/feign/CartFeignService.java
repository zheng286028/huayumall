package com.zzl.huayumall.order.feign;

import com.zzl.huayumall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/08  22:51
 */
@FeignClient("huayumall-cart")
public interface CartFeignService {

    @GetMapping("/getUserCartItem")
    List<OrderItemVo> getUserCartItem();
}

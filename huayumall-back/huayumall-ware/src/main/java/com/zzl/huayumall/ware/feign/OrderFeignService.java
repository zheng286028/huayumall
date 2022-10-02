package com.zzl.huayumall.ware.feign;

import com.zzl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/17  21:04
 */
@FeignClient("huayunall-order")
public interface OrderFeignService {
    @GetMapping("/order/order/status/{orderSh}")
    R queryOrderStatusByOrderSh(@PathVariable("orderSh")String orderSh);
}

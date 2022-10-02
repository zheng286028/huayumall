package com.zzl.huayumall.member.feign;

import com.zzl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/20  22:58
 */
@FeignClient("huayunall-order")
public interface OrderFeignService {
    @PostMapping("/order/order/listWithItem")
    R queryOrderWithOrderItem(@RequestBody Map<String, Object> params);
}

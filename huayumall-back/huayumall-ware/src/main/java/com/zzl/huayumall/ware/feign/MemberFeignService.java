package com.zzl.huayumall.ware.feign;

import com.zzl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/10  15:45
 */
@FeignClient("huayumall-member")
public interface MemberFeignService {

    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R getFeright(@PathVariable("id") Long id);
}

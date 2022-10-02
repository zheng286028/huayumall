package com.zzl.huayumall.order.feign;

import com.zzl.huayumall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/08  22:42
 */
@FeignClient("huayumall-member")
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/address/{memberId}")
    List<MemberAddressVo> getMemberAddress(@PathVariable("memberId")Long memberId);
}

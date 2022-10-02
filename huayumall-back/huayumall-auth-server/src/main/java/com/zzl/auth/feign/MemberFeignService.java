package com.zzl.auth.feign;

import com.zzl.auth.vo.SocialLoginVo;
import com.zzl.auth.vo.UserLoginVo;
import com.zzl.auth.vo.UserRegisterVo;
import com.zzl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/14  16:21
 */
@FeignClient("huayumall-member")
public interface MemberFeignService {
    @PostMapping("/register")
    R userRegister(@RequestBody UserRegisterVo vo);

    @PostMapping("/login")
    R userLoginVerify(@RequestBody UserLoginVo vo);

    @PostMapping("/social/login")
    R socialUserRegisterOrLogin(@RequestBody SocialLoginVo socialUserVo);
}

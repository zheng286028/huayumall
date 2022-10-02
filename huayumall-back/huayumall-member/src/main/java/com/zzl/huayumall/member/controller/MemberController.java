package com.zzl.huayumall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.zzl.huayumall.member.feign.couponFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zzl.huayumall.member.entity.MemberEntity;
import com.zzl.huayumall.member.service.MemberService;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.R;

import javax.annotation.Resource;


/**
 * 会员
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:40:26
 */
@RefreshScope //配置刷新
@RestController
@RequestMapping("member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Resource
    private couponFeignService couponFeignService;

    @Value("${name}")
    private String name;
    @Value("${age}")
    private int age;
    @Value("${server.port}")
    private int port;


    //测试配置中心
    @RequestMapping("/nacosConfig")
    public R nacosConfig(){
        return R.ok().put("name",name).put("age",age).put("port",port);
    }

    //测试feign远程连接
    @RequestMapping("/couponMember")
    public R couponMember(){
        R r = couponFeignService.couponMember();
        MemberEntity member = new MemberEntity();
        member.setNickname("王五");
        Object coupon = r.get("coupon");
        return R.ok().put("member",member).put("coupon",coupon);
    }

    /**
     * 列表
     */
    @RequestMapping("/member/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

package com.zzl.huayumall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zzl.common.utils.HttpUtils;
import com.zzl.huayumall.member.entity.MemberLevelEntity;
import com.zzl.huayumall.member.exception.PhoneExistException;
import com.zzl.huayumall.member.exception.UserNameExistException;
import com.zzl.huayumall.member.service.MemberLevelService;
import com.zzl.huayumall.member.vo.SocialLoginVo;
import com.zzl.huayumall.member.vo.SocialUserItemVo;
import com.zzl.huayumall.member.vo.UserLoginVo;
import com.zzl.huayumall.member.vo.UserRegisterVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.member.dao.MemberDao;
import com.zzl.huayumall.member.entity.MemberEntity;
import com.zzl.huayumall.member.service.MemberService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Resource
    private MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 用户注册
     *
     * @param vo
     */
    @Override
    public void saveUserRegister(UserRegisterVo vo) throws PhoneExistException, UserNameExistException {
        MemberEntity member = new MemberEntity();
        //查询默认会员等级
        MemberLevelEntity entity = memberLevelService.selectDefaultVipGrade();
        member.setLevelId(entity.getId());

        //查询手机，账号是否存在
        Integer email = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("email", vo.getEmail()));
        if (email > 0) {
            //手机存在，抛异常
            throw new PhoneExistException();
        }
        Integer userName = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", vo.getUserName()));
        if (userName > 0) {
            //账号存在，抛异常
            throw new UserNameExistException();
        }
        //密码加密
        BCryptPasswordEncoder md = new BCryptPasswordEncoder();
        String encode = md.encode(vo.getPassWord());
        //保存注册信息
        member.setEmail(vo.getEmail());
        member.setUsername(vo.getUserName());
        member.setCreateTime(new Date());
        member.setPassword(encode);
        //保存
        baseMapper.insert(member);
    }

    /**
     * 用户登录验证
     *
     * @param vo
     * @return
     */
    @Override
    public MemberEntity userLoginVerify(UserLoginVo vo) {
        //根据用户名或者手机号查询该用户
        MemberEntity entity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", vo.getUserName()).or().eq("email", vo.getUserName()));
        if (entity == null) {
            //查询不到该用户
            return null;
        }
        //校验密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(vo.getPassWord(), entity.getPassword());
        if (matches) {
            return entity;
        }
        return null;
    }

    /**
     * gitee社交登录或注册
     *
     * @param socialUserVo
     * @return
     */
    @Override
    @Transactional
    public MemberEntity socialUserRegisterOrLogin(SocialLoginVo socialUserVo) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        //1、通过令牌获取该用户信息,https://gitee.com/api/v5/user?access_token=437d183ef0de
        map.put("access_token", socialUserVo.getAccessToken());
        HttpResponse socialUser = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "", new HashMap<>(), map);
        if (socialUser.getStatusLine().getStatusCode() == 200) {
            //转成json
            String json = EntityUtils.toString(socialUser.getEntity());
            //转成对象
            SocialUserItemVo userItemVo = JSON.parseObject(json, SocialUserItemVo.class);
            //2、查询该用户是否存在
//            MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("social_uid", userItemVo.getId()));
            MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", userItemVo.getId()));
            if (memberEntity != null) {
                //存在，修改令牌信息及过期时间
                MemberEntity member = new MemberEntity();
                member.setAccessToken(socialUserVo.getAccessToken());
                member.setExpiresIn(socialUserVo.getExpiresIn());
                member.setId(memberEntity.getId());
                //修改
                this.updateById(member);
                //将查询出来的对象更新令牌信息，返回
                memberEntity.setExpiresIn(socialUserVo.getExpiresIn());
                memberEntity.setAccessToken(socialUserVo.getAccessToken());
                return memberEntity;
            } else {
                //3、不存在，注册
                //封装信息
                MemberEntity member = new MemberEntity();
                member.setUsername(userItemVo.getLogin());
                member.setNickname(userItemVo.getName());
                member.setCreateTime(userItemVo.getCreatedAt());
                member.setExpiresIn(socialUserVo.getExpiresIn());
                member.setAccessToken(socialUserVo.getAccessToken());
                member.setSocialUid(userItemVo.getId());
                member.setEmail(userItemVo.getEmail());
                //4、查询默认会员
                MemberLevelEntity levelEntity = memberLevelService.selectDefaultVipGrade();
                member.setLevelId(levelEntity.getId());
                //保存
                this.save(member);
                return member;
            }
        }
        //查询不到用户信息
        return null;
    }

}

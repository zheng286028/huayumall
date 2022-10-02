package com.zzl.huayumall.coupon.service.impl;

import com.zzl.huayumall.coupon.entity.SeckillSkuRelationEntity;
import com.zzl.huayumall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.coupon.dao.SeckillSessionDao;
import com.zzl.huayumall.coupon.entity.SeckillSessionEntity;
import com.zzl.huayumall.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {
    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询最近三天的商品
     *
     * @return
     */
    @Override
    public List<SeckillSessionEntity> seckillSessionService() {
        //1、查询最近三天的活动场次
        List<SeckillSessionEntity> sessionEntities = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime(), endTime()));
        //2、查出对应的sku
        if (sessionEntities != null && sessionEntities.size() > 0) {
            return sessionEntities.stream().map(session -> {
                List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", session.getId()));
                session.setRelationEntities(relationEntities);
                return session;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 开始时间
     *
     * @return
     */
    public String startTime() {
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        return LocalDateTime.of(now, min).format(DateTimeFormatter.ofPattern(" yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 结束时间
     *
     * @return
     */
    public String endTime() {
        LocalDate now = LocalDate.now();
        LocalDate days = now.plusDays(2);
        LocalTime max = LocalTime.MAX;
        return LocalDateTime.of(days, max).format(DateTimeFormatter.ofPattern(" yyyy-MM-dd HH:mm:ss"));
    }

}

package com.zzl.huayumall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zzl.common.utils.R;
import com.zzl.huayumall.seckill.feign.CouponFeignService;
import com.zzl.huayumall.seckill.feign.ProductFeignService;
import com.zzl.huayumall.seckill.service.SeckillService;
import com.zzl.huayumall.seckill.vo.SeckillSessionWithSkus;
import com.zzl.huayumall.seckill.to.SeckillSkuRedisTo;
import com.zzl.huayumall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/23  16:23
 */
@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private RedissonClient redissonClient;

    private static final String SESSION_CACHE_PREFIX = "seckill:sessions:";

    private static final String SKUSECKILL_CACHE_PREFIX = "seckill:skus";

    private static final String SECKILL_STOCK_SEMAPHORE = "seckill:stock:";

    private static final String UPLOAD_LOCK = "seckill:upload:lock";

    /**
     * 秒杀商品上架
     */
    @Override
    public void upLoadSeckillSkuLatestThreeDays() {
        //1、保证多个服务之间只能上架秒杀商品一次
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        lock.lock();
        try {
            //2、远程查询需要秒杀的数据
            R r = couponFeignService.getLatesThreeDaySession();
            if (r.getCode() == 0) {
                List<SeckillSessionWithSkus> data = (List<SeckillSessionWithSkus>) r.getData(new TypeReference<List<SeckillSessionWithSkus>>() {
                });
                if (data != null && data.size() > 0) {
                    //1、存储活动信息
                    saveSessionInfos(data);
                    //2、存储商品信息
                    saveSessionSkuInfos(data);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 缓存活动信息
     */
    public void saveSessionInfos(List<SeckillSessionWithSkus> seckillSessionWithSkuses) {
        seckillSessionWithSkuses.forEach(session -> {
            long stateTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = SESSION_CACHE_PREFIX + stateTime + "_" + endTime;
            //1、获取缓存的数据
            List<String> collect = session.getRelationEntities().stream().map(item -> item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
            //2、缓存
            //2.1、先查询是否有缓存
            Boolean aBoolean = stringRedisTemplate.hasKey(key);
            if (Boolean.FALSE.equals(aBoolean)) {
                stringRedisTemplate.opsForList().leftPushAll(key, collect);
            }
        });
    }

    /**
     * 缓存活动的商品信息
     */
    public void saveSessionSkuInfos(List<SeckillSessionWithSkus> seckillSessionWithSkuses) {
        seckillSessionWithSkuses.forEach(session -> {
            //缓存商品
            session.getRelationEntities().forEach(item -> {
                //1、连接hash
                BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(SKUSECKILL_CACHE_PREFIX);
                Boolean aBoolean = hashOps.hasKey(item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString());
                if (Boolean.FALSE.equals(aBoolean)) {
                    //2、秒杀的基本信息
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    BeanUtils.copyProperties(item, redisTo);
                    //3、秒杀的商品信息
                    R r = productFeignService.getSkuInfo(item.getSkuId());
                    if (r.getCode() == 0) {
                        SkuInfoVo skuInfo = (SkuInfoVo) r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuinfo(skuInfo);
                    }
                    //4、秒杀的时间信息
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());
                    //5、商品的随机码
                    redisTo.setRandomCode(UUID.randomUUID().toString().replace("-", ""));
                    //6、商品信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SECKILL_STOCK_SEMAPHORE + redisTo.getRandomCode());
                    semaphore.trySetPermits(item.getSeckillCount().intValue());
                    //7、缓存
                    String string = JSON.toJSONString(redisTo);
                    hashOps.put(item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString(), string);
                }
            });
        });
    }

    /**
     * 获取当前时间段的秒杀商品
     *
     * @return
     */
    @Override
    public List<SeckillSkuRedisTo> getCurrentTimeSeckillSkus() {
        //1、获取当前时间
        long currentTime = System.currentTimeMillis();
        //2、查询所有活动场次
        Set<String> keys = stringRedisTemplate.keys(SESSION_CACHE_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                //seckill:sessions:1664025209000_1664208000000
                //2.1、截取多余字符
                String replace = key.replace(SESSION_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                long stateTime = Long.parseLong(s[0]);
                long endTime = Long.parseLong(s[1]);
                if (currentTime >= stateTime && currentTime <= endTime) {
                    //3、得到当前时间段的活动m
                    List<String> seckillKeys = stringRedisTemplate.opsForList().range(key, 0, -1);
                    if (seckillKeys != null) {
                        //4、根据这些key查询对应的sku
                        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUSECKILL_CACHE_PREFIX);
                        List<String> strings = hashOps.multiGet(seckillKeys);
                        if (strings != null) {
                            //5、得到对应的skuInfo
                            //todo 此处有问题，一旦当前时间有多场活动，则最终只会显示一场
                            return strings.stream().map(item -> {
                                return JSON.parseObject(item, SeckillSkuRedisTo.class);
                            }).collect(Collectors.toList());
                        }
                    }
                }
            }
        }
        return null;
    }
}

package com.zzl.huayumall.product.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zzl.common.utils.R;
import com.zzl.huayumall.product.vo.Catelog2Vo;
import lombok.Synchronized;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.product.dao.CategoryDao;
import com.zzl.huayumall.product.entity.CategoryEntity;
import com.zzl.huayumall.product.service.CategoryService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Resource
    private StringRedisTemplate redis;
    @Resource
    private RedissonClient client;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public Long[] findCategoryPath(Long catelogId) {
        //收集路径
        List<Long> pathList = new ArrayList<>();
        //查询父id
        findParentPath(catelogId, pathList);
        //将查询的结果倒转
        Collections.reverse(pathList);

        return pathList.toArray(new Long[pathList.size()]);
    }

    /**
     * 查询一级分类菜单
     *
     * @return
     */
    @Override
    @Cacheable(value = "category",key = "#root.methodName")
    public List<CategoryEntity> selectDelevlCategoryOne() {
        List<CategoryEntity> categoryEntities = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }


    /**
     * 查询缓存
     *
     * @return
     */
    @Override
    @Cacheable(value = "category",key = "'categoryJSON'",sync = true)
    public Map<String, List<Catelog2Vo>> getCategoryJson() {
            //查询数据库，返回并存储到redis
            Map<String, List<Catelog2Vo>> stringListMap = getCategoryJsonForDbWithRedissonLock();
            return stringListMap;
    }

    /**
     * redisson分布式锁
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCategoryJsonForDbWithRedissonLock() {

        Map<String, List<Catelog2Vo>> dataForDb;
            dataForDb = getDataForDb();
            return dataForDb;
    }

    /**
     * 本地锁
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCategoryJsonForDbWithLocalLock() {
        //锁住当前线程
        //这里还有一个问题，就是如果一堆请求进来，但是以为将查询到的加入缓存需要一定时间
        //  解决方法就是，查询到数据之后也要将数据进行缓存，这两件事要保证原子性，也就是在一把锁里完成
        synchronized (this) {
            /**
             * todo
             * 双重检查,但这样也有问题，效率太低了，请求都被拦在了外面
             */
            String categoryJSON = redis.opsForValue().get("categoryJSON");
            if (!StringUtils.isEmpty(categoryJSON)) {
                Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(categoryJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
                return stringListMap;
            }
            System.out.println("缓存没有，查询数据库");
            //查询全部
            List<CategoryEntity> categoryEntityList = baseMapper.selectList(null);

            //收集一级分类
            List<CategoryEntity> categoryEntities = categoryIdCompareParentId(categoryEntityList, 0L);

            Map<String, List<Catelog2Vo>> categoryList = categoryEntities.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                //收集二级分类
                List<CategoryEntity> categoryLevelTwo = categoryIdCompareParentId(categoryEntityList, v.getCatId());
                List<Catelog2Vo> Catelog2Vo = null;

                if (categoryLevelTwo != null) {
                    //遍历二级分类收集该三级分类
                    Catelog2Vo = categoryLevelTwo.stream().map(level2 -> {
                        //封装二级分类数据
                        Catelog2Vo categoryLevel2 = new Catelog2Vo(level2.getParentCid().toString(), level2.getCatId().toString(), level2.getName(), null);
                        //收集三级分类
                        List<CategoryEntity> categoryLevelThree = categoryIdCompareParentId(categoryEntityList, level2.getCatId());
                        //遍历三级分类，封装数据
                        List<Catelog2Vo.Catelog3Vo> collect3 = categoryLevelThree.stream().map(level3 -> {
                            Catelog2Vo.Catelog3Vo categoryLevel3 = new Catelog2Vo.Catelog3Vo(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                            return categoryLevel3;
                        }).collect(Collectors.toList());
                        //封装二级分类下的三级分类
                        categoryLevel2.setCatalog3List(collect3);
                        return categoryLevel2;
                    }).collect(Collectors.toList());
                }
                return Catelog2Vo;
            }));
            //加入缓存
            String jsonStr = JSONUtil.toJsonStr(categoryList);
            redis.opsForValue().set("categoryJSON", jsonStr, 2400, TimeUnit.SECONDS);
            return categoryList;
        }
    }


    /**
     * 分布式锁
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCategoryJsonForDbWithRedisLock() {

        String token = UUID.randomUUID().toString();
        //1、占分布式锁，加锁的同时也要设置过期时间，要保证原子性
        Boolean lock = redis.opsForValue().setIfAbsent("lock", token, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("进入锁成功");
            Map<String, List<Catelog2Vo>> dataForDb;
            try {
                //加锁成功，执行业务
                dataForDb = getDataForDb();
            } finally {
                //删除锁，还要保证删除的是自己的锁
                //删除自己的锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                Long lock1 = redis.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), token);
            }
            return dataForDb;
        } else {
            //锁被占了，休眠一会再继续调用自己的方法
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            return getCategoryJsonForDbWithRedisLock();
        }
    }

    /**
     * 查询数据库获取分类菜单
     *
     * @return
     */
    private Map<String, List<Catelog2Vo>> getDataForDb() {
            System.out.println("查询数据库");
            //查询全部
            List<CategoryEntity> categoryEntityList = baseMapper.selectList(null);

            //收集一级分类
            List<CategoryEntity> categoryEntities = categoryIdCompareParentId(categoryEntityList, 0L);

            Map<String, List<Catelog2Vo>> categoryList = categoryEntities.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                //收集二级分类
                List<CategoryEntity> categoryLevelTwo = categoryIdCompareParentId(categoryEntityList, v.getCatId());
                List<Catelog2Vo> Catelog2Vo = null;

                if (categoryLevelTwo != null) {
                    //遍历二级分类收集该三级分类
                    Catelog2Vo = categoryLevelTwo.stream().map(level2 -> {
                        //封装二级分类数据
                        Catelog2Vo categoryLevel2 = new Catelog2Vo(level2.getParentCid().toString(), level2.getCatId().toString(), level2.getName(), null);
                        //收集三级分类
                        List<CategoryEntity> categoryLevelThree = categoryIdCompareParentId(categoryEntityList, level2.getCatId());
                        //遍历三级分类，封装数据
                        List<Catelog2Vo.Catelog3Vo> collect3 = categoryLevelThree.stream().map(level3 -> {
                            Catelog2Vo.Catelog3Vo categoryLevel3 = new Catelog2Vo.Catelog3Vo(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                            return categoryLevel3;
                        }).collect(Collectors.toList());
                        //封装二级分类下的三级分类
                        categoryLevel2.setCatalog3List(collect3);
                        return categoryLevel2;
                    }).collect(Collectors.toList());
                }
                return Catelog2Vo;
            }));

            return categoryList;
    }


    /**
     * 根据id比较收集数据
     *
     * @param categoryEntities ：全部分类数据
     * @param cateId           ：父分类id
     * @return
     */
    private List<CategoryEntity> categoryIdCompareParentId(List<CategoryEntity> categoryEntities, Long cateId) {
        List<CategoryEntity> collect = categoryEntities.stream().filter(item -> item.getParentCid() == cateId).collect(Collectors.toList());
        return collect;
    }

    /**
     * 递归查询
     *
     * @param catelogId
     * @param pathList
     * @return
     */
    public List<Long> findParentPath(Long catelogId, List<Long> pathList) {
        //查询当前菜单
        CategoryEntity entity = this.getById(catelogId);
        pathList.add(catelogId);
        //判断是否还有父菜单
        if (entity.getParentCid() != 0) {
            //有,使用递归
            findParentPath(entity.getParentCid(), pathList);
        }
        return pathList;
    }

    /**
     * =
     * 根据id:删除分类
     *
     * @param asList
     */
    @Override
    public R removeMenuByIds(List<Long> asList) {
        //删除前根据id查询当前菜单是否被引用
        //根据这些ip批量查询是否有parentID进行引用
        List<CategoryEntity> entities = baseMapper.batchSelectCategoryByCtaIds(asList);
        if (entities.isEmpty()) {
            //可以删除
            baseMapper.deleteBatchIds(asList);
            return R.ok();
        }
        return R.error("菜单被引用，不能删除！");
    }

    /**
     * 查询所有分类和子分类
     *
     * @return
     */
    @Override
    public List<CategoryEntity> selectCategoryByLevel() {
        //查询所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        List<CategoryEntity> collect = entities.stream().filter(category -> {
            //一级分类
            return category.getParentCid() == 0;
        }).map((menu) -> {
            //将查询的二级分类存储到一级分类里
            menu.setChildren(getChildren(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());


        return collect;
    }

    /**
     * 根据一级分类查询二级，二级查询三级
     *
     * @param currentCategory
     * @param allCategory
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity currentCategory, List<CategoryEntity> allCategory) {

        List<CategoryEntity> collect = allCategory.stream().filter((category) -> {
            //二级分类
//            return category.getParentCid() == currentCategory.getCatId();
            return currentCategory.getCatId() == category.getParentCid();
        }).map((menu) -> {
            //查询的三级分类存储到二级分类里
            menu.setChildren(getChildren(menu, allCategory));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return collect;
    }
}

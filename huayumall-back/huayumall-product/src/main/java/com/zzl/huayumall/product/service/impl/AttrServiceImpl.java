package com.zzl.huayumall.product.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.common.constant.productConstant;
import com.zzl.huayumall.product.dao.AttrAttrgroupRelationDao;
import com.zzl.huayumall.product.dao.AttrGroupDao;
import com.zzl.huayumall.product.dao.CategoryDao;
import com.zzl.huayumall.product.entity.*;
import com.zzl.huayumall.product.service.CategoryService;
import com.zzl.huayumall.product.vo.AttrRespVo;
import com.zzl.huayumall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.product.dao.AttrDao;
import com.zzl.huayumall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Resource
    private AttrAttrgroupRelationDao attrgroupRelationDao;
    @Resource
    private AttrGroupDao attrGroupDao;
    @Resource
    private CategoryDao categoryDao;
    @Resource
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttrAndAttrGroup(AttrVo attrvo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrvo,attrEntity);
        boolean save = this.save(attrEntity);
        if(!save){
            throw new RuntimeException();
        }
        /**
         * 保存成功，同时也要存储信息到attr_group表
         *      存储当前属性id,和分组名称,mybatis会回调id
         */
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
        attrAttrgroupRelationEntity.setAttrGroupId(attrvo.getAttrGroupId());
        //保存
        attrgroupRelationDao.insert(attrAttrgroupRelationEntity);
    }

    /**
     * 基本属性和销售属性的查询，根据type决定，属性类型[0-销售属性，1-基本属性
     * @param params
     * @param catId
     * @param type
     * @return
     */
    @Override
    public PageUtils selectByAttrList(Map<String, Object> params, Long catId, String type) {
        if(catId == 0){
            //查询全部
            IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),new QueryWrapper<AttrEntity>().eq("attr_type","sale".equalsIgnoreCase(type)?productConstant.AttrEnum.ATTR_TYPE_SALE.getCode():productConstant.AttrEnum.ATTR_TYPE_BASE.getCode()));
            //重新设置值，添加groupName和categoryName
            PageUtils pageUtils = new PageUtils(page);

            List<AttrEntity> records = page.getRecords();

            List<AttrRespVo> attr = records.stream().map((entity) -> {
                AttrRespVo attrRespVo = new AttrRespVo();

                BeanUtils.copyProperties(entity, attrRespVo);
                //查询关联表，如果是基本属性才存储分组name
                if(entity.getAttrType() == productConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
                    AttrAttrgroupRelationEntity attrGroupRelation = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", entity.getAttrId()));
                    if(attrGroupRelation != null && attrGroupRelation.getAttrGroupId() != null){
                        //获取组id，查询
                        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupRelation.getAttrGroupId());
                        if(attrGroupEntity!=null){
                            //存储name
                            attrRespVo.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                        }
                    }
                }
                //查询分类name
                CategoryEntity categoryEntity = categoryDao.selectById(entity.getCatelogId());
                //存储name
                if(!StringUtils.isEmpty(categoryEntity)){
                    attrRespVo.setCatelogName(categoryEntity.getName());
                }
                return attrRespVo;
            }).collect(Collectors.toList());
            //最新数据
            pageUtils.setList(attr);
            return pageUtils;
        }
        String key = (String) params.get("key");
        QueryWrapper<AttrEntity> catelog_id = new QueryWrapper<AttrEntity>().eq("catelog_id", catId).eq("attr_type","sale".equalsIgnoreCase(type)?productConstant.AttrEnum.ATTR_TYPE_SALE.getCode():productConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(!StringUtils.isEmpty(key)){
            catelog_id.and((obj)->{
               obj.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        //查询
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),catelog_id);
        PageUtils pageUtils = new PageUtils(page);

        List<AttrEntity> records = page.getRecords();

        List<AttrRespVo> attr_id1 = records.stream().map((entity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();

            BeanUtils.copyProperties(entity, attrRespVo);
            //查询关联表，如果是基本属性才存储分组name
            if(entity.getAttrType() == productConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
                AttrAttrgroupRelationEntity attr_id = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", entity.getAttrId()));
                if(attr_id != null){
                    //获取组id，查询
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attr_id.getAttrGroupId());
                    //存储name
                    attrRespVo.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            //查询分类name
            CategoryEntity categoryEntity = categoryDao.selectById(entity.getCatelogId());
            //存储name
            attrRespVo.setCatelogName(categoryEntity.getName());
            return attrRespVo;
        }).collect(Collectors.toList());
        //最新数据
        pageUtils.setList(attr_id1);
        return pageUtils;
    }

    /**
     * 根据key动态查询
     * @param params
     * @param type
     * @return
     */
    @Override
    public PageUtils selectAttrGroupByKeyDynamic(Map<String, Object> params, String type) {
        /**
         * todo:
         *    1、如果key是文字，而且是模糊查询，那么也会把销售属性的数据也查出来，但是在数据库上操作却查不出来：select * from pms_attr where attr_type = 1 or attr_id LIKE  "选" or attr_name LIKE  "选" LIMIT 1 , 10;
         *    2、
         */
        String key = (String) params.get("key");

        if(StringUtils.isEmpty(key)){
           //查全部
            IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), new QueryWrapper<AttrEntity>().eq("attr_type","sale".equalsIgnoreCase(type)?productConstant.AttrEnum.ATTR_TYPE_SALE.getCode():productConstant.AttrEnum.ATTR_TYPE_BASE.getCode()));
            PageUtils pageUtils = new PageUtils(page);
            List<AttrEntity> records = page.getRecords();

            List<AttrRespVo> attr_id1 = records.stream().map((entity) -> {
                AttrRespVo attrRespVo = new AttrRespVo();

                BeanUtils.copyProperties(entity, attrRespVo);
                //查询关联表
                AttrAttrgroupRelationEntity attr_id = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", entity.getAttrId()));
                if(attr_id != null && attr_id.getAttrGroupId() != null){
                    //获取组id，查询
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attr_id.getAttrGroupId());
                    //存储name
                    attrRespVo.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                }
                //查询分类name
                CategoryEntity categoryEntity = categoryDao.selectById(entity.getCatelogId());
                //存储name
                attrRespVo.setCatelogName(categoryEntity.getName());

                return attrRespVo;
            }).collect(Collectors.toList());
            //最新数据
            pageUtils.setList(attr_id1);
            //重新设置值，添加groupName和categoryName
            return pageUtils;
        }
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();
        attrEntityQueryWrapper.eq("attr_type","sale".equalsIgnoreCase(type)?productConstant.AttrEnum.ATTR_TYPE_SALE.getCode():productConstant.AttrEnum.ATTR_TYPE_BASE.getCode()).eq("attr_id",key).or().like("attr_name",key);
        //查询
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),attrEntityQueryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();

        List<AttrRespVo> attr_id1 = records.stream().map((entity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();

            BeanUtils.copyProperties(entity, attrRespVo);
            //查询关联表
            AttrAttrgroupRelationEntity attr_id = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", entity.getAttrId()));
            if(attr_id != null && attr_id.getAttrGroupId()!=null){
                //获取组id，查询
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attr_id.getAttrGroupId());
                //存储name
                attrRespVo.setAttrGroupName(attrGroupEntity.getAttrGroupName());
            }
            //查询分类name
            CategoryEntity categoryEntity = categoryDao.selectById(entity.getCatelogId());
            //存储name
            attrRespVo.setCatelogName(categoryEntity.getName());
            return attrRespVo;
        }).collect(Collectors.toList());
        //最新数据
        pageUtils.setList(attr_id1);
        //重新设置值，添加groupName和categoryName
        return pageUtils;
    }

    /**
     * 修改回显数据
     * @param attrId
     * @return
     */
    @Override
    public AttrRespVo getDetailsDate(Long attrId) {
        //查询attr基本信息
        AttrEntity byId = this.getById(attrId);
        AttrRespVo vo = new AttrRespVo();
        //拷贝数据
        BeanUtils.copyProperties(byId,vo);
        //根据该id查询关联表，获取分组id,在根据该id或者name
        //只有修改基本信息才回显组名
        if(byId.getAttrType() == productConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity attr_id = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if(attr_id != null){
                //查询分组
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attr_id.getAttrGroupId());
                if(attrGroupEntity != null){
                    //收集数据
                    vo.setAttrGroupId(attrGroupEntity.getAttrGroupId());
                    vo.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
        //查询分类信息
        CategoryEntity categoryEntity = categoryDao.selectById(byId.getCatelogId());
        if(categoryEntity != null){
           //收集数据
            vo.setCatelogId(categoryEntity.getCatId());
            vo.setCatelogName(categoryEntity.getName());
        }
        //查询分类路径
        Long[] categoryPath = categoryService.findCategoryPath(categoryEntity.getCatId());
        //收集
        vo.setCatelogPath(categoryPath);

        return vo;
    }

    /**
     * 修改规格表同时也要修改和其关联的属性分组
     * @param attrRespVo
     */
    @Override
    @Transactional
    public void updateByIdAndAttrGroup(AttrRespVo attrRespVo) {
        //修改规格表
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrRespVo,attrEntity);
        boolean b = this.updateById(attrEntity);
        if(!b) {
            throw new RuntimeException();
        }
        //修改成功,修改以其关联的表
        //修改前判断。如果该关联关系不存在，则新建 只有基本属性才能修改组名
        if(attrEntity.getAttrType() == productConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            entity.setAttrGroupId(attrRespVo.getAttrGroupId());
            entity.setAttrId(attrEntity.getAttrId());
            //统计
            Integer count = attrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if(count>0){
                //修改
                attrgroupRelationDao.update(entity,new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",entity.getAttrId()));
            }else{
                //新建
                attrgroupRelationDao.insert(entity);
            }
        }
    }

    /**
     * 根据分组id查询没被关联的属性分页数据
     * @param attrgroupId
     * @param param
     * @return
     */
    @Override
    public PageUtils noAttrRelation(Long attrgroupId, Map<String, Object> param) {
        //根据分组id查询当前分类
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        //收集当前分类id
        Long catelogId = attrGroupEntity.getCatelogId();
        //根据分类id查询所有分组
        List<AttrGroupEntity> group = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //收集到所有分组id
        List<Long> groupIds = group.stream().map((item) -> {
            return item.getAttrGroupId();

        }).collect(Collectors.toList());
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id",catelogId).eq("attr_type",productConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(groupIds.size()>0 && !groupIds.isEmpty()){
            //根据这些分组id查询被关联的属性
            List<AttrAttrgroupRelationEntity> relationEntities = attrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));
            //根据这些属性id查询没被关联的属性
            List<Long> attrIds = relationEntities.stream().map((item) -> {
                return item.getAttrId();
            }).collect(Collectors.toList());
            //检索条件
            Object key = param.get("key");
            if(!StringUtils.isEmpty(key)){
                wrapper.and((obj)->{
                    obj.eq("attr_id",key).or().like("attr_name",key);
                });
            }
                //获取当前分类下没被关联的属性
                if(!attrIds.isEmpty() && attrIds.size()>0){
                    wrapper.notIn("attr_id",attrIds);
                }
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(param), wrapper);

        return new PageUtils(page);
    }

    /**
     * 根据分组id批量查询属性
     * @param groupId
     * @return
     */
    @Override
    public List<AttrEntity> selectAttrListByGroupId(Long groupId) {
        //查询关联表获取属性id
        List<AttrAttrgroupRelationEntity> relationEntities = attrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", groupId));
        List<Long> attrIds = null;
        List<AttrEntity> attrEntities = null;
        if (relationEntities != null && relationEntities.size() > 0) {
            //收集属性id
            attrIds = relationEntities.stream().map((item) -> {
                return item.getAttrId();
            }).collect(Collectors.toList());
            //根据属性id批量查询
            attrEntities = baseMapper.selectBatchIds(attrIds);
        }

        return attrEntities;
    }

    /**
     * 根据spuId进行修改
     * @param spuId
     * @param valueEntities
     */
    @Override
    public void updateByspuId(Long spuId, List<ProductAttrValueEntity> valueEntities) {
        //为了防止是修改还是新增，先根据spuId进行删除

    }

    /**
     * 根据attrId查询能被检索的属性信息
     * @param attrIds
     * @return
     */
    @Override
    public List<Long> selectAttrAndSearchByIds(List<Long> attrIds) {
        return baseMapper.selectAttrAndSearchByIds(attrIds);
    }

}

package com.zzl.huayumall.product.service.impl;

import com.zzl.huayumall.product.entity.AttrEntity;
import com.zzl.huayumall.product.service.AttrAttrgroupRelationService;
import com.zzl.huayumall.product.service.AttrService;
import com.zzl.huayumall.product.vo.AttrGroupWithAttrVo;
import com.zzl.huayumall.product.vo.SkuItemVo;
import com.zzl.huayumall.product.vo.SpuItemBaseAttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.product.dao.AttrGroupDao;
import com.zzl.huayumall.product.entity.AttrGroupEntity;
import com.zzl.huayumall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 动态查询
     * @param params
     * @param id
     * @return
     */
    @Override
    public PageUtils selectAttrGroupByCatIdAndAnyField(Map<String, Object> params,Long id) {
        /**
         * todo:
         *   因为前端共用一个查询方法，也就是当id为0时查询所有，但是只有当菜单被点击时catId才不是0
         *      也就是只要点击菜单，catID才不是0，那么即使key不是空的，也会一直查询所有
         *  已解决：selectAttrGroupByAndAnyField()
         */
        if(id ==0){
            //查询所有
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>()
            );
            return new PageUtils(page);
        }else{
            //根据id查询三级分类,问题是key是多个的，不确定
            String key = (String) params.get("key");
            QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id",id);
            //有key，再加上这个key进行查询
            if(!StringUtils.isEmpty(key)){
                queryWrapper.and((obj)->{
                   obj.eq("attr_group_id",key).or().like("attr_group_name",key);
                });
            }
            //完成动态查询
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),queryWrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据key完成动态查询
     * @param params
     * @return
     */
    @Override
    public PageUtils queryAttrGroupByKeyDynamic(Map<String, Object> params) {
        String key = (String) params.get("key");
        if(StringUtils.isEmpty(key)){
            //查询全部
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), new QueryWrapper<>());
            return new PageUtils(page);

        }
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_group_id",key).or().like("attr_group_name",key).or().like("descript",key);
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),queryWrapper);
        return new PageUtils(page);
    }

    /**
     * 根据catId查询属性分组和属性
     * @param catId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrVo> selectAttrGroupsWithAttrsByCatId(Long catId) {
        //根据分类id查询分组
        List<AttrGroupEntity> catelogId = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        List<AttrGroupWithAttrVo> collect = null;
        if (catelogId != null && catelogId.size() > 0) {
            //分解获取属性id,并收集数据
            collect = catelogId.stream().map((item) -> {
                AttrGroupWithAttrVo vo = new AttrGroupWithAttrVo();
                //查询属性
                List<AttrEntity> listAttr = attrService.selectAttrListByGroupId(item.getAttrGroupId());
                if(!listAttr.isEmpty() && listAttr.size()>0){
                    BeanUtils.copyProperties(item, vo);
                    vo.setAttrs(listAttr);
                }
                /**
                 * todo，因为页面需要遍历AttrGroupWithAttrVo的全部信息，包括List<attr>而一旦该值为空，那么就会报错
                 *      而问题就是，因为根据分类id查询分组，那么全都会查询出来，就是查询分类表时查不出来，但还是存了分组的信息
                 *          但是attr却是空的，那么就会报错
                 */
                return vo;
            }).filter(a->{
                return !StringUtils.isEmpty(a);
            }).collect(Collectors.toList());

            return collect;
        }
        return collect;
    }

    @Override
    public List<SpuItemBaseAttrGroupVo> selectSpuItemBySkuIdAndCategoryId(Long catalogId, Long spuId) {
        List<SpuItemBaseAttrGroupVo> vos = baseMapper.selectSpuItemBySkuIdAndCategoryId(catalogId, spuId);
        return vos;
    }
}

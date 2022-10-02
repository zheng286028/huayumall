package com.zzl.huayumall.product.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzl.huayumall.product.dao.AttrAttrgroupRelationDao;
import com.zzl.huayumall.product.entity.AttrAttrgroupRelationEntity;
import com.zzl.huayumall.product.entity.AttrEntity;
import com.zzl.huayumall.product.service.AttrAttrgroupRelationService;
import com.zzl.huayumall.product.service.AttrService;
import com.zzl.huayumall.product.service.CategoryService;
import com.zzl.huayumall.product.vo.AttrGroupWithAttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zzl.huayumall.product.entity.AttrGroupEntity;
import com.zzl.huayumall.product.service.AttrGroupService;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.R;

import javax.annotation.Resource;


/**
 * 属性分组
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 23:09:42
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Resource
    private AttrService attrService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private AttrAttrgroupRelationDao attrgroupRelationDao;
    @Resource
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 根据catId查询属性分组和属性
     * @param catId
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R queryAttrGroupWithAttrByCateId(@PathVariable("catelogId") Long catId){
        List<AttrGroupWithAttrVo> vos = attrGroupService.selectAttrGroupsWithAttrsByCatId(catId);
        return R.ok().put("data",vos);
    }

    /**
     * 根据分组id查询已知关联的规格参数
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId")Long attrgroupId){
        //根据分组id获取关联的规格参数
        List<AttrAttrgroupRelationEntity> entities = attrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        if(entities !=null){
            //收集规格id
            List<Long> collect = entities.stream().map((attr) -> {
                return attr.getAttrId();
            }).collect(Collectors.toList());
            //批量查询
            Collection<AttrEntity> attrEntities = attrService.listByIds(collect);
            return R.ok().put("data",attrEntities);
        }
        return R.error();
    }

    /**
     * 根据分组id查询没被关联的属性分页数据
     * @param attrgroupId
     * @param param
     * @return
     */
    @GetMapping("{attrgroupId}/noattr/relation")
    public R noIncludeAttrRelation(@PathVariable("attrgroupId")Long attrgroupId,@RequestParam Map<String,Object> param){
        PageUtils page = attrService.noAttrRelation(attrgroupId,param);
        return R.ok().put("page",page);
    }

    /**
     * 批量保存关联属性分组
     * @param attr
     * @return
     */
    @PostMapping("/attr/relation")
    public R addAttrRelation(@RequestBody List<AttrAttrgroupRelationEntity> attr){
        if(attr.size()>0 && !attr.isEmpty()){
            attrAttrgroupRelationService.saveBatch(attr);
            return R.ok();
        }
        return R.error("不可提交非法数据");
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R allList(@RequestParam Map<String, Object> params,
                     @PathVariable("catId")Long id
                     ){
        PageUtils page = attrGroupService.selectAttrGroupByCatIdAndAnyField(params,id);

        return R.ok().put("data", page);
    }

    /**
     * 动态查询列表
     */
    @RequestMapping("/list/queryAttrGroupByKeyDynamic")
    //@RequiresPermissions("product:attrgroup:list")
    public R queryAttrGroupByKeyDynamic(@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryAttrGroupByKeyDynamic(params);

        return R.ok().put("data", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        //当前属性的菜单id
        Long catelogId = attrGroup.getCatelogId();
        //根据当前菜单id查询父子id
        Long[] path = categoryService.findCategoryPath(catelogId);

        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R attrRelationDelete(@RequestBody List<Long> ids){
        if(ids !=null && ids.size()>0){
            attrGroupService.removeByIds(ids);
            return R.ok();
        }
       return R.error();
    }

}

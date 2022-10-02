package com.zzl.huayumall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zzl.huayumall.product.entity.ProductAttrValueEntity;
import com.zzl.huayumall.product.service.ProductAttrValueService;
import com.zzl.huayumall.product.vo.AttrRespVo;
import com.zzl.huayumall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zzl.huayumall.product.service.AttrService;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.R;



/**
 * 商品属性
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 23:09:42
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;


    /**
     * 属性规格
     * @param id
     * @return
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseListforspu(@PathVariable("spuId")Long id){
        List<ProductAttrValueEntity> attrValueEntities = productAttrValueService.baseListforspu(id);
        return R.ok().put("data",attrValueEntities);
    }

    //product/attr/update/{spuId}
    @RequestMapping("/update/{spuId}")
    //@RequiresPermissions("product:attr:update")
    public R updateBySpuId(@PathVariable("spuId")Long spuId,List<ProductAttrValueEntity> valueEntities){
        productAttrValueService.updateByspuId(spuId,valueEntities);

        return R.ok();
    }

    @GetMapping("/{type}/list/{catId}")
    public R selectAttrList(@RequestParam Map<String, Object> params,@PathVariable("catId") Long catId,@PathVariable("type")String type){
        PageUtils page = attrService.selectByAttrList(params,catId,type);
        return R.ok().put("page",page);
    }

    /**
     * 查询按钮完成动态查询
     * @param params
     * @return
     */
    @GetMapping("/{attrType}/selectAttrGroupByKeyDynamic")
    public R selectAttrGroupByKeyDynamic(@RequestParam Map<String, Object> params,@PathVariable("attrType")String type){
        PageUtils page = attrService.selectAttrGroupByKeyDynamic(params,type);
        return R.ok().put("page",page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		AttrRespVo attr = attrService.getDetailsDate(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrvo){
        attrService.saveAttrAndAttrGroup(attrvo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrRespVo AttrRespVo){
		attrService.updateByIdAndAttrGroup(AttrRespVo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}

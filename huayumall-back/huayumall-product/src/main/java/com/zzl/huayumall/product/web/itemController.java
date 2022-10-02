package com.zzl.huayumall.product.web;

import com.zzl.huayumall.product.service.SkuInfoService;
import com.zzl.huayumall.product.vo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/11  14:44
 */
@Controller
public class itemController {
    @Resource
    private SkuInfoService skuInfoService;


    /**
     * 商品的详细首页
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String productItemBySkuId(@PathVariable("skuId") Long skuId, Model model){
        SkuItemVo vo = null;
        try {
            vo = skuInfoService.selectProductItemBySkuId(skuId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        model.addAttribute("item",vo);
        return "item";
    }
}

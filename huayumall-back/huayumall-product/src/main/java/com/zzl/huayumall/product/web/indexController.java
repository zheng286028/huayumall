package com.zzl.huayumall.product.web;

import com.zzl.huayumall.product.entity.CategoryEntity;
import com.zzl.huayumall.product.service.CategoryService;
import com.zzl.huayumall.product.vo.Catelog2Vo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/02  13:58
 */
@Controller
public class indexController {
    @Resource
    private CategoryService categoryService;

    /**
     * 首页
     *
     * @param model
     * @return
     */
    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        List<CategoryEntity> category = categoryService.selectDelevlCategoryOne();
        model.addAttribute("categorys", category);
        return "index";
    }

    /**
     * 菜单
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCategoryJson() {
        /*Map<String, List<Catelog2Vo>> listMap = categoryService.getCategoryJson();*/
        Map<String, List<Catelog2Vo>> listMap = categoryService.getCategoryJson();
        return listMap;
    }

}

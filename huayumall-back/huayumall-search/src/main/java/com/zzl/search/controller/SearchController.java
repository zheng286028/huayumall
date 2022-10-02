package com.zzl.search.controller;

import com.zzl.search.service.MallSearchService;
import com.zzl.search.vo.SearchParam;
import com.zzl.search.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * π¶ƒ‹√Ë ˆ
 *
 * @author ÷£◊”¿À
 * @date 2022/08/05  15:31
 */
@Controller
public class SearchController {
    @Resource
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request){
        param.set_queryString(request.getQueryString());
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result",result);
        return "list";
    }

}

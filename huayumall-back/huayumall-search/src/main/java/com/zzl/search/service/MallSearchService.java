package com.zzl.search.service;

import com.zzl.search.vo.SearchParam;
import com.zzl.search.vo.SearchResult;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/06  13:57
 */
public interface MallSearchService {
    SearchResult search(SearchParam param);
}

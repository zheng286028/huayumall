package com.zzl.huayumall.order.feign;

import com.zzl.common.utils.R;
import com.zzl.huayumall.order.vo.SkuStockVo;
import com.zzl.huayumall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/09  21:42
 */
@FeignClient("huayumall-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hasSkuStock")
    List<SkuStockVo> selectWareHasSkuStock(@RequestBody List<Long> skuId);

    @GetMapping("/ware/wareinfo/freight")
    public R getFreight(@RequestParam("addrId")Long addrId);

    @PostMapping("/ware/waresku/ware/lock")
    public R orderWareLock(@RequestBody WareSkuLockVo vo);
}

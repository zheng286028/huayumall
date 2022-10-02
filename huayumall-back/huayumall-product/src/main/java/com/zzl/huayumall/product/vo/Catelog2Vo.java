package com.zzl.huayumall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/02  15:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo {
    private String catalog1Id;
    private String id;
    private String name;
    private List<Catelog3Vo> catalog3List;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo {
        private String catalog2Id;
        private String id;
        private String name;
    }
}

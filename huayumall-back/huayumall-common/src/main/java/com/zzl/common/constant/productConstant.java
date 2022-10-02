package com.zzl.common.constant;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/07/25  23:33
 */
public class productConstant {

    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"基本属性"),
        ATTR_TYPE_SALE(0,"销售属性");
        private int code;
        private String msg;

        AttrEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
    public enum UpEnum{
        NEW_STATUS(0,"商品新建"),
        UP_STATUS(1,"商品上架"),
        DOWN_STATUS(2,"商品下架");
        private int code;
        private String msg;

        UpEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}

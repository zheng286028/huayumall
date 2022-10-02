package com.zzl.common.constant;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/07/29  14:31
 */
public class wareConstant {
    public enum purchaseDetailEnum {
        STATUS_NEW(0, "新建"),
        STATUS_ASSIGNED(1, "已分配"),
        STATUS_PURCHASING(2, "正在采购"),
        STATUS_COMPLETED(3, "已完成"),
        STATUS_PURCHASING_FAIL(4, "采购失败");
        private int code;
        private String msg;

        purchaseDetailEnum( int code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode () {
            return code;
        }

        public String getMsg () {
            return msg;
        }
    }
    public enum purchaseEnum {
        STATUS_NEW(0, "新建"),
        STATUS_ASSIGNED(1, "已分配"),
        STATUS_RECEIVED(2, "已领取"),
        STATUS_COMPLETED(3, "已完成"),
        STATUS_EXCEPTION(4, "有异常");
        private int code;
        private String msg;

        purchaseEnum( int code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode () {
            return code;
        }

        public String getMsg () {
            return msg;
        }
    }
}

package com.zzl.common.exception;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/07/23  15:41
 */
public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"数据校验失败"),
    RUNTIME_EXCEPTION(10002,"系统繁忙，请稍后重试"),
    NULLPOINTER_Exception(10003,"不能提交空数据"),
    PURCHASE_Exception(10004,"该采购需求正被采购"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    SMS_CODE(11001,"验证码已发送，请注意查收"),
    USER_PHONE_EXIST_EXCEPTION(11002,"手机号已存在"),
    NO_STOCK_EXCEPTION(21000,"商品库存不足"),
    USER_USERNAME_EXIST_EXCEPTION(11003,"用户名已存在");

    private int code;
    private String msg;

    BizCodeEnum(int code,String msg){
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

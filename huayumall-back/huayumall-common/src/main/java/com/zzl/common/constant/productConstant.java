package com.zzl.common.constant;

/**
 * ��������
 *
 * @author ֣����
 * @date 2022/07/25  23:33
 */
public class productConstant {

    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"��������"),
        ATTR_TYPE_SALE(0,"��������");
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
        NEW_STATUS(0,"��Ʒ�½�"),
        UP_STATUS(1,"��Ʒ�ϼ�"),
        DOWN_STATUS(2,"��Ʒ�¼�");
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

package com.zzl.common.constant;

/**
 * ��������
 *
 * @author ֣����
 * @date 2022/07/29  14:31
 */
public class wareConstant {
    public enum purchaseDetailEnum {
        STATUS_NEW(0, "�½�"),
        STATUS_ASSIGNED(1, "�ѷ���"),
        STATUS_PURCHASING(2, "���ڲɹ�"),
        STATUS_COMPLETED(3, "�����"),
        STATUS_PURCHASING_FAIL(4, "�ɹ�ʧ��");
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
        STATUS_NEW(0, "�½�"),
        STATUS_ASSIGNED(1, "�ѷ���"),
        STATUS_RECEIVED(2, "����ȡ"),
        STATUS_COMPLETED(3, "�����"),
        STATUS_EXCEPTION(4, "���쳣");
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

package com.zzl.common.Valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * ��������
 *  У�飺@ListValue������
 * @author ֣����
 * @date 2022/07/23  20:58
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue ,Integer> {

    private Set<Integer> set = new HashSet<>();

    /**
     * ��ʼ���������ռ��������Ĳ���
     * @param constraintAnnotation
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vale = constraintAnnotation.vale();
        //�ǿ��жϣ��ռ�У�������
        if(vale.length != 0){
            for (int item : vale) {
                set.add(item);
            }
        }
    }

    /**
     * ��ֵУ��
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        return set.contains(value);
    }
}

package com.zzl.common.Valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 功能描述
 *  校验：@ListValue的数据
 * @author 郑子浪
 * @date 2022/07/23  20:58
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue ,Integer> {

    private Set<Integer> set = new HashSet<>();

    /**
     * 初始化方法，收集传过来的参数
     * @param constraintAnnotation
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vale = constraintAnnotation.vale();
        //非空判断，收集校验的数据
        if(vale.length != 0){
            for (int item : vale) {
                set.add(item);
            }
        }
    }

    /**
     * 数值校验
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        return set.contains(value);
    }
}

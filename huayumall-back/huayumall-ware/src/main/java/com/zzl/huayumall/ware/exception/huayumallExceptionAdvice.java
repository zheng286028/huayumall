package com.zzl.huayumall.ware.exception;

import com.zzl.common.exception.BizCodeEnum;
import com.zzl.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述:
 *  集中处理异常信息
 * @author 郑子浪
 * @date 2022/07/23  15:19
 */
@RestControllerAdvice(basePackages = "com.zzl.huayumall.product.controller")
@Slf4j
public class huayumallExceptionAdvice {

    //参数校验异常
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleAdvice(MethodArgumentNotValidException e){

        BindingResult result = e.getBindingResult();

        Map<String,String> map = new HashMap<>();

        result.getFieldErrors().forEach((item)->{
            //错误信息
            String message = item.getDefaultMessage();
            //错误的属性
            String field = item.getField();
            map.put(field,message);
        });
        return R.error(BizCodeEnum.VAILD_EXCEPTION.getCode(),BizCodeEnum.VAILD_EXCEPTION.getMsg()).put("data",map);
    }
    //采购单合并异常
    @ExceptionHandler(value = purchaseMergeException.class)
    public R purchaseMergeException(purchaseMergeException e){
        log.info("异常信息为：{}",e.getLocalizedMessage());
        return R.error(BizCodeEnum.PURCHASE_Exception.getCode(),BizCodeEnum.PURCHASE_Exception.getMsg());
    }
    //空指针异常
    @ExceptionHandler(NullPointerException.class)
    public R handleNullPointerException(NullPointerException e){
        return R.error(BizCodeEnum.NULLPOINTER_Exception.getCode(),BizCodeEnum.NULLPOINTER_Exception.getMsg());
    }
    //运行异常
    @ExceptionHandler(RuntimeException.class)
    public R handleRuntimeException(RuntimeException e){
        return R.error(BizCodeEnum.RUNTIME_EXCEPTION.getCode(),BizCodeEnum.RUNTIME_EXCEPTION.getMsg());
    }
    //全部异常
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable e){
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(),BizCodeEnum.UNKNOW_EXCEPTION.getMsg());
    }
}

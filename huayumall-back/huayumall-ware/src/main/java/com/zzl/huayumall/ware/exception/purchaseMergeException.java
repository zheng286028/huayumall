package com.zzl.huayumall.ware.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/07/29  16:33
 */
@Component
public class purchaseMergeException extends RuntimeException{
    public purchaseMergeException() {
        super();
    }

    public purchaseMergeException(String message) {
        super(message);
    }
}

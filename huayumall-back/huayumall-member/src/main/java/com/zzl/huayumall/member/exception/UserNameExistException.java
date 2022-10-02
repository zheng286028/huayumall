package com.zzl.huayumall.member.exception;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/14  16:09
 */
public class UserNameExistException extends RuntimeException{
    public UserNameExistException() {
    }

    public UserNameExistException(String message) {
        super(message);
    }
}

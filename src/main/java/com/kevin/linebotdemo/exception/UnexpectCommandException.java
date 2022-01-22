package com.kevin.linebotdemo.exception;

/**
 * 當line指令有未知的指令則跳出此錯誤
 * @author liyanting
 */
public class UnexpectCommandException extends RuntimeException {

    public UnexpectCommandException(String message) {
        super(message);
    }
}

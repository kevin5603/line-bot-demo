package com.kevin.linebotdemo.config;

/**
 * @author liyanting
 */
public enum LineResponseCode {
    SUCCESS(200),
    CRATE(201),
    ERROR(404);

    LineResponseCode(int code) {

    }
}

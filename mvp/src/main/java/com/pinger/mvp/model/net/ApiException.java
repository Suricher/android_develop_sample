package com.pinger.mvp.model.net;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 3:23
 * 加载服务器API发生的错误异常
 */
public class ApiException extends Exception {

    public ApiException(String msg) {
        super(msg);
    }
}

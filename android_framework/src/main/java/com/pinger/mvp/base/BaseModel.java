package com.pinger.mvp.base;

import java.io.Serializable;

/**
 * @author Pinger
 * @since 2017/3/24 0024 下午 4:43
 * 数据模型
 */
public class BaseModel<T> implements Serializable {

    public int error_code;
    public T data;

    public boolean success() {
        return error_code == 1;
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "error_code=" + error_code +
                ", data=" + data +
                '}';
    }
}

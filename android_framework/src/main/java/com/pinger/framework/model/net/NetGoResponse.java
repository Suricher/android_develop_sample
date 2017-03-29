package com.pinger.framework.model.net;

/**
 * @author Pinger
 * @since 2017/3/29 0029 上午 10:28
 */
public class NetGoResponse<T> {

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

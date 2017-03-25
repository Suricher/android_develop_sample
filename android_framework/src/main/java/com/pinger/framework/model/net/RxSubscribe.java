package com.pinger.framework.model.net;

import rx.Subscriber;

/**
 * @author Pinger
 * @since 2017/3/24 0024 下午 4:53
 * 封装订阅者，自己进行处理，将错误视图拉取出来，统一进行管理
 */
public abstract class RxSubscribe<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {
        onAfter();
    }

    @Override
    public void onError(Throwable e) {
        onError(e.getMessage());
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }


    /**
     * 请求成功
     *
     * @param t 请求的数据
     */
    protected abstract void onSuccess(T t);

    /**
     * 请求失败
     *
     * @param message 失败的错误信息
     */
    protected void onError(String message){}

    /**
     * 请求完成
     */
    protected void onAfter(){}

}

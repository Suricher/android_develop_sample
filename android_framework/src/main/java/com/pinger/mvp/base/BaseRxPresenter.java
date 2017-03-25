package com.pinger.mvp.base;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 12:45
 * 基于RxJava的Presenter，统一管理事件的订阅者，防止内存泄漏
 */
public abstract class BaseRxPresenter implements BasePresenter {

    private CompositeSubscription mCompositeSubscription;

    /**
     * 移除订阅
     */
    protected void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }


    /**
     * 添加订阅
     *
     * @param subscription
     */
    protected void addSubscribe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }
}

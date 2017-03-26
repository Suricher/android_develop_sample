package com.pinger.framework.model.net;

import com.pinger.framework.base.BaseModel;

import rx.Observable;

/**
 * @author Pinger
 * @since 2017/3/24 0024 下午 4:46
 * API基类，抽取所有的请求接口
 */
public interface ApiService<T> {


    Observable<BaseModel<T>> requestData();

}

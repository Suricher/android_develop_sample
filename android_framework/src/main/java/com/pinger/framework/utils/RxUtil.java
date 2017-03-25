package com.pinger.framework.utils;

import com.pinger.framework.base.BaseModel;
import com.pinger.framework.model.net.ApiException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 3:06
 * RxJava管理网络接口的工具类
 */
public class RxUtil {

    /**
     * 创建Observable
     *
     * @param <T>
     * @return
     */
    public static <T> Observable<T> createData(final T t) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * 统一线程处理
     *
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<T, T> scheduleTransformer() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 处理观察结果
     *
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<BaseModel<T>, T> handleResult() {
        return new Observable.Transformer<BaseModel<T>, T>() {
            @Override
            public Observable<T> call(Observable<BaseModel<T>> responseObservable) {
                return responseObservable.flatMap(new Func1<BaseModel<T>, Observable<T>>() {
                    @Override
                    public Observable<T> call(BaseModel<T> response) {
                        if (response.success()) {
                            return createData(response.data);
                        } else if (response.error_code == -3) {
                            return Observable.error(new ApiException("参数错误"));
                        } else if (response.error_code == -16) {
                            return Observable.error(new ApiException("签名验证失败"));
                        } else if (response.error_code == -999) {
                            return Observable.error(new ApiException("其他错误"));
                        } else if (response.data == null) {
                            return Observable.error(new ApiException("服务器返回数据为空"));
                        } else {
                            return Observable.error(new ApiException("服务器发生错误"));
                        }
                    }
                });
            }
        };
    }
}

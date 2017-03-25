package com.pinger.framework.model.api;

import com.pinger.framework.base.BaseModel;
import com.pinger.framework.model.bean.BannerBean;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 3:37
 * 首页模块接口API
 */
public interface HomeApis {

    /**
     * 获取测试数据
     */
    @GET("v3/hot/banner")
    Observable<BaseModel<List<BannerBean>>> getBannerList();
}

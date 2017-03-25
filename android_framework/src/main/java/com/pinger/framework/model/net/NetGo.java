package com.pinger.framework.model.net;

import android.content.Context;

import com.pinger.framework.MVPApplication;
import com.pinger.framework.model.api.HomeApis;
import com.pinger.framework.model.constant.UrlConstant;
import com.pinger.framework.model.net.cookie.ClearableCookieJar;
import com.pinger.framework.model.net.cookie.PersistentCookieJar;
import com.pinger.framework.model.net.cookie.SetCookieCache;
import com.pinger.framework.model.net.cookie.SharedPrefsCookiePersistor;
import com.pinger.framework.model.net.intercept.BasicParamsInterceptor;
import com.pinger.framework.model.net.intercept.HttpLoggingInterceptor;
import com.pinger.framework.model.net.intercept.RewriteCacheControlInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Pinger
 * @since 2017/3/20 0020 下午 12:07
 * Retrofit加载数据的接口服务
 */
public class NetGo {

    //查询网络的Cache-Control设置
    //(假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)
    private static final String CACHE_CONTROL_NETWORK = "Cache-Control: public, max-age=3600";

    // 避免出现 HTTP 403 Forbidden，参考：http://stackoverflow.com/questions/13670692/403-forbidden-with-java-but-not-web-browser
    private static final String AVOID_HTTP403_FORBIDDEN = "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    private static HomeApis mHomeApis;

    private NetGo() {
        throw new AssertionError();
    }

    public static void init(Context context) {
        // 持久化cookie
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        // 公共参数
        BasicParamsInterceptor basicParamsInterceptor =
                new BasicParamsInterceptor.Builder()
                        .addHeader("device_id", "123")
                        .addBodyParam("uid", "456")
                        .addParams("api_version", "1.1")
                        .build();

        // 指定缓存路径,缓存大小100Mb
        Cache cache = new Cache(new File(MVPApplication.getContext().getCacheDir(), "HttpCache"),
                1024 * 1024 * 100);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(cache)
                .retryOnConnectionFailure(true)

                // addInterceptor:设置应用拦截器，可用于设置公共参数，头信息，日志拦截等
                .addInterceptor(basicParamsInterceptor)
                .addInterceptor(new HttpLoggingInterceptor())
                // 网络拦截器，可以用于重试或重写
                .addNetworkInterceptor(new RewriteCacheControlInterceptor())
                .connectTimeout(5, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(UrlConstant.TEST_HOME)
                .build();



        mHomeApis = retrofit.create(HomeApis.class);

    }


    /**
     * =============================== API =================================
     */

    public static HomeApis getHomeApis() {
        System.out.println("============获取API===========");


        return mHomeApis;
    }


}

package com.pinger.framework.model.net.intercept;

import android.util.Log;

import com.pinger.framework.MVPApplication;
import com.pinger.framework.utils.NetUtil;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Pinger
 * @since 2017/3/21 0021 上午 11:09
 * 云端响应头拦截器，用来配置缓存策略
 * Dangerous interceptor that rewrites the server's cache-control header.
 */

public class RewriteCacheControlInterceptor implements Interceptor {

    private static final String TAG = RewriteCacheControlInterceptor.class.getSimpleName();

    //设缓存有效期为1天
    private static final long CACHE_STALE_SEC = 60 * 60 * 24;
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetUtil.isNetworkAvailable(MVPApplication.getContext())) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            Log.e(TAG, "当前网络无连接，请检查网络！");
        }
        Response originalResponse = chain.proceed(request);

        if (NetUtil.isNetworkAvailable(MVPApplication.getContext())) {
            // 有网的时候读接口上的@Headers里的配置，可以在这里进行统一的设置
            String cacheControl = request.cacheControl().toString();
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();
        } else {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, " + CACHE_CONTROL_CACHE)
                    .removeHeader("Pragma")
                    .build();
        }
    }
}

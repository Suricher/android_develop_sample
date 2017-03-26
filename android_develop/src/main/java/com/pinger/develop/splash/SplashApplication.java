package com.pinger.develop.splash;

import android.app.Application;
import android.util.Log;


/**
 * @author Pinger
 * @since 2017/3/26 11:06
 */

public class SplashApplication extends Application {

    private static final String TAG = SplashApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        initSDK();
    }

    /**
     * 初始化第三方SDK，耗时操作
     */
    private void initSDK() {
        Log.d(TAG, "==========start--初始化第三方SDK--start==========");
        // 启动服务去做耗时操作
        InitializeService.start(this);

        // 加载MainActivity初始化后就要用到的SDK
        // TODO
    }
}

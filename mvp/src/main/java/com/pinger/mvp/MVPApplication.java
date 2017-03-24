package com.pinger.mvp;

import android.app.Application;
import android.content.Context;

import com.pinger.mvp.dagger.component.AppComponent;
import com.pinger.mvp.dagger.component.DaggerAppComponent;
import com.pinger.mvp.dagger.module.AppModule;
import com.pinger.mvp.model.net.NetGo;

/**
 * @author Pinger
 * @since 2017/3/19 0019 上午 11:38
 */

public class MVPApplication extends Application {

    private AppComponent mAppComponent;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .build();

        initConfig();
    }

    private void initConfig() {
        NetGo.init(getContext());
    }

    public static Context getContext() {
        return mContext;
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }


}

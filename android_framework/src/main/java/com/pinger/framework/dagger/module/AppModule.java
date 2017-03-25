package com.pinger.framework.dagger.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 4:03
 * 全局的Application，统一创建单利的类，和Application关联
 */
@Module
public class AppModule {

    private final Context mContext;

    public AppModule(Context context) {
        this.mContext = context;
    }

    /**
     * 全局唯一的单利Context
     *
     * @return
     */
    @Singleton
    @Provides
    Context provideContext() {
        return mContext;
    }


}

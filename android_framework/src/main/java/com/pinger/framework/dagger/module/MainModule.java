package com.pinger.framework.dagger.module;

import com.pinger.framework.presenter.contract.MainContract;

import dagger.Module;
import dagger.Provides;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 1:56
 */
@Module
public class MainModule {
    private MainContract.View mView;

    public MainModule(MainContract.View view) {
        this.mView = view;
    }

    @Provides
    MainContract.View providerMainView() {
        return mView;
    }
}

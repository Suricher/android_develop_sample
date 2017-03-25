package com.pinger.mvp.dagger.module;

import com.pinger.mvp.presenter.contract.MainContract;

import dagger.Module;
import dagger.Provides;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 2:46
 */
@Module
public class HomeModule {

    private final MainContract.View mView;

    public HomeModule(MainContract.View view) {
        this.mView = view;
    }

    @Provides
    public MainContract.View providerHomeView() {
        return mView;
    }
}

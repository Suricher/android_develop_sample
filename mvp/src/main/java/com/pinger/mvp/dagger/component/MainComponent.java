package com.pinger.mvp.dagger.component;

import com.pinger.mvp.activity.MainActivity;
import com.pinger.mvp.dagger.module.MainModule;
import com.pinger.mvp.dagger.scope.ActivityScope;

import dagger.Component;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 2:03
 */

@ActivityScope
@Component(dependencies = AppComponent.class, modules = MainModule.class)
public interface MainComponent {
    void inject(MainActivity activity);
}

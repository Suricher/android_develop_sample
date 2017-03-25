package com.pinger.framework.dagger.component;

import com.pinger.framework.activity.MainActivity;
import com.pinger.framework.dagger.module.MainModule;
import com.pinger.framework.dagger.scope.ActivityScope;

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

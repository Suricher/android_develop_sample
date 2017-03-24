package com.pinger.mvp.dagger.component;

import com.pinger.mvp.dagger.module.HomeModule;
import com.pinger.mvp.dagger.scope.FragmentScope;
import com.pinger.mvp.fragment.HomeFragment;

import dagger.Component;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 2:49
 */

@FragmentScope
@Component(dependencies = AppComponent.class, modules = HomeModule.class)
public interface HomeComponent {
    void inject(HomeFragment fragment);
}

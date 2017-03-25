package com.pinger.framework.dagger.component;

import com.pinger.framework.dagger.module.HomeModule;
import com.pinger.framework.dagger.scope.FragmentScope;
import com.pinger.framework.fragment.HomeFragment;

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

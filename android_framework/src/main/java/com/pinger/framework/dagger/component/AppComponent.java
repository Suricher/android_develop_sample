package com.pinger.framework.dagger.component;

import com.pinger.framework.dagger.module.AppModule;
import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 4:34
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

}

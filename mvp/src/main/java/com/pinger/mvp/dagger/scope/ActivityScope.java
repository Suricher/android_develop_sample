package com.pinger.mvp.dagger.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 4:01
 * 自定义Activity的生命域，可以规定共享对象的作用域
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScope {
}
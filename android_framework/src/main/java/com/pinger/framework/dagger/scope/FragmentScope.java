package com.pinger.framework.dagger.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 5:56
 * Fragment的作用域
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface FragmentScope {
}
package com.pinger.framework.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 12:51
 */
public abstract class BaseActivity extends AppCompatActivity {

    // 判断View是否被激活
    protected boolean mActive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mActive = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mActive = false;
    }
}

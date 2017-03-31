package com.pinger.framework.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 1:03
 */
public abstract class BaseFragment extends Fragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected abstract <T extends BaseFragment > T getInstance();



}

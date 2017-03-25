package com.pinger.framework.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.pinger.framework.MVPApplication;
import com.pinger.framework.R;
import com.pinger.framework.base.BaseActivity;
import com.pinger.framework.dagger.component.DaggerMainComponent;
import com.pinger.framework.dagger.module.MainModule;
import com.pinger.framework.model.bean.BannerBean;
import com.pinger.framework.presenter.MainPresenter;
import com.pinger.framework.presenter.contract.MainContract;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MainContract.View {

    @Inject
    MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getFragmentManager().beginTransaction().replace(R.id.container, HomeFragment.newInstance()).commitAllowingStateLoss();
        DaggerMainComponent
                .builder()
                .appComponent(((MVPApplication) getApplication()).getAppComponent())
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showContent(List<BannerBean> beanList) {
        Toast.makeText(this, beanList.get(0).title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isActive() {
        return mActive;
    }
}

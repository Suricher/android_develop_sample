package com.pinger.mvp.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.pinger.mvp.MVPApplication;
import com.pinger.mvp.R;
import com.pinger.mvp.base.BaseActivity;
import com.pinger.mvp.dagger.component.DaggerMainComponent;
import com.pinger.mvp.dagger.module.MainModule;
import com.pinger.mvp.fragment.HomeFragment;
import com.pinger.mvp.model.bean.BannerBean;
import com.pinger.mvp.presenter.MainPresenter;
import com.pinger.mvp.presenter.contract.MainContract;

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

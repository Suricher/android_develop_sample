package com.pinger.mvp.presenter;

import com.pinger.mvp.base.BaseModel;
import com.pinger.mvp.base.BaseRxPresenter;
import com.pinger.mvp.model.bean.BannerBean;
import com.pinger.mvp.model.net.NetGo;
import com.pinger.mvp.presenter.contract.MainContract;
import com.pinger.mvp.utils.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 1:09
 */
public class MainPresenter extends BaseRxPresenter implements MainContract.Presenter {

    private MainContract.View mainView;

    @Inject
    public MainPresenter(MainContract.View mainView) {
        this.mainView = mainView;
    }

    @Override
    public void start() {
        loadData();
    }

    @Override
    public void loadData() {

        System.out.println("=============开始加载数据===========");

        Subscription subscript = NetGo.getHomeApis().getBannerList()
                .compose(RxUtil.<BaseModel<List<BannerBean>>>scheduleTransformer())
                .compose(RxUtil.<List<BannerBean>>handleResult())
                .subscribe(new Action1<List<BannerBean>>() {
                    @Override
                    public void call(List<BannerBean> bannerBeen) {
                        if (mainView.isActive()) {

                            System.out.println("============显示数据===========");


                            mainView.showContent(bannerBeen);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        mainView.showToast("加载出错");
                    }
                });
        addSubscribe(subscript);
    }
}

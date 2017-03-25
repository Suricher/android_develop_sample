package com.pinger.framework.presenter.contract;

import com.pinger.framework.base.BasePresenter;
import com.pinger.framework.base.BaseView;
import com.pinger.framework.model.bean.BannerBean;

import java.util.List;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 1:07
 */
public interface MainContract {

    interface View extends BaseView{
        void showToast(String msg);

        void showContent(List<BannerBean> beanList);
    }

    interface Presenter extends BasePresenter {

        void loadData();
    }
}

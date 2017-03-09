package com.pinger.test.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pinger.swipeview.adapter.BaseRecyclerAdapter;
import com.pinger.swipeview.adapter.BaseViewHolder;
import com.pinger.test.R;
import com.pinger.test.dao.BannerBean;
import com.pinger.widget.view.banner.BannerBaseAdapter;
import com.pinger.widget.view.banner.BannerView;

/**
 * @author Pinger
 * @since 2017/3/9 0009 下午 5:39
 */
public class MainAdapter extends BaseRecyclerAdapter<BannerBean> {

    public MainAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BannerHolder(parent);
    }

    private class BannerHolder extends BaseViewHolder<BannerBean> {

        private final BannerView mBannerView;
        private final BannerAdapter mAdapter;

        BannerHolder(ViewGroup parent) {
            super(parent, R.layout.holder_banner);

            mBannerView = $(R.id.bannerView);
            mBannerView.setAnimationDuration(1000);
            mBannerView.startAutoScroll();
            mAdapter = new BannerAdapter(getContext());
            mBannerView.setAdapter(mAdapter);

            mAdapter.setOnPageTouchListener(new BannerBaseAdapter.OnPageTouchListener() {
                @Override
                public void onPageClick(int position, Object obj) {
                    Toast.makeText(getContext(), "位置" + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPageDown() {
                    mBannerView.stopAutoScroll();
                }

                @Override
                public void onPageUp() {
                    mBannerView.startAutoScroll();
                }
            });

        }

        @Override
        protected void setData(BannerBean data) {
            mAdapter.setData(data.data);

            mBannerView.setCurrentPosition(data.data.size());
        }
    }
}

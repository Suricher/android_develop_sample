package com.pinger.test.ui.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pinger.swipeview.adapter.BaseRecyclerAdapter;
import com.pinger.test.R;
import com.pinger.test.dao.BannerBean;
import com.pinger.test.ui.adapter.BannerAdapter;
import com.pinger.widget.view.banner.BannerBaseAdapter;
import com.pinger.widget.view.banner.BannerView;

import java.util.List;

/**
 * @author Pinger
 * @since 2017/3/9 0009 下午 5:52
 */
public class BannerHeadView implements BaseRecyclerAdapter.ItemView {

    private List<String> mDatas;
    private BannerView mBannerView;
    private BannerAdapter mAdapter;

    @Override
    public View onCreateView(final ViewGroup parent) {
        View holderView = View.inflate(parent.getContext(), R.layout.holder_banner, null);

        mBannerView = (BannerView) holderView.findViewById(R.id.bannerView);

        mBannerView.setAnimationDuration(1000);
        mBannerView.startAutoScroll();
        mAdapter = new BannerAdapter(parent.getContext());
        mBannerView.setAdapter(mAdapter);

        mAdapter.setOnPageTouchListener(new BannerBaseAdapter.OnPageTouchListener() {
            @Override
            public void onPageClick(int position, Object obj) {
                Toast.makeText(parent.getContext(), "位置" + position, Toast.LENGTH_SHORT).show();
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

        return holderView;
    }

    @Override
    public void onBindView(View view) {
        mAdapter.setData(mDatas);
        mBannerView.setCurrentPosition(mDatas.size());
    }

    public void setData(List<BannerBean> datas) {
        mDatas = datas.get(0).data;
    }
}

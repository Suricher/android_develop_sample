package com.pinger.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.pinger.test.ui.adapter.BannerAdapter;
import com.pinger.widget.view.banner.BannerBaseAdapter;
import com.pinger.widget.view.banner.BannerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Object> mDatas = new ArrayList<>();
    private BannerBaseAdapter mAdapter;
    private BannerView mBannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBannerView = (BannerView) findViewById(R.id.bannerView);
        mBannerView.setAdapter(mAdapter = new BannerAdapter(this, mDatas));
        mBannerView.startAutoScroll();
        mBannerView.setAnimationDuration(1000);

        // 加载数据
        initData();
        initEvent();

        // 更新数据，重新设置当前的位置
        mAdapter.setNewData(mDatas);
        mBannerView.setCurrentPosition(mDatas);

    }

    private void initEvent() {
        mAdapter.setOnPageTouchListener(new BannerBaseAdapter.OnPageTouchListener() {
            @Override
            public void onPageClick(int position, Object obj) {
                Toast.makeText(MainActivity.this, "点击了条目" + position, Toast.LENGTH_SHORT).show();
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

    private void initData() {
        mDatas.add("http://bpic.588ku.com/element_banner/20/17/02/6ef5cd8073f1fd94eb00eb538a76ed8b.jpg");
        mDatas.add("http://bpic.588ku.com/back_pic/04/71/16/86589a94f5aae1d.jpg");
        mDatas.add("http://bpic.588ku.com/back_pic/00/02/87/48561ba9a32f297.jpg");
        mDatas.add("http://bpic.588ku.com/back_pic/02/64/88/4957863e5539451.jpg");
        mDatas.add("http://bpic.588ku.com/back_pic/03/62/43/7257aacb5e8d877.jpg");
    }
}

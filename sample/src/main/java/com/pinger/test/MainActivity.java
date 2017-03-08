package com.pinger.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.pinger.test.ui.adapter.BannerAdapter;
import com.pinger.widget.banner.BannerBaseAdapter;
import com.pinger.widget.banner.BannerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Object> mDatas = new ArrayList<>();
    private BannerBaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BannerView bannerView = (BannerView) findViewById(R.id.bannerView);
        bannerView.setAdapter(mAdapter = new BannerAdapter(this, mDatas));
        bannerView.startAutoScroll();
        bannerView.setAnimationDuration(1000);

        // 加载数据
        initData();
        initEvent();

        // 更新数据，重新设置当前的位置
        mAdapter.setNewData(mDatas);
        bannerView.setCurrentPosition(mDatas);

    }

    private void initEvent() {
        mAdapter.setOnPageClickListener(new BannerBaseAdapter.OnPageClickListener() {
            @Override
            public void onPageClick(int position, Object obj) {
                Toast.makeText(MainActivity.this, "点击了条目" + position, Toast.LENGTH_SHORT).show();
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

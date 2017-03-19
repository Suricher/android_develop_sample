package com.pinger.test.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.pinger.swipeview.SwipeRecyclerView;
import com.pinger.swipeview.swipe.OnRefreshListener;
import com.pinger.swipeview.swipe.SwipeRefreshLayout;
import com.pinger.test.R;
import com.pinger.test.dao.BannerBean;
import com.pinger.test.ui.adapter.MainAdapter;
import com.pinger.test.ui.holder.BannerHeadView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<BannerBean> mDatas = new ArrayList<>();
    private MainAdapter mAdapter;
    private SwipeRecyclerView mSwipeRecyclerView;
    private BannerHeadView mHeadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeRecyclerView = (SwipeRecyclerView) findViewById(R.id.swipeRecyclerView);
        mSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSwipeRecyclerView.setAdapterWithProgress(mAdapter = new MainAdapter(this));
        // 加载数据
        mHeadView = new BannerHeadView();
        mAdapter.addHeader(mHeadView);

        initData();
        initEvent();
    }

    private void initEvent() {
        mSwipeRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(SwipeRefreshLayout swipeRefreshLayout) {
                initData();
                swipeRefreshLayout.finishRefreshing();
            }

            @Override
            public void onLoadMore(SwipeRefreshLayout refreshLayout) {
                initData();
                refreshLayout.finishLoadMore();

            }
        });
    }

    private void initData() {
        List<String> datas = new ArrayList<>();
        datas.add("http://bpic.588ku.com/element_banner/20/17/02/6ef5cd8073f1fd94eb00eb538a76ed8b.jpg");
        datas.add("http://bpic.588ku.com/back_pic/04/71/16/86589a94f5aae1d.jpg");
        datas.add("http://bpic.588ku.com/back_pic/00/02/87/48561ba9a32f297.jpg");
        datas.add("http://bpic.588ku.com/back_pic/02/64/88/4957863e5539451.jpg");
        datas.add("http://bpic.588ku.com/back_pic/03/62/43/7257aacb5e8d877.jpg");
        mDatas.add(new BannerBean(datas));

        // 更新数据
        mAdapter.clear();
        mAdapter.addAll(mDatas);
        mHeadView.setData(mDatas);

    }
}

package com.pinger.test.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.pinger.widget.R;
import com.pinger.widget.banner.BannerBaseAdapter;
import com.pinger.widget.banner.BannerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class BannerActivity extends AppCompatActivity {

    private List<BannerBean> datas = new ArrayList<>();
    private BannerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        initBanner();
        initData();
    }

    private void initBanner() {
        final BannerView bannerView = (BannerView) findViewById(R.id.bannerView);
        bannerView.setAdapter(mAdapter = new BannerAdapter(this));
        mAdapter.setOnPageTouchListener(new BannerBaseAdapter.OnPageTouchListener<BannerBean>() {
            @Override
            public void onPageClick(int position, BannerBean bannerBean) {
                // 页面点击
                Toast.makeText(BannerActivity.this, bannerBean.title, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageDown() {
                // 按下，可以停止轮播
                //   Toast.makeText(BannerActivity.this, "按下", Toast.LENGTH_SHORT).show();
                bannerView.stopAutoScroll();
            }

            @Override
            public void onPageUp() {
                // 抬起开始轮播
                //  Toast.makeText(BannerActivity.this, "抬起", Toast.LENGTH_SHORT).show();
                bannerView.startAutoScroll();
            }
        });
    }

    private void initData() {
        datas.clear();

        datas.add(new BannerBean(R.mipmap.page1, "第一页"));
        datas.add(new BannerBean(R.mipmap.page2, "第二页"));
        datas.add(new BannerBean(R.mipmap.page3, "第三页"));
        datas.add(new BannerBean(R.mipmap.page4, "第四页"));
        datas.add(new BannerBean(R.mipmap.page5, "第五页"));
        datas.add(new BannerBean(R.mipmap.page6, "第六页"));

        // 数据更新之后需要设置
        mAdapter.setData(datas);
    }


    private class BannerAdapter extends BannerBaseAdapter<BannerBean> {

        public BannerAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getLayoutResID() {
            return R.layout.item_banner;
        }

        @Override
        protected void convert(View convertView, BannerBean data) {
            setImage(R.id.pageImage, data.imageRes);
            setText(R.id.pageText, data.title);
        }
    }


    private class BannerBean implements Serializable {
        public String imageUrl;
        public String title;
        public @IntegerRes int imageRes;

        public BannerBean(int imageRes, String title) {
            this.imageRes = imageRes;
            this.title = title;
        }
        // 图片宽高传过来时一般是统一的，所以这里不处理
    }
}

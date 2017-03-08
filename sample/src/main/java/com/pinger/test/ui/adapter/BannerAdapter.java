package com.pinger.test.ui.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.pinger.library.ImageGo;
import com.pinger.test.R;
import com.pinger.widget.view.banner.BannerBaseAdapter;

import java.util.List;

/**
 * @author Pinger
 * @since 2017/3/8 0008 下午 6:57
 */
public class BannerAdapter extends BannerBaseAdapter {

    public BannerAdapter(Context context, List<Object> datas) {
        super(context, datas);
    }

    @Override
    protected void performHolder(View convertView, Object object, int position) {
        ImageView pageView = (ImageView) convertView.findViewById(R.id.pageView);
        ImageGo.load(pageView, (String) object);
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.holder_banner_view;
    }
}

package com.pinger.test.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.pinger.library.ImageGo;
import com.pinger.test.R;
import com.pinger.widget.view.banner.BannerBaseAdapter;

/**
 * @author Pinger
 * @since 2017/3/8 0008 下午 6:57
 */
public class BannerAdapter extends BannerBaseAdapter<String> {

    public BannerAdapter(Context context) {
        super(context, R.layout.holder_banner_view);
    }


    @Override
    protected void convert(View convertView, String data) {
        ImageGo.load((ImageView) getView(R.id.pageView), data);
    }
}

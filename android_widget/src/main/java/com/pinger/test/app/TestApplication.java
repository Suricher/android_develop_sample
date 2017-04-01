package com.pinger.test.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pinger.widget.R;
import com.pinger.widget.imagepreview.ImagePreview;

/**
 * @author Pinger
 * @since 2017/3/28 0028 上午 11:02
 */
public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImagePreview.setImageLoader(new ImagePreview.ImageLoader() {
            @Override
            public void onDisplayImage(Context context, ImageView imageView, String url) {
                Glide.with(context).load(url)//
                        .placeholder(R.color.ic_default_placeholder)//
                        .error(R.color.ic_default_placeholder)//
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//
                        .into(imageView);
            }

            @Override
            public Bitmap getCacheImage(String url) {
                return null;
            }
        });
    }
}

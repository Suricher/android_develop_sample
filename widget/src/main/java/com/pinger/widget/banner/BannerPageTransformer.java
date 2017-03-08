package com.pinger.widget.banner;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 轮播动画实现，改造自系统的ZoomOutPageTransformer类
 * 切换时左右进行缩放
 */
public class BannerPageTransformer implements ViewPager.PageTransformer {

    // 缩放和透明比例，需要自己修改
    private float scaleMin = 0.8f;
    private float alphaMin = 0.8f;

    @Override
    public void transformPage(View page, float position) {
        // 不同位置的缩放和透明度
        float scale = (position < 0)
                ? ((1 - scaleMin) * position + 1)
                : ((scaleMin - 1) * position + 1);
        float alpha = (position < 0)
                ? ((1 - alphaMin) * position + 1)
                : ((alphaMin - 1) * position + 1);
        // 保持左右两边的图片位置中心
        if (position < 0) {
            ViewCompat.setPivotX(page, page.getWidth());
            ViewCompat.setPivotY(page, page.getHeight() / 2);
        } else {
            ViewCompat.setPivotX(page, 0);
            ViewCompat.setPivotY(page, page.getHeight() / 2);
        }
        ViewCompat.setScaleX(page, scale);
        ViewCompat.setScaleY(page, scale);
        ViewCompat.setAlpha(page, Math.abs(alpha));
    }
}

package com.pinger.widget.view.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.pinger.widget.R;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Pinger
 * @since 2017/3/8 0008 下午 3:26
 * 自定义无限轮转的海报控件
 */
public class BannerView extends FrameLayout {

    private ViewPager mViewPager;

    // 页面边距
    private int pageMargin = 15;
    // 页面显示屏幕占比
    private float pagePercent = 0.8f;
    // 自动轮播间隔时长
    private long mDuration = 4000;
    // 当前滑动的状态
    private boolean isAutoScrolling;
    // 当前的条目位置
    private int mCurrentPosition;


    private View mRootView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private AutoScrollTask mScrollTask;
    private long mRecentTouchTime;


    public BannerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initEvent();
    }


    private void initView() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_banner_view, this);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);

        // 注意clipChildren属性的使用
        // 初始化ViewPager
        LayoutParams params = (LayoutParams) mViewPager.getLayoutParams();
        params.width = (int) (getScreenWidth() * pagePercent);
        params.gravity = Gravity.CENTER;
        mViewPager.setLayoutParams(params);
        mViewPager.setPageMargin(pageMargin);
        mViewPager.setPageTransformer(false, new BannerPageTransformer());

        // 自动轮播任务
        mScrollTask = new AutoScrollTask();
    }


    private void initEvent() {
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        stopAutoScroll();
                        break;
                    case MotionEvent.ACTION_UP:
                        startAutoScroll();
                        break;
                }
                return false;
            }
        });

        // 父亲将触摸事件交给孩子处理
        mRootView.findViewById(R.id.viewPager_container).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mViewPager.onInterceptTouchEvent(motionEvent);
            }
        });
    }


    private int getScreenWidth() {
        return ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    }

    /**
     * 自动轮播任务
     */
    private class AutoScrollTask implements Runnable {

        @Override
        public void run() {
            int currentPosition = mViewPager.getCurrentItem();

            if (mViewPager.getAdapter() != null) {
                if (currentPosition == mViewPager.getAdapter().getCount() - 1) {
                    // 最后一页
                    mViewPager.setCurrentItem(0);
                } else {
                    mViewPager.setCurrentItem(currentPosition + 1);
                }
            }

            // 记录当前的页面位置
            mCurrentPosition = currentPosition;


            System.out.println("=======================当前第" + mCurrentPosition + "页=====================");

            // 一直给自己发消息
            mHandler.postDelayed(this, mDuration);
        }

        void start() {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(this, mDuration);
        }

        void stop() {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 设置滑动动画持续时间
     *
     * @param during
     */
    public void setAnimationDuration(final int during) {
        try {
            // viewPager平移动画事件
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            Scroller mScroller = new Scroller(getContext(),
                    // 动画效果与ViewPager的一致
                    new Interpolator() {
                        public float getInterpolation(float t) {
                            t -= 1.0f;
                            return t * t * t * t * t + 1.0f;
                        }
                    }) {

                @Override
                public void startScroll(int startX, int startY, int dx,
                                        int dy, int duration) {
                    // 如果手动滚动,则加速滚动
                    if (System.currentTimeMillis() - mRecentTouchTime > mDuration) {
                        // 动画滑动
                        duration = during;
                    } else {
                        // 手势滚动
                        duration /= 2;

                    }
                    super.startScroll(startX, startY, dx, dy, duration);
                }

                @Override
                public void startScroll(int startX, int startY, int dx,
                                        int dy) {
                    super.startScroll(startX, startY, dx, dy, during);
                }
            };
            mField.set(mViewPager, mScroller);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mRecentTouchTime = System.currentTimeMillis();
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * ==================================API==================================
     */

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public void setAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
    }

    public void startAutoScroll() {
        if (mScrollTask == null) return;
        isAutoScrolling = true;
        mScrollTask.start();
    }

    public void stopAutoScroll() {
        if (mScrollTask == null) return;
        isAutoScrolling = false;
        mScrollTask.stop();
    }

    public boolean isAutoScrolling() {
        return isAutoScrolling;
    }

    public void setCurrentPosition(List<Object> datas) {
        setCurrentPosition(datas, 0);
    }

    public void setCurrentPosition(List<Object> datas, int position) {
        if (datas == null) return;
        // 获取居中的位置
        int midPosition;
        if (datas.size() == 0) {
            midPosition = Integer.MAX_VALUE / 2;
        } else {
            midPosition = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % datas.size();
        }

        if (mCurrentPosition != 0) {
            // 回到之前停留的页面
            mViewPager.setCurrentItem(mCurrentPosition + 1);
        } else {
            // 设置回第0页
            mViewPager.setCurrentItem(midPosition + position);
        }

        mViewPager.setOffscreenPageLimit(datas.size());

    }


}

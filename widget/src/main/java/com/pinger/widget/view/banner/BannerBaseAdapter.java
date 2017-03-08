package com.pinger.widget.view.banner;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author Pinger
 * @since 2017/3/8 0008 下午 6:29
 * ViewPager基类适配器，需要传入Item视图和数据
 */
public abstract class BannerBaseAdapter extends PagerAdapter {

    private Context mContext;
    private List<Object> mDatas;
    private OnPageTouchListener mListener;
    private long mDownTime;


    public BannerBaseAdapter(Context context, List<Object> datas) {
        this.mContext = context;
        this.mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas == null || mDatas.size() == 0 ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = LayoutInflater.from(mContext).inflate(getItemLayoutRes(), null);
        if (mDatas != null && mDatas.size() != 0) {
            position = position % mDatas.size();
        }

        Object object = null;
        if (mDatas != null) {
            object = mDatas.get(position);
            if (object != null) {
                // 处理视图和数据
                performHolder(convertView, object, position);
            }
        }

        final Object finalObject = object;
        final int finalPosition = position;


        // 处理条目的触摸事件
        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        mDownTime = System.currentTimeMillis();
                        if (mListener != null) {
                            mListener.onPageDown();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        long upTime = System.currentTimeMillis();
                        if (mListener != null) {
                            mListener.onPageUp();
                        }
                        if (upTime - mDownTime < 500) {
                            // 500毫秒以内就算单击
                            if (mListener != null && finalObject != null) {
                                mListener.onPageClick(finalPosition, finalObject);
                            }
                        }
                        break;
                }
                return false;
            }
        });


        container.addView(convertView);
        return convertView;
    }

    public void setNewData(List<Object> datas) {
        if (datas == null) return;
        mDatas = datas;
        notifyDataSetChanged();
    }

    public void setOnPageTouchListener(OnPageTouchListener listener) {
        this.mListener = listener;
    }

    /**
     * 条目页面的触摸事件
     */
    public interface OnPageTouchListener {
        void onPageClick(int position, Object obj);

        void onPageDown();

        void onPageUp();
    }

    // 绑定视图和数据
    protected abstract void performHolder(View convertView, Object object, int position);

    // 获取条目视图id
    protected abstract int getItemLayoutRes();
}

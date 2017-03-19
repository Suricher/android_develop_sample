package com.pinger.widget.view.banner;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Pinger
 * @since 2017/3/8 0008 下午 6:29
 * ViewPager基类适配器，需要传入Item视图和数据
 */
public abstract class BannerBaseAdapter<T> extends PagerAdapter {

    private Context mContext;
    private List<T> mDatas;
    private OnPageTouchListener mListener;
    private long mDownTime;
    private int mLayoutResID;
    private View mConvertView;

    public BannerBaseAdapter(Context context, int layoutResID) {
        init(context, new ArrayList<T>(), layoutResID);
    }

    public BannerBaseAdapter(Context context, T[] objects, int layoutResID) {
        init(context, Arrays.asList(objects), layoutResID);
    }

    public BannerBaseAdapter(Context context, List<T> datas, int layoutResID) {
        init(context, datas, layoutResID);
    }


    private void init(Context context, List<T> datas, int layoutResID) {
        mContext = context;
        mDatas = new ArrayList<>(datas);
        mLayoutResID = layoutResID;
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


    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mConvertView = LayoutInflater.from(mContext).inflate(mLayoutResID, null);

        if (mDatas != null && mDatas.size() != 0) {
            position = position % mDatas.size();
        }

        if (mDatas != null) {
            // 处理视图和数据
            convert(mConvertView, getItem(position));
        }

        final int finalPosition = position;
        // 处理条目的触摸事件
        mConvertView.setOnTouchListener(new View.OnTouchListener() {
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
                            if (mListener != null && getItem(finalPosition) != null) {
                                mListener.onPageClick(finalPosition, getItem(finalPosition));
                            }
                        }
                        break;
                }
                return false;
            }
        });

        container.addView(mConvertView);
        return mConvertView;
    }

    public void setData(List<T> datas) {
        if (datas == null) return;
        this.mDatas = new ArrayList<>(datas);
        notifyDataSetChanged();
    }


    public <K extends View> K getView(int viewId) {
        View view = mConvertView.findViewById(viewId);
        return (K) view;
    }

    public BannerBaseAdapter setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public BannerBaseAdapter setImage(int viewId, int drawableId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(drawableId);
        return this;
    }

    public BannerBaseAdapter setImage(int viewId, Bitmap bitmap) {
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 获取Item对象
     *
     * @return
     */
    public View getItemView() {
        return mConvertView;
    }

    // 绑定视图和数据
    protected abstract void convert(View convertView, T data);

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


}

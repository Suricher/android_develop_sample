package com.pinger.widget.banner;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
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
    private OnPageClickListener mListener;


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
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null && finalObject != null) {
                    mListener.onPageClick(finalPosition,finalObject);
                }
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

    public void setOnPageClickListener(OnPageClickListener listener) {
        this.mListener = listener;
    }

    public interface OnPageClickListener {
        void onPageClick(int position,Object obj);
    }

    // 绑定视图和数据
    protected abstract void performHolder(View convertView, Object object, int position);

    // 获取条目视图id
    protected abstract int getItemLayoutRes();
}

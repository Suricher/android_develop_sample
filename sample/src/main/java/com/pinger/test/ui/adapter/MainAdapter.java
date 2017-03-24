package com.pinger.test.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pinger.swipeview.adapter.BaseRecyclerAdapter;
import com.pinger.swipeview.adapter.BaseViewHolder;
import com.pinger.test.R;

/**
 * @author Pinger
 * @since 2017/3/9 0009 下午 5:39
 */
public class MainAdapter extends BaseRecyclerAdapter<String> {

    public MainAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BannerHolder(parent);
    }

    private class BannerHolder extends BaseViewHolder<String> {

        private final TextView mTextView;

        BannerHolder(ViewGroup parent) {
            super(parent, R.layout.holder_text);
            mTextView = $(R.id.text);
        }

        @Override
        protected void setData(String data) {
            mTextView.setText(data);
        }
    }
}

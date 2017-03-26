package com.pinger.develop.splash;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import com.pinger.develop.R;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewStub viewStub = (ViewStub) findViewById(R.id.viewStub);

        Log.d(TAG, "==========start--填充SplashFragment--start==========");
        // 1.初始化SplashFragment，填充Splash
        final SplashFragment splashFragment = new SplashFragment();
        getFragmentManager().beginTransaction().replace(R.id.container, splashFragment).commitAllowingStateLoss();

        // 2.窗体加载完毕的时候,填充主页布局
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                // 填充布局
                viewStub.inflate();
                // 初始化布局
                initView();
                // 2秒后移除Splash
                mHandler.postDelayed(new DelayRunnable(MainActivity.this, splashFragment), 2000);
            }
        });

        // 3. 加载主页数据
        initData();
    }

    private void initView() {
        Log.d(TAG, "==========start--初始化MainActivity布局--start==========");
        TextView text = (TextView) findViewById(R.id.text);
        text.setText("我是主页内容，我终于显示出来了");
    }


    private void initData() {
        Log.d(TAG, "==========start--加载MainActivity数据--start==========");
        Toast.makeText(this, "数据加载完毕", Toast.LENGTH_SHORT).show();
    }

    /**
     * 延时关闭
     */
    private static class DelayRunnable implements Runnable {
        private WeakReference<Context> contextRef;
        private WeakReference<SplashFragment> fragmentRef;

        DelayRunnable(Context context, SplashFragment splashFragment) {
            contextRef = new WeakReference<>(context);
            fragmentRef = new WeakReference<>(splashFragment);
        }

        @Override
        public void run() {
            Activity context = (Activity) contextRef.get();
            if (context != null) {
                SplashFragment splashFragment = fragmentRef.get();
                if (splashFragment == null)
                    return;
                final FragmentTransaction transaction = context.getFragmentManager().beginTransaction();
                transaction.remove(splashFragment);
                transaction.commit();

                Log.d(TAG, "==========start--移除SplashFragment--start==========");
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}

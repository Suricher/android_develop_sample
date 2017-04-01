package com.pinger.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pinger.widget.R;

/**
 * @author Pinger
 * @since 2017/3/27 0027 下午 2:45
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void imagePreview(View view) {
        startActivity(new Intent(this, ImageActivity.class));
    }


    public void banner(View view) {
        startActivity(new Intent(this, BannerActivity.class));
    }

}

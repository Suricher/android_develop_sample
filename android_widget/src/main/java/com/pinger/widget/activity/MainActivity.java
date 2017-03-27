package com.pinger.widget.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pinger.widget.R;
import com.pinger.widget.photoview.PhotoActivity;

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

    public void startPhoto(View view) {
        startActivity(new Intent(this, PhotoActivity.class));
    }
}

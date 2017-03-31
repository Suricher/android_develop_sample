package com.pinger.transition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView1;
    private ImageView mImageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView1 = (ImageView) findViewById(R.id.image1);
        mImageView2 = (ImageView) findViewById(R.id.image2);
    }

    public void start(View view) {
        // 单个动画跳转
        //startActivity(new Intent(this,SecondActivity.class), ActivityOptionsCompat.makeSceneTransitionAnimation(this,mImageView1,"shareImage1").toBundle());


        // 多个动画跳转
        startActivity(new Intent(MainActivity.this, SecondActivity.class),
                ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                        Pair.create((View) mImageView1, "shareImage1"),
                        Pair.create((View) mImageView2, "shareImage2")).toBundle());

    }
}

package com.pinger.widget.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.pinger.widget.R;
import com.pinger.widget.ninegridview.ImageEntity;
import com.pinger.widget.ninegridview.NineGridView;
import com.pinger.widget.ninegridview.NineGridViewClickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pinger
 * @since 2017/3/27 0027 下午 2:45
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImageViewer(1);
    }

    private void initImageViewer(int count) {
        NineGridView nineGridView = (NineGridView) findViewById(R.id.nineGrid);
        List<ImageEntity> imageEntities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ImageEntity entity = new ImageEntity();
            entity.bigImageUrl = "http://s1.dwstatic.com/group1/M00/A1/56/0eb8a499b898cf716f1fa8a30039ab10.jpg";
            entity.thumbnailUrl = "http://s1.dwstatic.com/group1/M00/A1/56/0eb8a499b898cf716f1fa8a30039ab10.jpg";
            entity.imageViewWidth = 600;
            entity.imageViewHeight = 439;
            imageEntities.add(entity);
        }

        nineGridView.setAdapter(new NineGridViewClickAdapter(this, imageEntities));

        // 只有一张图片时
        if (imageEntities.size() == 1) {
            // 适配单张图片
            nineGridView.setSingleImageRatio(imageEntities.get(0).imageViewHeight * 1.0f / imageEntities.get(0).imageViewHeight);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.one:
                initImageViewer(1);
                break;
            case R.id.three:
                initImageViewer(3);
                break;
            case R.id.four:
                initImageViewer(4);
                break;
            case R.id.serven:
                initImageViewer(7);
                break;
            case R.id.nine:
                initImageViewer(9);
                break;
            case R.id.more:
                initImageViewer(16);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.pinger.widget.imagepreview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import com.pinger.widget.imagepreview.preview.ImagePreviewActivity;

import java.io.Serializable;
import java.util.List;

public class ImagePreviewClickAdapter extends ImagePreviewAdapter {

    private int statusHeight;

    public ImagePreviewClickAdapter(Context context, List<ImageEntity> imageInfo) {
        super(context, imageInfo);
        statusHeight = getStatusHeight(context);
    }

    @Override
    protected void onImageItemClick(Context context, ImagePreview nineGridView, int index, List<ImageEntity> imageEntities, List<Rect> imageRects) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, (Serializable) imageEntities);
        bundle.putSerializable(ImagePreviewActivity.IMAGE_RECT, (Serializable) imageRects);
        bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, index);
        intent.putExtras(bundle);
        context.startActivity(intent);
        // 禁用动画
        ((Activity) context).overridePendingTransition(0, 0);

        // 纠正图片位置
        for (int i = 0; i < imageEntities.size(); i++) {
            ImageEntity imageEntity = imageEntities.get(i);
            View imageView;
            if (i < nineGridView.getMaxSize()) {
                imageView = nineGridView.getChildAt(i);
            } else {
                //如果图片的数量大于显示的数量，则超过部分的返回动画统一退回到最后一个图片的位置
                imageView = nineGridView.getChildAt(nineGridView.getMaxSize() - 1);
            }
            imageEntity.imageWidth = imageView.getWidth();
            imageEntity.imageHeight = imageView.getHeight();
            int[] points = new int[2];
            imageView.getLocationInWindow(points);
            // 图片位置
            imageEntity.imageViewX = points[0];
            imageEntity.imageViewY = points[1] - statusHeight;
        }
    }

    /**
     * 获得状态栏的高度
     */
    public int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
}

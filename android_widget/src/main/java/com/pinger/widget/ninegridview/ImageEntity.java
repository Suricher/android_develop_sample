package com.pinger.widget.ninegridview;

import java.io.Serializable;

public class ImageEntity implements Serializable {

    public String thumbnailUrl;
    public String bigImageUrl;
    public int imageViewHeight;  // 图片高度
    public int imageViewWidth;   // 图片宽度
    public int imageViewX;       // 图片x轴位置
    public int imageViewY;       // 图片y轴位置

    @Override
    public String toString() {
        return "ImageEntity{" +
                "imageViewY=" + imageViewY +
                ", imageViewX=" + imageViewX +
                ", imageViewWidth=" + imageViewWidth +
                ", imageViewHeight=" + imageViewHeight +
                ", bigImageUrl='" + bigImageUrl + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}';
    }
}

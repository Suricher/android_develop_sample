package com.pinger.widget.imagepreview;

import java.io.Serializable;

public class ImageEntity implements Serializable {

    public String thumbnailUrl;
    public String bigImageUrl;
    public int imageHeight;      // 图片高度
    public int imageWidth;       // 图片宽度
    public int imageViewX;       // 控件x轴位置
    public int imageViewY;       // 控件y轴位置

    @Override
    public String toString() {
        return "ImageEntity{" +
                "imageViewY=" + imageViewY +
                ", imageViewX=" + imageViewX +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", bigImageUrl='" + bigImageUrl + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}';
    }
}

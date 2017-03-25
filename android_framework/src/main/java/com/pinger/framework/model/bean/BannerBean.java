package com.pinger.framework.model.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Pinger
 * @since 2017/3/19 0019 下午 3:40
 */
public class BannerBean implements Serializable {

    /**
     * id : 7da0b9a59f51558943408903c3060bdb
     * title : 中国军团26枚金牌回顾：张梦雪夺首金女排压轴圆梦
     * order : 0
     * pic : http://img.a8tiyu.com/literal/2016/08/23/201608231350475374.png
     * type : video
     * props : {"category":1,"videos":[],"playType":"2"}
     */

    public String id;
    public String title;   // 标题
    public int order;      // 序号索引
    public String pic;      // 图片url
    public String type;    // 类型，有：ad,inner，
    public PropsBean props;

    public static class PropsBean implements Serializable {
        /**
         * category : 1
         * videos : []
         * playType : 2
         */

        public int category;
        public String pkg_iOS;  // 包名
        public String pkg_Android;  // 包名
        public String url;   // 跳转链接
        public List<VideoBean> videos;   // 视频链接
        public int playType; // 播放类型，1原生，2，3，中间页浏览器

        public static class VideoBean implements Serializable {
            public String thumb; // 视频缩略图
            public String url;
        }


    }
}

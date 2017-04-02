##  Android自定义带动画无限自动轮播的Banner控件
显示的效果请往下看，先说一下需求，可以自动轮播，按下停止轮播，松手开始轮播，不可见时停止轮播，自动轮播时带动画，手动滑动时不带动画，点击时要有水波纹效果，下拉刷新回到第一页，页面不能卡顿等等。参考了一些网上的想法，结合自己的认知，总结如下。

---
#### 先上效果图
<img src="http://upload-images.jianshu.io/upload_images/2786991-34861ad3cdcb4231.gif?imageMogr2/auto-orient/strip"></img>

#### 自定BannerView控件
为了以后开发的方便，这里将Banner封装成一个控件来使用，以后就可以直接在布局里引用。

做这种轮播效果一般采用的都是`ViewPager`，所以这个控件也是对`ViewPager`的封装，为了解耦，这里没有把适配器放在`BannerView`，只是提供了基类适配器和接口，使用很方便。

讲一下具体如何实现封装： 
第一步：自定义控件，实现构造，初始化属性和视图。
* 基于需求，先定义了页面边距，主页面占比，缩放比例，轮播时长，是否动画轮播等等属性，通过引用attrs的方式，在布局里赋值，并且提供set方法在代码里设置。

```
private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        pageMargin = (int) a.getDimension(R.styleable.BannerView_bannerPageMargin, pageMargin);
        pagePercent = a.getFloat(R.styleable.BannerView_bannerPagePercent, pagePercent);
        scaleMin = a.getFloat(R.styleable.BannerView_bannerPageScale, scaleMin);
        alphaMin = a.getFloat(R.styleable.BannerView_bannerPageAlpha, alphaMin);
        ...
        a.recycle();
    }
```

* 定义了基本属性后，将`ViewPager`视图填充到控件容器里，ViewPager布局里需要使用`clipChildren`属性来控制系统绘制View的范围，在`ViewPager`和它的父容器里都设置`clipChildren=false`。这样指定了主页面占比后，左右两边的Page就会进行绘制。
```
    private void initView() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.banner_view, this);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
        LayoutParams params = (LayoutParams) mViewPager.getLayoutParams();
        params.width = (int) (getScreenWidth() * pagePercent);
        params.gravity = Gravity.CENTER;
        mViewPager.setLayoutParams(params);
        mViewPager.setPageMargin(pageMargin);
        mViewPager.setPageTransformer(false, new BannerPageTransformer());
        mViewPager.setOffscreenPageLimit(5);
        // 自动轮播任务
        mScrollTask = new AutoScrollTask();
        // 如果动画轮播
        if (isAnimScroll) {
            setAnimationScroll((int) mAnimDuration);
        }
    }
```

---
第二步：滑动动画实现
* 众所周知，谷歌已经提供了`ViewPager`的滑动动画设置接口`setPageTransformer()`，并且他自己也实现了三种基本的滑动滑动。需要自定义动画时只需实现`ViewPager.PageTransformer`接口，并实现`transformPage()`方法。

* 设计稿中的动画要求是，滑动时左侧缩小，主页随着滑动百分比缩小，右侧随着滑动百分比放大。`transformPage`方法中提供了主页所在的`position=0.0`，左侧位置百分比`position<0`，右侧位置百分比`position>0`，通过这个position可以动态计算出各个页面的缩放比。
```
	public void transformPage(View page, float position) {
	   // 不同位置的缩放和透明度
	   float scale = (position < 0)
	            ? ((1 - scaleMin) * position + 1)
	            : ((scaleMin - 1) * position + 1);
	   float alpha = (position < 0)
	            ? ((1 - alphaMin) * position + 1)
	            : ((alphaMin - 1) * position + 1);
	   // 保持左右两边的图片位置中心
	   if (position < 0) {
	        ViewCompat.setPivotX(page, page.getWidth());
	        ViewCompat.setPivotY(page, page.getHeight() / 2);
	    } else {
	        ViewCompat.setPivotX(page, 0);
	        ViewCompat.setPivotY(page, page.getHeight() / 2);
	    }
	    Log.d(TAG, "transformPage: scale=" + scale);
	    ViewCompat.setScaleX(page, scale);
	    ViewCompat.setScaleY(page, scale);
	    ViewCompat.setAlpha(page, Math.abs(alpha));
	}
```

第三步：无限自动轮播和轮播动画处理
* 无限轮播可用线程池，定时器，`Handler`等等实现，这里采用最简单的Handler实现。首先自定义一个轮播任务，实现Runnable接口，在run方法里使用Handler发送延时消息来不停轮播，并且提供start方法和stop方法开启和停止轮播。在初始化视图完毕时，开启轮播。
```
   @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 视图初始化完毕，开始轮播任务
        if (mScrollTask == null) mScrollTask = new AutoScrollTask();
        if (isAutoScroll) startAutoScroll();
    }
```
* `ViewPager`滑动是没有延时的，谷歌也没有提供具体的接口去实现延时滑动。所以这里使用反射去重新设置`ViewPager`的滑动世间。自动轮播时，需求动画延时滑动，但是手动滑动时需要原生的滑动，所以根据使用时间差和滑动时间来控制自动和手动滑动。
```
 public void startScroll(int startX, int startY, int dx,
                                        int dy, int duration) {
      // 如果手动滚动,则加速滚动
         // TODO 使用这种设置极不稳定，需要抽离
         if (System.currentTimeMillis() - mRecentTouchTime > mScrollDuration && isAnimScroll) {
             // 动画滑动
             duration = during;
         } else {
             // 手势滚动
             duration /= 2;

         }
         super.startScroll(startX, startY, dx, dy, duration);
     }
```

---

#### BannerView的基类适配器封装
* `BannerView`封装的是`ViewPager`，所以基类适配器`BannerBaseAdapter`封装的也是`PagerAdapter`，由于适配器涉及到数据和视图，所以基类里将所有都封装好，只留数据和视图留给子类去实现。除此之外，页面的点击，按下和抬起也在适配器实现并通过接口暴露出来。
* 实现父类适配后，只需要指定数据类型和实现展示视图转换数据的方法，加载完数据之后，通过setData方法来重新更新数据即可。
```
private class BannerAdapter extends BannerBaseAdapter<BannerBean> {
        public BannerAdapter(Context context) {
            super(context);
        }
        @Override
        protected int getLayoutResID() {
            return R.layout.item_banner;
        }
        @Override
        protected void convert(View convertView, BannerBean data) {
            setImage(R.id.pageImage, data.imageRes);
            setText(R.id.pageText, data.title);
        }
  }
```

#### 使用教程
* 拷贝BannerView全路径到布局里，使用attrs指定属性
```
 <com.pinger.widget.banner.BannerView
      android:id="@+id/bannerView"
      android:layout_width="match_parent"
      android:layout_height="200dp"
      app:bannerPageAlpha="1.0"
      app:bannerPageMargin="8dp"
      app:bannerPagePercent="0.8"
      app:bannerPageScale="0.8"
      app:bannerAnimScroll="true"
      app:bannerAutoScroll="true"
      app:bannerScrollDuration="4000"
      app:bannerAnimDuration="1500"/>
```

* 在代码中设置适配器和设置页面的触摸监听，初始化数据之后，更新数据

```
  final BannerView bannerView = (BannerView) findViewById(R.id.bannerView);
  bannerView.setAdapter(mAdapter = new BannerAdapter(this));
  initData();  // 初始化数据
  mAdapter.setData(mDatas);
  mAdapter.setOnPageTouchListener(...);
```

---
[Demo下载地址](https://github.com/PingerOne/android_develop_sample/blob/master/android_widget/src/main/java/com/pinger/widget/banner/BannerView.java)

> 欢迎大家访问我的[简书](http://www.jianshu.com/u/64f479a1cef7)，[博客](http://pingerone.com/)和[GitHub](https://github.com/PingerOne)。
## App启动优化，看淘宝怎么做
项目重构App的启动，之前的处理是使用SplashActivity作为过渡页，然后将Application中的第三方SDK放到IntentService中加载，启动过程中的一些耗时操作也都去掉了，奈何MainActivity中的业务逻辑还是太过复杂，启动仍然比较漫长。研究了一下`淘宝`项目的启动处理，总结出一种非常优化的App启动方案。

### `传统的启动处理`
市面上大部分的App启动处理如出一辙，基本都是先进入SplashActivity，然后Handler延时后跳转到MainActivity。这样做的好处是SplashActivity作为过渡页更加轻量，启动到显示界面的时间更短，给用户较好的体验，而且SplashActivity中也可以提前做一些数据处理，减轻MainActivity的压力。但是加入SplashActivity过渡页也有一些缺点，比如要多绘制一个页面，要使用Intent传递数据，不能减轻MainActivity页面的绘制压力和一些数据加载。
![Alt text](http://oibrygxgr.bkt.clouddn.com/app_lun1.png)


除此之外，Application的处理也是一个很重要的点，因为App启动是先走Application的onCreate方法的，如果onCreate中加载SDK的时间过于漫长，就会导致App启动后显示一段时间白屏页才会进入SplashActivity，当然也可以使用windowBackGround属性避免白屏，但是时间长了总是会影响用户体验的。所以Application中一般都是采用IntentService来加载SDK的。

为了解决App启动慢和SplashActivity过渡启动的缺点，个人去研究了一下淘宝的启动，发现第一次启动耗时大概3秒，然后退出去，再进入时就不会显示Splash，直接进入主页读取缓存显示页面。我猜测淘宝的启动肯定是根据应用的进程是否存活，如果存活就直接进入MainActivity，否则显示Splash，而且淘宝的Splash加载很快，MainActivity的加载也很快，所以淘宝应该是采用了Splash绑定在Mainactivity上的方式，即`SplashFragment+StubView`的方式启动App。


### `优化后的Splash`
传统的App启动方式不足以满足现在用户的体验，用户需要的是加载更快，性能更优的体验，所以这里采用SplashFragment+StubView的方式去实现App的快速启动。
![Alt text](http://oibrygxgr.bkt.clouddn.com/app_lun2.png)

跟传统启动方式最大的不同是，优化后的启动会直接进入MainActivity，然后在MainActivity控制Splash的显示隐藏，这样在填充Splash的时候还以为直接加载MainActivity的数据，当窗体填充完毕之后，就可以绘制MainActivity的布局，然后移除Splash，展示页面，这种方式很好的体现了异步的操作，使得填充和加载分离，启动的速度更快。

既然知道了这种实现方式，那具体代码是如何编写的呢，接下来看一下如何实现。
#### 1. 填充SplashFragment
在MainActivity的onCreate方法中，首先，需要做的就是填充Splash并显示，显示的内容就可以在SplashFragment中编辑。

        log.d(TAG, "==========填充SplashFragment==========");
        // 1.初始化SplashFragment，填充Splash
        final SplashFragment splashFragment = new SplashFragment();
        getFragmentManager().beginTransaction().replace(R.id.container,splashFragment).commitAllowingStateLoss();

#### 2. 窗体部署完毕，填充主页布局
当整个页面最底层DecorView填充完毕之后，就可以开始去填充MainActivity的布局了，并且使用Handler发送延时消息，2秒后移除Splash。当然移除Splash也可以直接在数据加载完毕显示页面之前移除掉。


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


### 3. 加载MainActivity数据
其实加载数据会在第二步之前执行，因为第二步是异步操作，并且加载数据是耗时操作，所以需要更早执行，不要放在第二步里去执行，这里在测试的时候有可能数据先加载完毕，布局才绘制完，调用控件就会有空指针问题，但是实际开发中肯定不会出现这种情况的，MainActivity中的数据加载肯定很耗时。


### `Application的优化`
Application是应用的核心枢纽，应用启动时会先走Application的onCreate方法去初始化一些全局数据，比如SDK等等。但是当全局数据很多时，App的启动就会变得缓慢，因为初始化是同步加载的，所以要想加快启动，就需要将延时操作放到后台去处理。

Android里有一个Service，正好可以做延时处理的操作，这个Service就是IntentService。关于IntentService，不了解的看这里。
[IntentService 示例与详解](http://www.jianshu.com/p/332b6daf91f0)
[IntentService文档](https://developer.android.com/reference/android/app/IntentService.html)

接下来，看一下如何实现。
* 自定义InitializeService继承IntentService，实现构造和方法onHandleIntent

* 在Application的onCreate方法中启动初始化服务


		 // 启动服务去做耗时操作
	     InitializeService.start(this);

	     public static void start(Context context) {
		       Intent intent = new Intent(context, InitializeService.class);
		        intent.setAction(ACTION_APP_LAUNCHER);
		        context.startService(intent);
		 }

* 在onHandleIntent方法中判断Intent，然后去初始化SDK


		 @Override
		  protected void onHandleIntent(@Nullable Intent intent) {
			  if (intent != null) {
		            final String action = intent.getAction();
		            if (ACTION_APP_LAUNCHER.equals(action)) {
		                performInit();
			        }
		      }
	      }
		
		
		    /**
		     * 启动初始化耗时操作
		     */
		    private void performInit() {
		        // 模拟延时加载
		        SystemClock.sleep(2000);
		        Log.d(TAG, "==========初始化第三方SDK结束==========");
	         }



`注意：`
* 在清单文件中配置Application和Service
* 并不是所有的SDK都能放到Service中去初始化的，在MainActivity中用到的还是需要提前初始化


---
> 欢迎大家访问我的[简书](http://www.jianshu.com/u/64f479a1cef7)，[博客](http://pingerone.com/)和[GitHub](https://github.com/PingerOne)。

package com.pinger.develop.splash;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * @author Pinger
 * @since 2017/3/26 11:27
 */

public class InitializeService extends IntentService {

    private static final String TAG = InitializeService.class.getSimpleName();
    public static final String ACTION_APP_LAUNCHER = "action.app.launcher";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public InitializeService() {
        super(InitializeService.class.getSimpleName());
    }

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

    /**
     * 启动调用
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, InitializeService.class);
        intent.setAction(ACTION_APP_LAUNCHER);
        context.startService(intent);
    }


}

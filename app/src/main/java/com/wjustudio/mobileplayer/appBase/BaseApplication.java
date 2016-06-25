package com.wjustudio.mobileplayer.appBase;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * 作者： songwenju on 2016/6/25 11:19.
 * 邮箱： songwenju@outlook.com
 */
public class BaseApplication extends Application {
    private static Context mContext;
    private static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mHandler = new Handler();
    }
    public static Context getContext(){
        return mContext;
    }

    public static Handler getAppHandler(){
        return mHandler;
    }
}

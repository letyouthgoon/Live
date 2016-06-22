package com.showworld.living;

import android.app.Application;
import android.content.Context;

import com.showworld.living.presenters.InitBusinessHelper;
import com.showworld.living.utils.SwlLogImpl;
import com.squareup.leakcanary.LeakCanary;


/**
 * 全局Application
 */
public class SwlApplication extends Application {

    private static SwlApplication mApp;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        mContext = getApplicationContext();

        SwlLogImpl.init(getApplicationContext());

        //初始化APP
        InitBusinessHelper.initApp(mContext);

        LeakCanary.install(this);
    }

    public static Context getmContext() {
        return mContext;
    }

    public static SwlApplication getInstance() {
        return mApp;
    }
}

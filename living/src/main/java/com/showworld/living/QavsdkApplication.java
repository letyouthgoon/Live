package com.showworld.living;

import android.app.Application;
import android.content.Context;

import com.showworld.living.presenters.InitBusinessHelper;
import com.showworld.living.utils.SxbLogImpl;


/**
 * 全局Application
 */
public class QavsdkApplication extends Application {

    private static QavsdkApplication app;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        context = getApplicationContext();

        SxbLogImpl.init(getApplicationContext());

        //初始化APP
        InitBusinessHelper.initApp(context);


//        LeakCanary.install(this);

        //创建AVSDK 控制器类
    }

    public static Context getContext() {
        return context;
    }

    public static QavsdkApplication getInstance() {
        return app;
    }
}

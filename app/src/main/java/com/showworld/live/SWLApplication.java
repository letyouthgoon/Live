package com.showworld.live;

import android.app.Application;

import com.showworld.live.main.control.QavsdkControl;

/**
 * Created by alex on 2016/6/1.
 */
public class SWLApplication extends Application {

    private QavsdkControl mQavsdkControl = null;
    @Override
    public void onCreate() {
        super.onCreate();
        mQavsdkControl = new QavsdkControl(this);
//        DemoCache.setContext(this);
//
//        NIMClient.init(this, getLoginInfo(), getOptions());
//
//        // crash handler
//        AppCrashHandler.getInstance(this);
//
//        if (inMainProcess()) {
//            // 注册自定义消息附件解析器
//            NIMClient.getService(MsgService.class).registerCustomAttachmentParser(FlavorDependent.getInstance().getMsgAttachmentParser());
//            // init tools
//            StorageUtil.init(this, null);
//            ScreenUtil.init(this);
//            DemoCache.initImageLoaderKit();
//
//            // init log
//            initLog();
//            FlavorDependent.getInstance().onApplicationCreate();
//        }
    }

    public QavsdkControl getQavsdkControl() {
        return mQavsdkControl;
    }
//
//    private LoginInfo getLoginInfo() {
//        String account = AuthPreferences.getUserAccount();
//        String token = AuthPreferences.getUserToken();
//
//        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
//            DemoCache.setAccount(account.toLowerCase());
//            return new LoginInfo(account, token);
//        } else {
//            return null;
//        }
//    }
//
//    private SDKOptions getOptions() {
//        SDKOptions options = new SDKOptions();
//
//        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
//        StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
//        if (config == null) {
//            config = new StatusBarNotificationConfig();
//        }
//        // 点击通知需要跳转到的界面
//        config.notificationEntrance = WelcomeActivity.class;
//        config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;
//
//        // 通知铃声的uri字符串
//        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
//        options.statusBarNotificationConfig = config;
//        UserPreferences.setStatusConfig(config);
//
//        // 配置保存图片，文件，log等数据的目录
//        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim/";
//        options.sdkStorageRootPath = sdkPath;
//        Log.i("demo", FlavorDependent.getInstance().getFlavorName() + " demo nim sdk log path=" + sdkPath);
//
//        // 配置数据库加密秘钥
//        options.databaseEncryptKey = "NETEASE";
//
//        // 配置是否需要预下载附件缩略图
//        options.preloadAttach = true;
//
//        // 配置附件缩略图的尺寸大小，
//        options.thumbnailSize = (int) (0.5 * ScreenUtil.screenWidth);
//
//        // 用户信息提供者
//        options.userInfoProvider = null;
//
//        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
//        options.messageNotifierCustomization = null;
//
//        return options;
//    }
//
//    private boolean inMainProcess() {
//        String packageName = getPackageName();
//        String processName = SystemUtil.getProcessName(this);
//        return packageName.equals(processName);
//    }
//
//    private void initLog() {
//        String path = StorageUtil.getDirectoryByDirType(StorageType.TYPE_LOG);
//        LogUtil.init(path, Log.DEBUG);
//        LogUtil.i("demo", FlavorDependent.getInstance().getFlavorName() + " demo log path=" + path);
//    }
}

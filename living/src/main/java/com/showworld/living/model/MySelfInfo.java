package com.showworld.living.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.showworld.living.utils.Constants;
import com.showworld.living.utils.SwlLog;

/**
 * 自己的状态数据
 */
public class MySelfInfo {
    private static final String TAG = MySelfInfo.class.getSimpleName();
    private String id;
    private String userSig;
    private String nickName;    // 呢称
    private String avatar;      // 头像
    private String sign;      // 签名
    private String CosSig;

    private boolean bLiveAnimator;  // 渐隐动画
    private SwlLog.SwlLogLevel logLevel;           // 日志等级


    private int id_status;

    private int myRoomNum = -1;

    private static MySelfInfo ourInstance = new MySelfInfo();

    public static MySelfInfo getInstance() {

        return ourInstance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getMyRoomNum() {
        return myRoomNum;
    }

    public void setMyRoomNum(int myRoomNum) {
        this.myRoomNum = myRoomNum;
    }

    public String getCosSig() {
        return CosSig;
    }

    public void setCosSig(String cosSig) {
        CosSig = cosSig;
    }

    public boolean isbLiveAnimator() {
        return bLiveAnimator;
    }

    public void setbLiveAnimator(boolean bLiveAnimator) {
        this.bLiveAnimator = bLiveAnimator;
    }

    public SwlLog.SwlLogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(SwlLog.SwlLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void writeToCache(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.USER_INFO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.USER_ID, id);
        editor.putString(Constants.USER_SIG, userSig);
        editor.putString(Constants.USER_NICK, nickName);
        editor.putString(Constants.USER_AVATAR, avatar);
        editor.putString(Constants.USER_SIGN, sign);
        editor.putInt(Constants.USER_ROOM_NUM, myRoomNum);
        editor.putBoolean(Constants.LIVE_ANIMATOR, bLiveAnimator);
        editor.putInt(Constants.LOG_LEVEL, logLevel.ordinal());
        editor.apply();
    }

    public void clearCache(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.USER_INFO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    public void getCache(Context context) {
        SharedPreferences sharedata = context.getSharedPreferences(Constants.USER_INFO, 0);
        id = sharedata.getString(Constants.USER_ID, null);
        userSig = sharedata.getString(Constants.USER_SIG, null);
        myRoomNum = sharedata.getInt(Constants.USER_ROOM_NUM, -1);
        nickName = sharedata.getString(Constants.USER_NICK, null);
        avatar = sharedata.getString(Constants.USER_AVATAR, null);
        sign = sharedata.getString(Constants.USER_SIGN, null);
        bLiveAnimator = sharedata.getBoolean(Constants.LIVE_ANIMATOR, false);
        int level = sharedata.getInt(Constants.LOG_LEVEL, SwlLog.SwlLogLevel.INFO.ordinal());
        if (level < SwlLog.SwlLogLevel.OFF.ordinal() || level > SwlLog.SwlLogLevel.INFO.ordinal()) {
            logLevel = SwlLog.SwlLogLevel.INFO;
        } else {
            logLevel = SwlLog.SwlLogLevel.values()[level];
        }
        SwlLog.setLogLevel(logLevel);
        SwlLog.i(TAG, " getCache id: " + id);
    }

    public int getIdStatus() {
        return id_status;
    }

    public void setIdStatus(int id_status) {
        this.id_status = id_status;
    }

    /**
     * 判断是否需要登录
     *
     * @return true 代表需要重新登录
     */
    public boolean needLogin() {
        if (getId() != null) {
            return false;//有账号不需要登录
        } else {
            return true;//需要登录
        }

    }
}
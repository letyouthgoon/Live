package com.showworld.living.avcontrollers;

import android.content.Context;
import android.view.View;

import com.showworld.living.utils.SwlLog;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVContext;
import com.tencent.av.sdk.AVRoom;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;
import com.showworld.living.utils.Constants;

import java.util.ArrayList;

/**
 * AVSDK 总控制器类
 */
public class SwlavsdkControl {
    private static final String TAG = "SwlavsdkControl";
    private AVContextControl mAVContextControl = null;
    private AVUIControl mAVUIControl = null;
    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static SwlavsdkControl instance = null;
    private static Context context;

    public static SwlavsdkControl getInstance() {
        if (instance == null) {
            instance = new SwlavsdkControl(context);
        }
        return instance;
    }


    public ArrayList<String> getmRemoteVideoIds() {
        return mRemoteVideoIds;
    }

    private ArrayList<String> mRemoteVideoIds = new ArrayList<String>();


    public static void initQavsdk(Context context) {
        SwlavsdkControl.context = context;
    }


    private SwlavsdkControl(Context context) {
        mAVContextControl = new AVContextControl(context);
        SwlLog.d(TAG, "WL_DEBUG SwlavsdkControl");
    }

    public void removeRemoteVideoMembers(String id) {
        if (mRemoteVideoIds.contains(id))
            mRemoteVideoIds.remove(id);
    }

    public void clearVideoMembers() {
        mRemoteVideoIds.clear();
    }


    /**
     * 启动SDK系统
     */
    public int startContext() {
        if (mAVContextControl == null)
            return Constants.DEMO_ERROR_NULL_POINTER;
        return mAVContextControl.startContext();
    }

    /**
     * 设置AVSDK参数
     *
     * @param appid
     * @param accountype
     * @param identifier
     * @param usersig
     */
    public void setAvConfig(int appid, String accountype, String identifier, String usersig) {
        if (mAVContextControl == null)
            return;
        mAVContextControl.setAVConfig(appid, accountype, identifier, usersig);
    }


    /**
     * 关闭SDK系统
     */
    public void stopContext() {
        if (mAVContextControl != null) {
            mAVContextControl.stopContext();
        }
    }

    public String getSelfIdentifier() {
        if (mAVContextControl == null)
            return null;
        return mAVContextControl.getSelfIdentifier();
    }

    public AVRoom getRoom() {
        AVContext avContext = getAVContext();

        return avContext != null ? avContext.getRoom() : null;
    }

    public void setMirror(boolean isMirror) {
        SwlLog.d(TAG, "setMirror SelfIdentifier:" + getSelfIdentifier() + "/" + isMirror);

        if (null != mAVUIControl) {
            mAVUIControl.setMirror(isMirror, getSelfIdentifier());
        }
    }

    public AVContext getAVContext() {
        if (mAVContextControl == null)
            return null;
        return mAVContextControl.getAVContext();
    }

    /**
     * 初始化UI层
     *
     * @param context
     * @param view    AVSDK UILayer层
     */
    public void initAvUILayer(Context context, View view) {
        mAVUIControl = new AVUIControl(context, view);
    }

    public void onResume() {
        if (mAVUIControl != null) {
            mAVUIControl.onResume();
        }
    }

    public void onPause() {
        if (null != mAVUIControl) {
            mAVUIControl.onPause();
        }
    }

    public void onDestroy() {
        if (null != mAVUIControl) {
            mAVUIControl.onDestroy();
            mAVUIControl = null;
        }
    }


    public void setLocalHasVideo(boolean isLocalHasVideo, String selfIdentifier) {
        if (null != mAVUIControl) {
            mAVUIControl.setLocalHasVideo(isLocalHasVideo, false, selfIdentifier);
        }
    }

    public void setRemoteHasVideo(boolean isRemoteHasVideo, String identifier, int videoSrcType) {
        SwlLog.i(TAG, "setRemoteHasVideo : " + identifier);
        if (null != mAVUIControl) {
            mAVUIControl.setSmallVideoViewLayout(isRemoteHasVideo, identifier, videoSrcType);
        }
    }

    public void setSelfId(String key) {
        if (null != mAVUIControl) {

            mAVUIControl.setSelfId(key);
        }
    }

    public void setRotation(int rotation) {
        if (mAVUIControl != null) {
            mAVUIControl.setRotation(rotation);
        }
    }

    public int getAvailableViewIndex(int start) {
        if (null != mAVUIControl) {
            return mAVUIControl.getIdleViewIndex(start);
        }
        return -1;
    }

    public void closeMemberView(String id) {
        if (null != mAVUIControl) {
            removeRemoteVideoMembers(id);
            mAVUIControl.closeMemberVideoView(id);
        }
    }

    public boolean containIdView(String id) {
        if (null != mAVUIControl) {
            if (mAVUIControl.getViewIndexById(id, AVView.VIDEO_SRC_TYPE_CAMERA) != -1)
                return true;
        }
        return false;
    }

    public String getAudioQualityTips() {
        AVAudioCtrl avAudioCtrl;
        if (SwlavsdkControl.getInstance() != null && SwlavsdkControl.getInstance().getAVContext() != null) {
            avAudioCtrl = getAVContext().getAudioCtrl();
            return avAudioCtrl.getQualityTips();
        }

        return "";
    }

    public String getVideoQualityTips() {
        if (SwlavsdkControl.getInstance() != null) {
            AVVideoCtrl avVideoCtrl = SwlavsdkControl.getInstance().getAVContext().getVideoCtrl();
            return avVideoCtrl.getQualityTips();
        }
        return "";
    }


    public String getQualityTips() {
        SwlavsdkControl qavsdk = SwlavsdkControl.getInstance();
        String audioQos = "";
        String videoQos = "";
        String roomQos = "";

        if (qavsdk != null) {
            audioQos = getAudioQualityTips();

            videoQos = getVideoQualityTips();

            if (qavsdk.getRoom() != null) {
                roomQos = qavsdk.getRoom().getQualityTips();
            }
        }

        if (audioQos != null && videoQos != null && roomQos != null) {
            return audioQos + videoQos + roomQos;
        } else {
            return "";
        }

    }


}
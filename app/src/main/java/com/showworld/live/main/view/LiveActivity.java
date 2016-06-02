package com.showworld.live.main.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.showworld.live.R;
import com.showworld.live.SWLApplication;
import com.showworld.live.base.ui.TActivity;
import com.showworld.live.base.util.NetworkUtil;
import com.showworld.live.main.Constants;
import com.showworld.live.main.control.QavsdkControl;
import com.tencent.av.sdk.AVConstants;
import com.tencent.av.sdk.AVError;
import com.tencent.open.utils.Util;

import java.util.Timer;

/**
 * Created by alex on 2016/6/1.
 * 直播界面
 */
public class LiveActivity extends TActivity {

    private static final String TAG = "AvActivity";
    private SWLApplication mQavsdkApplication;
    private QavsdkControl mQavsdkControl;
    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            Log.e(TAG, "WL_DEBUG netinfo mobile = " + mobileInfo.isConnected() + ", wifi = " + wifiInfo.isConnected());

            int netType = NetworkUtil.getNetWorkType(LiveActivity.this);
            Log.e(TAG, "WL_DEBUG connectionReceiver getNetWorkType = " + netType);
            mQavsdkControl.setNetType(netType);

            if (!mobileInfo.isConnected() && !wifiInfo.isConnected()) {
                Log.e(TAG, "WL_DEBUG connectionReceiver no network = ");
                // unconnect network
                // 暂时不关闭
//				if (ctx instanceof Activity) {
//					Toast.makeText(getApplicationContext(), ctx.getString(R.string.notify_no_network), Toast.LENGTH_SHORT).show();
//					((Activity)ctx).finish();
//				}
            } else {
                // connect network
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive action = " + action);
            if (action.equals(Constants.ACTION_SURFACE_CREATED)) {
                locateCameraPreview();
                wakeLock.acquire();
//                mQavsdkControl.toggleEnableCamera();
                if (mSelfUserInfo.isCreater() == true) {
                    initTIMGroup();
                    mEditTextInputMsg.setClickable(true);
                    mIsSuccess = true;
                    mVideoTimer = new Timer(true);
                    mVideoTimerTask = new VideoTimerTask();
                    mVideoTimer.schedule(mVideoTimerTask, 1000, 1000);
                    mQavsdkControl.toggleEnableCamera();
                    boolean isEnable = mQavsdkControl.getIsEnableCamera();
                    refreshCameraUI();
                    if (mOnOffCameraErrorCode != AVError.AV_OK) {
                        showDialog(isEnable ? DIALOG_OFF_CAMERA_FAILED : DIALOG_ON_CAMERA_FAILED);
                        mQavsdkControl.setIsInOnOffCamera(false);
                        refreshCameraUI();
                    }
//                    Log.d(TAG, "getMemberInfo isHandleMemberRoomSuccess " + mQavsdkApplication.isHandleMemberRoomSuccess());

//                    if (mQavsdkApplication.isHandleMemberRoomSuccess()) {
//                        Log.d(TAG, "getMemberInfo isHandleMemberRoomSuccess " + mQavsdkApplication.isHandleMemberRoomSuccess() + " yes  do it normally ");
                    mHandler.sendEmptyMessageDelayed(GET_ROOM_INFO, 0);
//                    } else {
//                        Log.w(TAG, "getMemberInfo isHandleMemberRoomSuccess " + mQavsdkApplication.isHandleMemberRoomSuccess() + " no wait for call");
//                    }
                    mQavsdkControl.setRequestCount(0);
                    //上报主播心跳
                    mHeartClickTimer.schedule(mHeartClickTask, 1000, 10000);
                } else {

                    hostRequestView(mHostIdentifier);
                }
            } else if (action.equals(Util.ACTION_VIDEO_CLOSE)) {
                String identifier = intent.getStringExtra(Util.EXTRA_IDENTIFIER);
                if (!TextUtils.isEmpty(mRecvIdentifier)) {
                    mQavsdkControl.setRemoteHasVideo(false, mRecvIdentifier, 0);
                }

                mRecvIdentifier = identifier;
            } else if (action.equals(Util.ACTION_VIDEO_SHOW)) {
                //成员模式加入视频聊天室
                String identifier = intent.getStringExtra(Util.EXTRA_IDENTIFIER);
                Log.d(TAG, "onReceive ACTION_VIDEO_SHOW  id " + identifier);
                mRecvIdentifier = identifier;
                mQavsdkControl.setRemoteHasVideo(true, mRecvIdentifier, 0);
                //IMSDk 加入聊天室
                joinGroup();
                initTIMGroup();
                mIsSuccess = true;
                mEditTextInputMsg.setClickable(true);
                //获取群组成员信息
                getMemberInfo();
                //发消息通知大家 自己上线了
                onMemberEnter();
                Util.switchWaitingDialog(ctx, mDialogInit, DIALOG_INIT, false);
            } else if (action.equals(Util.ACTION_ENABLE_CAMERA_COMPLETE)) {
                Log.d(TAG, "onClick ACTION_ENABLE_CAMERA_COMPLETE   " + " status " + mQavsdkControl.getIsEnableCamera());
                //自己是主播才本地渲染摄像头界面

                boolean isbeauty = mQavsdkControl.getAVContext().getVideoCtrl().enableBeauty(true);
                //如果具备美颜能力 显示美颜接口
                if (isbeauty == true)
                    mButtonBeauty.setVisibility(View.VISIBLE);

                if (mSelfUserInfo.isCreater() == true) {
                    refreshCameraUI();
                    mOnOffCameraErrorCode = intent.getIntExtra(Util.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
                    boolean isEnable = intent.getBooleanExtra(Util.EXTRA_IS_ENABLE, false);

                    if (mOnOffCameraErrorCode == AVError.AV_OK) {
                        if (!mIsPaused) {
                            Log.d(TAG, "ACTION_ENABLE_CAMERA_COMPLETE mHostIdentifier " + mHostIdentifier);
                            if (!mHostIdentifier.startsWith("86-")) {
                                mHostIdentifier = "86-" + mHostIdentifier;
                            }
                            mQavsdkControl.setSelfId(mHostIdentifier);
                            mQavsdkControl.setLocalHasVideo(isEnable, mHostIdentifier);
//                            startDefaultRecord();
                        }
                    } else {
                        showDialog(isEnable ? DIALOG_ON_CAMERA_FAILED : DIALOG_OFF_CAMERA_FAILED);
                    }

                    if (currentCameraIsFront == false) {
                        Log.d(TAG, " onSwitchCamera!!ACTION_ENABLE_CAMERA_COMPLETE and lastTime is backCamera :  " + mQavsdkControl.getIsInOnOffCamera());
                        onSwitchCamera();
                    }
                }
            } else if (action.equals(Util.ACTION_SWITCH_CAMERA_COMPLETE)) {
                Log.d(TAG, " onSwitchCamera!! ACTION_SWITCH_CAMERA_COMPLETE  " + mQavsdkControl.getIsInOnOffCamera());
                refreshCameraUI();

                mSwitchCameraErrorCode = intent.getIntExtra(Util.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
                boolean isFront = intent.getBooleanExtra(Util.EXTRA_IS_FRONT, false);
                if (mSwitchCameraErrorCode != AVError.AV_OK) {
                    showDialog(isFront ? DIALOG_SWITCH_FRONT_CAMERA_FAILED : DIALOG_SWITCH_BACK_CAMERA_FAILED);
                } else {
                    currentCameraIsFront = mQavsdkControl.getIsFrontCamera();
                    Log.d(TAG, "onSwitchCamera  " + currentCameraIsFront);
                }
            } else if (action.equals(Util.ACTION_MEMBER_CHANGE)) {

            } else if (action.equals(Util.ACTION_INSERT_ROOM_TO_SERVER_COMPLETE)) {
                Log.w(TAG, "getMemberInfo isHandleMemberRoomSuccess " + mQavsdkApplication.isHandleMemberRoomSuccess() + " now is time ");
                mHandler.sendEmptyMessageDelayed(GET_ROOM_INFO, 0);
            } else if (action.equals(Util.ACTION_INVITE_MEMBER_VIDEOCHAT)) {
                //发起邀请消息
                selectIdentier = intent.getStringExtra(Util.EXTRA_IDENTIFIER);
                Log.d(TAG, "onReceive inviteVC selectIdentier " + selectIdentier);

                if (viewIndex != null) {
                    String id;
                    if (selectIdentier.startsWith("86-")) {
                        id = selectIdentier.substring(3);

                    } else {
                        id = selectIdentier;
                    }
                    if (viewIndex.containsKey(id)) {
                        Toast.makeText(AvActivity.this, "you can't allowed to invite the same people", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                //开始邀请信息
                sendMaskViewStatus(selectIdentier);
                sendVCInvitation(selectIdentier);

            } else if (action.equals(Util.ACTION_MEMBER_VIDEO_SHOW)) {
                String identifier = intent.getStringExtra(Util.EXTRA_IDENTIFIER);
                mRecvIdentifier = identifier;
                //不在这个位置
//                int viewindex = viewIndex.get(identifier.substring(3));
                //第一个位置
                int locactionIndex = mQavsdkControl.getSmallVideoView();
                mMemberVideoCount = locactionIndex;
                Log.d(TAG, "onReceive ACTION_VIDEO_SHOW  id " + identifier + " viewindex " + locactionIndex);
                mQavsdkControl.setRemoteHasVideo(true, mRecvIdentifier, locactionIndex);

            } else if (action.equals(Util.ACTION_SHOW_VIDEO_MEMBER_INFO)) {
                String identifier = intent.getStringExtra(Util.EXTRA_IDENTIFIER);
                showVideoMemberInfo(identifier);
            } else if (action.equals(Util.ACTION_CLOSE_MEMBER_VIDEOCHAT)) {
                String identifier = intent.getStringExtra(Util.EXTRA_IDENTIFIER);
                closeVideoMemberByHost(identifier);
            } else if (action.equals(Util.ACTION_CLOSE_ROOM_COMPLETE)) {
                closeActivity();
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live);
        registerBroadcastReceiver();
        mQavsdkApplication = (SWLApplication) getApplication();
        mQavsdkControl = mQavsdkApplication.getQavsdkControl();
        if (NetworkUtil.getNetWorkType(this) != AVConstants.NETTYPE_NONE) {
            mQavsdkControl.setNetType(NetworkUtil.getNetType(this));
        }
        if (mQavsdkControl.getAVContext() != null) {
            mQavsdkControl.onCreate((SWLApplication) getApplication(), findViewById(android.R.id.content));
        } else {
            finish();
        }

    }


    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_SURFACE_CREATED);
        intentFilter.addAction(Constants.ACTION_VIDEO_SHOW);
        intentFilter.addAction(Constants.ACTION_MEMBER_VIDEO_SHOW);
        intentFilter.addAction(Constants.ACTION_VIDEO_CLOSE);
        intentFilter.addAction(Constants.ACTION_ENABLE_CAMERA_COMPLETE);
        intentFilter.addAction(Constants.ACTION_SWITCH_CAMERA_COMPLETE);
        intentFilter.addAction(Constants.ACTION_INSERT_ROOM_TO_SERVER_COMPLETE);
        intentFilter.addAction(Constants.ACTION_INVITE_MEMBER_VIDEOCHAT);
        intentFilter.addAction(Constants.ACTION_MEMBER_CHANGE);
        intentFilter.addAction(Constants.ACTION_SHOW_VIDEO_MEMBER_INFO);
        intentFilter.addAction(Constants.ACTION_CLOSE_MEMBER_VIDEOCHAT);
        intentFilter.addAction(Constants.ACTION_CLOSE_ROOM_COMPLETE);
        registerReceiver(mBroadcastReceiver, intentFilter);

        IntentFilter netIntentFilter = new IntentFilter();
        netIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, netIntentFilter);
    }
}

package com.showworld.live.main.control;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.showworld.live.SWLApplication;
import com.showworld.live.main.Constants;
import com.showworld.live.main.module.MemberInfo;
import com.showworld.live.main.module.UserInfo;
import com.tencent.av.sdk.AVContext;
import com.tencent.av.sdk.AVEndpoint;
import com.tencent.av.sdk.AVError;
import com.tencent.av.sdk.AVRoom;
import com.tencent.av.sdk.AVRoomMulti;

import java.util.ArrayList;

class AVRoomControl {
    private static final int TYPE_MEMBER_CHANGE_IN = 0;
    private static final int TYPE_MEMBER_CHANGE_OUT = TYPE_MEMBER_CHANGE_IN + 1;
    private static final int TYPE_MEMBER_CHANGE_UPDATE = TYPE_MEMBER_CHANGE_OUT + 1;
    private static final String TAG = "AVRoomControl";
    private boolean mIsInCreateRoom = false;
    private boolean mIsInCloseRoom = false;
    private Context mContext;
    private ArrayList<MemberInfo> mMemberList = new ArrayList<MemberInfo>();
    private int audioCat = Constants.AUDIO_VOICE_CHAT_MODE;

    public void setAudioCat(int audioCat) {
        this.audioCat = audioCat;
    }

    private AVRoomMulti.Delegate mRoomDelegate = new AVRoomMulti.Delegate() {


        // 创建房间成功回调
        public void onEnterRoomComplete(int result) {
            Log.d(TAG, "WL_DEBUG mRoomDelegate.onEnterRoomComplete result = " + result);
            mIsInCreateRoom = false;
            mContext.sendBroadcast(new Intent(Constants.ACTION_ROOM_CREATE_COMPLETE).putExtra(Constants.EXTRA_AV_ERROR_RESULT, result));
        }

        // 离开房间成功回调
        public void onExitRoomComplete(int result) {
            Log.d(TAG, "WL_DEBUG mRoomDelegate.onExitRoomComplete result = " + result);
            mIsInCloseRoom = false;
            mMemberList.clear();
            mContext.sendBroadcast(new Intent(Constants.ACTION_CLOSE_ROOM_COMPLETE));
        }

        @Override
        public void onEndpointsUpdateInfo(int i, String[] strings) {

        }

        public void OnPrivilegeDiffNotify(int privilege) {
            Log.d(TAG, "OnPrivilegeDiffNotify. privilege = " + privilege);
        }
    };

    AVRoomControl(Context context) {
        mContext = context;
    }


    private void onMemberChange(int type, AVEndpoint endpointList[], int endpointCount) {

        mContext.sendBroadcast(new Intent(Constants.ACTION_MEMBER_CHANGE));
    }


    /**
     * 创建房间
     *
     * @param relationId 讨论组号
     */
    void enterRoom(int relationId) {
        Log.d(TAG, "WL_DEBUG enterRoom relationId = " + relationId);
//		int roomType = AVRoom.AV_ROOM_MULTI;
//		int roomId = 0;
//		int authBufferSize = 0;//权限位加密串长度；TODO：请业务侧填上自己的加密串长度。
//		String controlRole = "";//角色名；多人房间专用。该角色名就是web端音视频参数配置工具所设置的角色名。TODO：请业务侧填根据自己的情况填上自己的角色名。
//		int audioCategory = audioCat;

        QavsdkControl qavsdk = ((SWLApplication) mContext).getQavsdkControl();
        AVContext avContext = qavsdk.getAVContext();
        long authBits = AVRoom.AUTH_BITS_DEFUALT;//权限位；默认值是拥有所有权限。TODO：请业务侧填根据自己的情况填上权限位。
        byte[] authBuffer = null;//权限位加密串；TODO：请业务侧填上自己的加密串。
        AVRoom.EnterRoomParam enterRoomParam = new AVRoomMulti.EnterRoomParam(relationId, authBits, authBuffer, "", audioCat, true);
        if (avContext == null) {
//            Toast.makeText(mContext, "avContext is null", Toast.LENGTH_SHORT);
            Log.e(TAG, "enterRoom avContext is null");
//            retryStartContext();
//            try {






//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            enterRoom(relationId);
            return;
        }
        avContext.enterRoom(AVRoom.AV_ROOM_MULTI, mRoomDelegate, enterRoomParam);
        Log.d(TAG, "enterRoom done !!!!");
//		AVRoom.Info roomInfo = new AVRoom.Info(roomType, roomId, relationId, AVRoom.AV_MODE_AUDIO, "", authBits, authBuffer, authBufferSize, audioCategory, controlRole);
//		// create room
//		avContext.enterRoom(mRoomDelegate, roomInfo);
        mIsInCreateRoom = true;
    }

    /**
     * 关闭房间
     */
    int exitRoom() {
        Log.d(TAG, "WL_DEBUG exitRoom");
        QavsdkControl qavsdk = ((SWLApplication) mContext).getQavsdkControl();
        AVContext avContext = qavsdk.getAVContext();
        int result = avContext.exitRoom();
        mIsInCloseRoom = true;

        return result;
    }

    /**
     * 获取成员列表
     *
     * @return 成员列表
     */
    ArrayList<MemberInfo> getMemberList() {
        return mMemberList;
    }

    boolean getIsInEnterRoom() {
        return mIsInCreateRoom;
    }

    boolean getIsInCloseRoom() {
        return mIsInCloseRoom;
    }

    public void setCreateRoomStatus(boolean status) {
        mIsInCreateRoom = status;
    }

    public void setCloseRoomStatus(boolean status) {
        mIsInCloseRoom = status;
    }

    public void setNetType(int netType) {
        QavsdkControl qavsdk = ((SWLApplication) mContext).getQavsdkControl();
        AVContext avContext = qavsdk.getAVContext();
        AVRoomMulti room = (AVRoomMulti) avContext.getRoom();
    }


    private int retryStartContext() {
        Log.w(TAG, "retryStartContext mLoginErrorCode   ");
        UserInfo mSelfUserInfo = ((SWLApplication) mContext).getMyselfUserInfo();
        String phone = ((SWLApplication) mContext).getMyselfUserInfo().getUserPhone();
        //if (mSelfUserInfo.getLoginType() == Util.TRUSTEESHIP)
        phone = "86-" + phone;
        if (mSelfUserInfo.getUsersig().equals("")) {
//                Toast.makeText(ProgramListActivity.this, "Shit Crash!!!!  ", Toast.LENGTH_LONG).show();
            return -1;
        }
        Log.e(TAG, "import phone: " + phone + "  Usersig   " + mSelfUserInfo.getUsersig());
        int mLoginErrorCode = ((SWLApplication) mContext).getQavsdkControl().startContext(
                phone, mSelfUserInfo.getUsersig());
        if (mLoginErrorCode != AVError.AV_OK) {
            return -1;
        }
        return 0;
    }

}